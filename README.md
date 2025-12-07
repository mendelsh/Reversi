# Reversi

A Java implementation of the classic Reversi (Othello) board game with a graphical interface and AI bot integration.

## Overview

This project consists of a complete Reversi game implementation featuring:
- **Java Client Engine** (`Board.java`) - Core game logic and state management
- **Bot_01** - HTTP-based AI bot that communicates with an external engine server
- **Visual Board** - Swing-based graphical user interface for interactive gameplay

## Project Structure

```
Reversi-java/
├── src/
│   ├── game/
│   │   └── Board.java          # Core game engine
│   ├── PengWin_Package/
│   │   ├── Bot_01.java         # HTTP-based AI bot
│   │   └── Bot_0.java          # Minimax-based bot (legacy)
│   ├── VisualBoard.java         # GUI interface
│   └── Main.java                # Entry point
└── pieces/                      # Game piece images
    ├── new-moon.png             # Black piece icon
    └── rec.png                  # White piece icon
```

## Core Components

### Java Client Engine (`Board.java`)

The `Board` class serves as the core game engine, managing all game state and logic.

#### Key Features:
- **8x8 Board Representation**: Uses a 2D character array (`'+'` for empty, `'b'` for black, `'w'` for white)
- **Turn Management**: Tracks whose turn it is (black/white) via boolean flag
- **Move Validation**: Comprehensive `isValid()` method checks all 8 directions (horizontal, vertical, diagonal)
- **Move Execution**: `makeMove()` method places pieces and flips opponent pieces in all valid directions
- **Game State Queries**: Methods to find all legal moves, check game completion, and get piece counts

#### Main Methods:
- `makeMove(int row, int col)` - Executes a move at the specified position, flipping pieces as needed
- `isValid(int row, int col)` - Validates if a move is legal by checking all 8 directions
- `findAllMoves()` - Returns array of all valid moves for the current player
- `isGameFinished()` - Determines if the game has ended (no valid moves for either player)
- `getCountWhite()` / `getCountBlack()` - Returns current piece counts
- `getResult()` - Returns game result ("White", "Black", or "Draw")

#### Board State:
- Initial board setup with standard Reversi starting position (2x2 center pieces)
- Automatic turn switching after valid moves
- Handles edge cases: no valid moves, game completion, piece elimination

### Bot_01 (`PengWin_Package/Bot_01.java`)

An advanced AI bot that communicates with an external HTTP server to get optimal moves.

#### Architecture:
- **HTTP Client**: Connects to an external HTTP server endpoint via POST requests
- **Bitboard Encoding**: Converts the Java board representation into a compact binary format
  - 8 bytes for black pieces (one byte per row, bit per column)
  - 8 bytes for white pieces
  - 1 byte for current turn (1 = black, 0 = white)
  - Total payload: 17 bytes
- **JSON Response Parsing**: Receives and parses JSON containing `x`, `y`, and `score` fields

#### Key Components:
- `botMove(Board b, char color)` - Main entry point, returns `int[]` with `[row, col]` coordinates
- `setBitboardStruct(Board b)` - Converts board state to binary payload format
- `fetchEngineMove(Board b)` - Performs HTTP POST request to engine server
- `parseJson(String json)` - Extracts move coordinates from JSON response
- `Fetch` inner class - Handles HTTP response and error states
- `LastMove` inner class - Stores parsed move coordinates and score

#### Coordinate Transformation:
The bot performs coordinate inversion: `{7 - lastMove.y, 7 - lastMove.x}` to convert from server's coordinate system to Java's board representation.

#### Error Handling:
- Gracefully handles network failures, HTTP errors, and parsing issues
- Returns valid move coordinates even if fetch fails (uses last known move)

### Visual Board (`VisualBoard.java`)

A Swing-based graphical user interface providing an interactive game experience.

#### Features:
- **8x8 Grid Layout**: Visual representation of the game board using `JButton` components
- **Piece Rendering**: Custom icons for black (`new-moon.png`) and white (`rec.png`) pieces
- **Interactive Gameplay**: Click-based move input with validation
- **Score Display**: Real-time score tracking showing white and black piece counts
- **Color Selection**: Players can choose to play as black or white
- **Bot Integration**: Seamless integration with Bot_01 for AI opponent

#### UI Components:
- `JFrame` - Main application window (600x680 pixels)
- `JPanel` with `GridLayout(8, 8)` - Game board grid
- `JButton[][]` - Individual square buttons for each board position
- `JLabel` - Score display at the top
- Color selection buttons at the bottom

#### Asynchronous Bot Moves:
Uses `SwingWorker` to perform bot moves on a background thread, preventing UI freezing:
- `startBotLoop()` - Recursively handles bot turns
- Updates UI on EDT after bot move completes
- Handles consecutive bot moves when player has no valid moves

#### Visual Updates:
- `updateVisualBoard()` - Refreshes all button icons based on current board state
- `updateScoreLabel()` - Updates score display with current piece counts
- Automatic board refresh after each move

## How to Run

1. **Compile the project**:
   ```bash
   cd Reversi-java
   javac -d bin src/**/*.java
   ```

2. **Run the application**:
   ```bash
   java -cp bin Main
   ```

3. **Gameplay**:
   - Choose your color (Black or White) using the buttons at the bottom
   - Click on a valid square to make your move
   - The bot will automatically make its move after yours
   - The score updates in real-time at the top of the window

## Bot_01 Server Requirements

Bot_01 requires an external HTTP server that:
- Accepts POST requests with `Content-Type: application/octet-stream`
- Receives 17-byte binary payload (8 bytes black, 8 bytes white, 1 byte turn)
- Returns JSON response: `{"x": <int>, "y": <int>, "score": <int>}`

To use Bot_01, ensure the server is running and accessible. The server URL is configured in the `Bot_01` class.

## Game Rules

- Players take turns placing pieces on an 8x8 board
- A move is valid only if it flips at least one opponent piece
- Pieces are flipped in all 8 directions (horizontal, vertical, diagonal) where valid
- If a player has no valid moves, their turn is skipped
- The game ends when neither player has valid moves
- The player with the most pieces wins

## Technical Details

- **Language**: Java
- **GUI Framework**: Java Swing
- **Network Protocol**: HTTP POST with binary payload
- **Board Representation**: 2D character array
- **Coordinate System**: 0-indexed, row-major order (0-7 for both dimensions)