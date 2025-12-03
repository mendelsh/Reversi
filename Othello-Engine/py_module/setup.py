from setuptools import setup, Extension

module = Extension(
    "othello.board",
    sources=["boardmodule.c", "../board/board.c"],
    include_dirs=["../board"],
)

setup(
    name="othello",
    version="1.0",
    packages=["othello"],
    ext_modules=[module],
)
