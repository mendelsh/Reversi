package PengWin_Package;

import game.Board;

public class Bot_0 {

	public int[] botMove(Board b, char color) {
		
		int depth = 6;
		if(b.getCountBlack() + b.getCountWhite() >= 50)
			depth = 14;
		
		return botMove(b, color, depth);
//		if(color != b.whoseTurn() || b.isGameFinished()) return new int[] {-1, -1};
//		int[][] moves = b.findAllMoves();
//		int len = (int)(Math.random()*(moves.length));
//		return moves[len];
	}
	
	
	public int[] botMove(Board b, char color, int depth) {
	    if (color != b.whoseTurn() || b.isGameFinished()) return new int[] {-1, -1};
	    
	    int[][] moves = b.findAllMoves();
	    int[] bestMove = new int[] {-1, -1};
	    int bestValue = Integer.MIN_VALUE;
	    
	    for (int[] move : moves) {
	        Board newBoard = new Board(b);
	        newBoard.makeMove(move[0], move[1]);
	        
	        int moveValue = minimax(newBoard, depth - 1, false, color);
	        if (moveValue > bestValue) {
	            bestValue = moveValue;
	            bestMove = move;
	        }
	    }
	    return bestMove;
	}
	
	
	
	public int minimax(Board b, int depth, boolean maximizingPlayer, char color) {
		if(b.isGameFinished())
			return evalFinishGame(b, color);
	    if (depth == 0) {
	        return b.howManyLegalMoves() - b.howManyMovesForOpponent() + placedWell(b);
	    }
	    
	    int[][] moves = b.findAllMoves();
	    
	    if (maximizingPlayer) {
	        int maxEval = Integer.MIN_VALUE;
	        for (int[] move : moves) {
	            Board newBoard = new Board(b); // Create a copy of the board
	            newBoard.makeMove(move[0], move[1]);
	            int eval = minimax(newBoard, depth - 1, false, color);
	            maxEval = Math.max(maxEval, eval);
	        }
	        return maxEval;
	    } 
	    else {
	        int minEval = Integer.MAX_VALUE;
	        for (int[] move : moves) {
	            Board newBoard = new Board(b);
	            newBoard.makeMove(move[0], move[1]);
	            int eval = minimax(newBoard, depth - 1, false, color);
	            minEval = Math.min(minEval, eval);
	            
	        }
	        return minEval;
	    }
	}
	
	public int evalFinishGame(Board b, char color) {
		if(!b.isGameFinished()) return 0;
		if(b.getCountBlack() - b.getCountWhite() == 0)
			return 64;
		else if(b.getCountBlack() - b.getCountWhite() > 0) {
			if(color == 'b')
				return b.getCountBlack() - b.getCountWhite() + 64;
			return b.getCountWhite() - b.getCountBlack() - 64;
		}
		else {
			if(color == 'b')
				return b.getCountBlack() - b.getCountWhite() - 64;
			return b.getCountWhite() - b.getCountBlack() + 64;
		}
	}
	
	
	public int sumPositions(Board b, int depth) {
	    if (depth == 0) return b.howManyLegalMoves();
	    int sum = 0;
	    int[][] moves = b.findAllMoves();

	    for (int[] move : moves) {
	        Board newBoard = new Board(b); // Create a new instance of the Board class
	        newBoard.makeMove(move[0], move[1]);
//	        newBoard.print();
	        sum += sumPositions(newBoard, depth - 1);
	    }

	    return sum;
	}
	
	public boolean isCorner(int move0, int move1) {
		if(move0 == 0 && move1 == 0 || move0 == 0 && move1 == 7 ||
				move0 == 7 && move1 == 0 || move0 == 7 && move1 == 7)
			return true;
		return false;
	}
	
	public boolean isNearCorner(int move0, int move1) {
		if(isCorner(move0, move1) || isCorner(move0, move1-1) ||
				isCorner(move0-1, move1) || isCorner(move0-1, move1-1) ||
				isCorner(move0-1, move1+1) || isCorner(move0, move1+1) ||
				isCorner(move0+1, move1) || isCorner(move0+1, move1+1) ||
				isCorner(move0+1, move1-1)) return true;
		return false;
	}
	
	public int placedWell(Board b) {
		int sum = 0;
		char c = b.whoseTurn();
		
		if(b.move(0, 0) == c) {
			sum += 3;
			for(int i=1; i<8; i++) {
				if(b.move(0, i) == c)
					sum += 2;
				else break;
			}
			for(int i=1; i<8; i++) {
				if(b.move(i, 0) == c)
					sum += 2;
				else break;
			}
		}
		
		if(b.move(0, 7) == c) {
			sum += 3;
			for(int i=1; i<8; i++) {
				if(b.move(0, 7-i) == c)
					sum += 2;
				else break;
			}
			for(int i=1; i<8; i++) {
				if(b.move(i, 0) == c)
					sum += 2;
				else break;
			}
		}
		
		if(b.move(7, 0) == c) {
			sum += 3;
			for(int i=1; i<8; i++) {
				if(b.move(7, i) == c)
					sum += 2;
				else break;
			}
			for(int i=1; i<8; i++) {
				if(b.move(7-i, 0) == c)
					sum += 2;
				else break;
			}
		}
		
		if(b.move(7, 7) == c) {
			sum += 3;
			for(int i=1; i<8; i++) {
				if(b.move(7, 7-i) == c)
					sum += 2;
				else break;
			}
			for(int i=1; i<8; i++) {
				if(b.move(7-i, 7) == c)
					sum += 2;
				else break;
			}
		}
		
		return sum*2;
	}
	
}
