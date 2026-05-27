import os
import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from youtube_transcript_api import YouTubeTranscriptApi, RequestBlocked, IpBlocked, NoTranscriptFound, TranscriptsDisabled, VideoUnavailable, VideoUnplayable
from youtube_transcript_api.proxies import GenericProxyConfig

app = FastAPI()

WINDOWS_TAILSCALE_IP = os.getenv("WINDOWS_TAILSCALE_IP", "127.0.0.1").strip()
PROXY_PORT = os.getenv("PROXY_PORT", "8899").strip()

tailscale_proxy_config = GenericProxyConfig(
    http_url= f"http://{WINDOWS_TAILSCALE_IP}:{PROXY_PORT}",
    https_url= f"http://{WINDOWS_TAILSCALE_IP}:{PROXY_PORT}"
)

class RequestBody(BaseModel):
    videoId: str
    languageCode: str

def process_transcript_snippets(transcript_data):
    text = ""
    for snippet in transcript_data:
        text += snippet['text'] + " "
    return text.strip()

@app.post("/api/transcript")
async def fetch_transcript(request_body: RequestBody):
    try:
        ytt_api = YouTubeTranscriptApi(proxy_config=tailscale_proxy_config)

        transcript_list = ytt_api.list(request_body.videoId)

        transcript = transcript_list.find_transcript([request_body.languageCode])
        fetched_data = transcript.fetch()

        return {
            "text": process_transcript_snippets(fetched_data),
            "videoId": transcript.video_id,
            "languageCode": transcript.language_code,
            "language": transcript.language,
            "isGenerated": transcript.is_generated
        }

    except RequestBlocked:
        raise HTTPException(status_code=403, detail="Request blocked by YouTube. Please try again later.")
    except IpBlocked:
        raise HTTPException(status_code=500, detail="The IP transcript crawler service is blocked by YouTube. Please try again later.")
    except NoTranscriptFound:
        raise HTTPException(status_code=404, detail="No transcript found for the given video ID and language code.")
    except TranscriptsDisabled:
        raise HTTPException(status_code=403, detail="Transcripts are disabled for this video.")
    except VideoUnplayable:
        raise HTTPException(status_code=404, detail="The video is unplayable.")
    except VideoUnavailable:
        raise HTTPException(status_code=404, detail="The video is unavailable.")

    except requests.exceptions.ProxyError:
        raise HTTPException(status_code=503, detail="The residential proxy (Windows laptop) is offline or unreachable.")
    except requests.exceptions.ConnectTimeout:
        raise HTTPException(status_code=504, detail="Connection to the residential proxy timed out.")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))