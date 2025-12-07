# from . import board as cboard  # board.cpython-xxx.so

# othello/__init__.py
import importlib.util
import sys
from pathlib import Path

board_path = Path(__file__).parent / "board.cpython-312-x86_64-linux-gnu.so"
spec = importlib.util.spec_from_file_location("othello.board", board_path)
cboard = importlib.util.module_from_spec(spec)
sys.modules["othello.board"] = cboard
spec.loader.exec_module(cboard)

class Board:
    BLACK_TURN = True
    WHITE_TURN = False

    def __init__(self, black_bitboard=None, white_bitboard=None, turn=None):
        if black_bitboard is None:
            self._cap = cboard.create()
        else:
            self._cap = cboard.create_custom(
                black_bitboard,
                white_bitboard,
                int(bool(turn))
            )

    def display(self):
        cboard.display(self._cap)

    def play(self, x, y):
        return cboard.play(self._cap, x, y)

    @property
    def turn(self):
        return cboard.get_turn(self._cap)  # C function returns current turn

    def game_over(self):
        return cboard.game_over(self._cap)

    def count_discs(self):
        black, white = cboard.count_discs(self._cap)
        return black, white


class Move:
    def __init__(self, x, y, score=0):
        self.x = x
        self.y = y
        self.score = score


class Bot:
    @staticmethod
    def create(board, bot_type, side):
        # bot_type: "pengwin" or "greedy"
        cap = None
        if bot_type.lower() == "pengwin":
            cap = cboard.pengwin_create(board._cap, side)
        elif bot_type.lower() == "greedy":
            cap = cboard.greedy_create(board._cap, side)
        return Bot(cap)

    def __init__(self, cap):
        self._cap = cap

    def select_move(self, depth):
        x, y, score = cboard.bot_select_move(self._cap, depth)
        return Move(x, y, score)

