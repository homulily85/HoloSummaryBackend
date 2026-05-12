from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from youtube_transcript_api import YouTubeTranscriptApi

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
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))