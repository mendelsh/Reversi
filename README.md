# Reversi / Othello Game Engine

A high-performance Reversi (Othello) game implementation with multiple components: a C-based game engine with bitboard optimization, Python bindings, a FastAPI server, and a Java GUI application.

## Project Structure

This repository contains two main implementations:

### 1. Othello-Engine (C/Python)
A high-performance game engine written in C with:
- **Bitboard representation**: Uses 64-bit integers for efficient board state management
- **Python bindings**: C extension module for easy Python integration
- **AI bots**: Multiple bot implementations (Pengwin, Greedy) using minimax with alpha-beta pruning
- **FastAPI server**: REST API for remote move calculation

### 2. Reversi-java
A Java-based GUI application featuring:
- **Visual board**: Swing-based graphical interface
- **Client-server integration**: Java bot that communicates with the C engine via HTTP
- **Interactive gameplay**: Play against AI bots with visual feedback

## Features

### Core Engine (`Othello-Engine/board/`)
- Bitboard-based board representation for optimal performance
- Efficient move generation and validation
- Disc flipping logic with bitwise operations
- Game state management (turn tracking, game over detection)
- Position counting utilities

### AI Bots (`Othello-Engine/bot/`)
- **Pengwin Bot**: Advanced evaluation function with strategic positioning
- **Greedy Bot**: Simpler evaluation focusing on immediate disc count
- Minimax algorithm with alpha-beta pruning
- Configurable search depth

### Python Module (`Othello-Engine/py_module/`)
- C extension module exposing board and bot functionality to Python
- Easy-to-use Python API:
  ```python
  from othello import Board, Bot
  
  board = Board()
  bot = Bot.create(board, "pengwin", Board.BLACK_TURN)
  move = bot.select_move(depth=6)
  board.play(move.x, move.y)
  ```

### FastAPI Server (`Othello-Engine/server/reversi-server/`)
- REST API endpoint: `POST /best_move`
- Accepts board state as binary payload (17 bytes: 8 bytes black bitboard, 8 bytes white bitboard, 1 byte turn)
- Returns JSON with best move coordinates and score
- Dockerized for easy deployment

### Java GUI (`Reversi-java/`)
- Visual board with Swing components
- Interactive gameplay
- Integration with remote C engine via HTTP
- Score tracking and game state display

## Building

### C Engine and Python Module

```bash
cd Othello-Engine/py_module
./cpbuild.sh
```

This will:
- Compile the C extension module
- Build the Python package
- Generate the `.so` file in the `othello/` directory

### FastAPI Server

```bash
cd Othello-Engine
docker build -t reversi_server_01 -f server/reversi-server/Dockerfile .
docker run -p 8000:8000 reversi_server_01
```

Or manually:
```bash
cd Othello-Engine
pip install fastapi uvicorn
python -m uvicorn server.reversi-server.server:app --host 0.0.0.0 --port 8000
```

### Java Application

```bash
cd Reversi-java
javac -d bin -sourcepath src src/Main.java
java -cp bin Main
```

## Usage

### Python API

```python
from othello import Board, Bot

# Create a new game
board = Board()

# Create bots
black_bot = Bot.create(board, "pengwin", Board.BLACK_TURN)
white_bot = Bot.create(board, "greedy", Board.WHITE_TURN)

# Play a game
while not board.game_over():
    bot = black_bot if board.turn == Board.BLACK_TURN else white_bot
    move = bot.select_move(depth=6)
    board.play(move.x, move.y)

# Display final board
board.display()
black, white = board.count_discs()
print(f"Black: {black}, White: {white}")
```

### FastAPI Server

The server accepts POST requests to `/best_move` with a 17-byte binary payload:
- Bytes 0-7: Black bitboard (little-endian)
- Bytes 8-15: White bitboard (little-endian)
- Byte 16: Turn (1 for black, 0 for white)

Response format:
```json
{"x": 3, "y": 2, "score": 42}
```

### Java GUI

Run the Java application to launch the visual board:
```bash
java -cp bin Main
```

The Java bot (`Bot_01`) can connect to the FastAPI server for move calculation. Update the URL in `Bot_01.java` if needed.

## Architecture

### Bitboard Representation
The engine uses a 64-bit integer to represent the board state:
- Each bit corresponds to a square on the 8x8 board
- Separate bitboards for black and white pieces
- Bitwise operations for efficient move generation and validation

### AI Algorithm
- **Search**: Minimax with alpha-beta pruning
- **Evaluation**: Position-based heuristics considering:
  - Disc count
  - Corner control
  - Mobility
  - Stability (for Pengwin bot)
- **Depth**: Configurable search depth (typically 6-10)

## Testing

### C Engine Tests
```bash
cd Othello-Engine/board
gcc -o test test.c board.c
./test

cd Othello-Engine/bot
gcc -o test test.c bot.c ../board/board.c
./test
```

### Python Tests
```bash
cd Othello-Engine/py_module
python test.py
```

## Dependencies

### C Engine
- C compiler (GCC/Clang)
- Standard C library

### Python Module
- Python 3.x
- setuptools
- C compiler for building extensions

### FastAPI Server
- Python 3.11+
- FastAPI
- Uvicorn
- The compiled Python module

### Java Application
- Java JDK 8+
- Java Swing (included in JDK)


