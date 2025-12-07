from setuptools import setup, Extension

module = Extension(
    "othello.board",
    sources=["boardmodule.c", "../board/board.c", "../bot/bot.c"],
    include_dirs=["../board", "../bot"],
)

setup(
    name="othello",
    version="1.0",
    packages=["othello"],
    ext_modules=[module],
)
