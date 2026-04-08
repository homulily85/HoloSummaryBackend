from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from youtube_transcript_api import YouTubeTranscriptApi, RequestBlocked, IpBlocked, NoTranscriptFound, TranscriptsDisabled, VideoUnavailable, VideoUnplayable
from typing import List, Dict
import math

app = FastAPI()


class RequestBody(BaseModel):
    video_id: str
    language_code: str


def merge_snippets_into_batches(snippets: List, batch_size: int = 300) -> List[Dict]:
    if not snippets:
        return []

    def _get_field(s, name, default=None):
        if isinstance(s, dict):
            return s.get(name, default)
        if hasattr(s, name):
            return getattr(s, name)
        alt = None
        if name == "start":
            alt = s.get("offset", default) if isinstance(s, dict) else None
        return default

    normalized = []
    for s in snippets:
        text = _get_field(s, 'text', '')
        start_raw = _get_field(s, 'start', None)
        duration_raw = _get_field(s, 'duration', 0.0)

        try:
            start = float(start_raw) if start_raw is not None else None
        except (TypeError, ValueError):
            raise ValueError("Each snippet must have numeric 'start' field; got: {}".format(start_raw))
        try:
            duration = float(duration_raw) if duration_raw is not None else 0.0
        except (TypeError, ValueError):
            raise ValueError("Each snippet must have numeric 'duration' field; got: {}".format(duration_raw))

        if start is None:
            raise ValueError("Each snippet must have a 'start' value; got None")

        normalized.append({
            'text': str(text).strip() if text is not None else '',
            'start': start,
            'end': start + duration
        })

    normalized.sort(key=lambda x: x['start'])

    windows = {}
    for it in normalized:
        window_index = int(math.floor(it['start'] / batch_size))
        windows.setdefault(window_index, []).append(it)

    batches = []
    for window_index in sorted(windows.keys()):
        items_sorted = sorted(windows[window_index], key=lambda x: x['start'])
        texts = [it['text'] for it in items_sorted if it['text']]
        batch_text = " ".join(texts).strip()
        batch_start = min(it['start'] for it in items_sorted)
        batch_end = max(it['end'] for it in items_sorted)
        batches.append({
            'text': batch_text,
            'start': batch_start,
            'end': batch_end
        })

    return batches

@app.post("/api/transcript")
async def fetch_transcript(request_body: RequestBody):
    try:
        ytt_api = YouTubeTranscriptApi()
        fetched_transcript = ytt_api.fetch(request_body.video_id, languages=[request_body.language_code])
        return {"snippets": merge_snippets_into_batches(fetched_transcript.snippets),
                "video_id": fetched_transcript.video_id,
                "language_code": fetched_transcript.language_code,
                "language": fetched_transcript.language,
                "is_generated": fetched_transcript.is_generated}

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
