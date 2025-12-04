from fastapp import FastApp
import sys
from pathlib import Path

MODULE_DIR = Path(__file__).parent / "py_module"
sys.path.append(str(MODULE_DIR))

from othello import Board, Bot

app = FastApp()

@app.get("/best-move/{bot_type}/{side}/{black_bitboard}/{white_bitboard}/{depth}")
async def best_move(bot_type: str, side: bool, black_bitboard: int, white_bitboard: int, depth: int):
    # Ensure turn (side) is bool — FastApp may give string ("true"/"false")
    side = bool(int(side)) if isinstance(side, str) else side

    # Create board
    board = Board(black_bitboard, white_bitboard, side)

    # Create bot
    bot = Bot.create(board, bot_type, side)

    # Select move
    move = bot.select_move(depth)

    return {"x": move.x, "y": move.y, "score": move.score}
