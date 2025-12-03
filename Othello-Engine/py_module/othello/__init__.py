from . import board as cboard

class Board:
    def __init__(self):
        self._cap = cboard.create()

    def display(self):
        cboard.display(self._cap)

    def play(self, x, y):
        return cboard.play(self._cap, x, y)
