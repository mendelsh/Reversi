from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

import sys
sys.path.append("/app/py_module")
from othello import Board

app = FastAPI()

@app.middleware("http")
async def log_requests(request, call_next):
    print(f"Request: {request.method} {request.url}")
    response = await call_next(request)
    return response


@app.post("/stuff")
async def stuff(request: Request):
    print("Received request for /stuff", flush=True)
    body = await request.body()  # read raw bytes
    if len(body) != 16:  # two 8-byte bitboards
        return JSONResponse({"error": "Expected 16 bytes"}, status_code=400)

    black_bytes = body[:8]
    white_bytes = body[8:]

    black_bb = int.from_bytes(black_bytes, byteorder='little')
    white_bb = int.from_bytes(white_bytes, byteorder='little')

    board = Board(black_bitboard=black_bb, white_bitboard=white_bb, turn=True)
    board.display()

    return JSONResponse({
        "black_bitboard": black_bb,
        "white_bitboard": white_bb
    })
