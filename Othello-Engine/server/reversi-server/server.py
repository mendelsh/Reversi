from fastapi import FastAPI, Request

import sys
sys.path.append("/app/py_module")
from othello import Board, Bot



app = FastAPI()

@app.post("/best_move")
async def best_move(req: Request):
    raw_body = await req.body()
    if len(raw_body) != 17:
        return {"error": "Expected 17 bytes"}
    
    black_bb = int.from_bytes(raw_body[0:8], byteorder='little')
    white_bb = int.from_bytes(raw_body[8:16], byteorder='little')
    side = bool(raw_body[16])
    depth = 6  # default depth

    print(' '.join(format(b, '08b') for b in raw_body[0:8]))
    print(' '.join(format(b, '08b') for b in raw_body[8:16]))

    print()
    bits_b = format(black_bb, '064b')
    bits_b = ' '.join(bits_b[i:i+8] for i in range(0, 64, 8))
    print(bits_b)

    bits_w = format(white_bb, '064b')
    bits_w = ' '.join(bits_w[i:i+8] for i in range(0, 64, 8))
    print(bits_w)
    print()

    board = Board(black_bitboard=black_bb, white_bitboard=white_bb, turn=side)
    # print("black")
    # Board(black_bitboard=black_bb, white_bitboard=0, turn=side).display()
    # print("white")
    # Board(black_bitboard=0, white_bitboard=white_bb, turn=side).display()
    # print("full")
    board.display()
    bot = Bot.create(board, "pengwin", side)
    move = bot.select_move(depth)

    res = f'{{"x": {move.x}, "y": {move.y}, "score": {move.score}}}'
    print(res, flush=True)
    return res
    # return {"x": move.x, "y": move.y, "score": move.score}


# @app.get("/best_move/{bot_type}/{side}/{black_bitboard}/{white_bitboard}/{depth}")
# async def best_move(bot_type: str, side: bool, black_bitboard: int, white_bitboard: int, depth: int):
#     # Ensure turn (side) is bool — FastApp may give string ("true"/"false")
#     side = bool(int(side)) if isinstance(side, str) else side

#     # Create board
#     board = Board(black_bitboard, white_bitboard, side)

#     # Create bot
#     bot = Bot.create(board, bot_type, side)

#     # Select move
#     move = bot.select_move(depth)

#     return {"x": move.x, "y": move.y, "score": move.score}
