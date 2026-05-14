from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from youtube_transcript_api import YouTubeTranscriptApi, RequestBlocked, IpBlocked, NoTranscriptFound, TranscriptsDisabled, VideoUnavailable, VideoUnplayable

app = FastAPI()


class RequestBody(BaseModel):
    videoId: str
    languageCode: str


def process_transcript_snippets(transcript):
    text = ""
    for snippet in transcript.snippets:
        text += snippet.text + " "
    return text.strip()


@app.post("/api/transcript")
async def fetch_transcript(request_body: RequestBody):
    try:
        ytt_api = YouTubeTranscriptApi()
        fetched_transcript = ytt_api.fetch(request_body.videoId, languages=[request_body.languageCode])
        return {"text": process_transcript_snippets(fetched_transcript),
                "videoId": fetched_transcript.video_id,
                "languageCode": fetched_transcript.language_code,
                "language": fetched_transcript.language,
                "isGenerated": fetched_transcript.is_generated}
    except RequestBlocked:
        raise HTTPException(status_code=403, detail="Request blocked by YouTube. Please try again later.")
    except IpBlocked:
        raise HTTPException(status_code=403, detail="IP blocked by YouTube. Please try again later.")
    except NoTranscriptFound:
        raise HTTPException(status_code=404, detail="No transcript found for the given video ID and language code.")
    except TranscriptsDisabled:
        raise HTTPException(status_code=403, detail="Transcripts are disabled for this video.")
    except VideoUnplayable:
        raise HTTPException(status_code=404, detail="The video is unplayable.")
    except VideoUnavailable:
        raise HTTPException(status_code=404, detail="The video is unavailable.")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))