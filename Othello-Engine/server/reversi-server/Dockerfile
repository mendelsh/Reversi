FROM python:3.11-slim

WORKDIR /app

# Copy both server/test.py and py_module
COPY py_module ./py_module
COPY server/reversi-server/server.py ./server.py

RUN pip install fastapi uvicorn

CMD ["python", "-u", "-m", "uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000"]