FROM python:3.13.12-trixie

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["uvicorn", "crawler:app","--host", "0.0.0.0", "--port", "8000"]