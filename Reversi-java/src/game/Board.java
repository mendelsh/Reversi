package game;

public class Board {

	private char[][] board = {	{'+','+','+','+','+','+','+','+'},
								{'+','+','+','+','+','+','+','+'},
								{'+','+','+','+','+','+','+','+'},
								{'+','+','+','w','b','+','+','+'},
								{'+','+','+','b','w','+','+','+'},
								{'+','+','+','+','+','+','+','+'},
								{'+','+','+','+','+','+','+','+'},
								{'+','+','+','+','+','+','+','+'}	};
	boolean whoseTurn = false; // false for BLACK and true for WHITE
	private int countWhite = 2, countBlack = 2;
	private int noValid = 0;
	//private String result;
	
	public Board() {}
	public Board(Board other) {
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				board[i][j] = other.board[i][j];
		this.whoseTurn = other.whoseTurn;
	}

	public String getResult() {
		if(!isGameFinished())
			return null;
		int w = getCountWhite(), b = getCountBlack();
		if(w == b) return "Draw";
		return w > b ? "White" : "Black";
	}
	
	public int getCountWhite() {return countWhite;}
	public int getCountBlack() {return countBlack;}
	public char[][] getBoard() {return board;}
	public char getBoard(int row, int col) {return board[row][col];}
	public void setBoard(char[][] board) {this.board = board;}
	public void setBoard(int row, int col) {
		board[row][col] = whoseTurn();
		}
	
	public void print() {
		for(char[] row : board) {
			for(char c : row)
				System.out.print(c + " ");
			System.out.println();
		}
	}
	
	private void incCount(char color) {
		if(color == 'w')
			countWhite++;
		else
			countBlack++;
	}
	private void decCount(char color) {
		if(color == 'w')
			countWhite--;
		else
			countBlack--;
	}
	
	public int[][] fintAllPieces(){
		int count = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++)
				if(move(row, col) == whoseTurn()) count++;
		int[][] moves = new int[count][2];
		count = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++)
				if(move(row, col) == whoseTurn()) {
					moves[count][0] = row;
					moves[count][1] = col;
					count++;
				}
		return moves;
	}
	
	public int[][] findAllMoves() {
		int count = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++)
				if(isValid(row, col)) count++;
		int[][] moves = new int[count][2];
		count = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++) 
				if(isValid(row, col)) {
					moves[count][0] = row;
					moves[count][1] = col;
					count++;
				}
		return moves;
	}
	
	public int howManyLegalMoves() {
		int count = 0;
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++)
				if(isValid(row, col)) count++;
		return count;
	}
	
	public int howManyMovesForOpponent() {
		whoseTurn = !whoseTurn;
		int sum = howManyLegalMoves();
		whoseTurn = !whoseTurn;
		return sum;
	}
	
	public boolean isValid(int row, int col) {
		if(row < 0 || row > 7 || col < 0 || col > 7) return false;
		if(isOccupied(move(row, col))) return false;
		
		if(col-1 > -1 && move(row, col-1) == whoseNotTurn()) 
			for(int i=2; col-i > -1; i++) {
				char c = move(row, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}	
		if(col+1 < 8 && move(row, col+1) == whoseNotTurn())
			for(int i=2; col+i < 8; i++) {
				char c = move(row, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		if(row-1 > -1 && move(row-1, col) == whoseNotTurn()) 
			for(int i=2; row-i > -1; i++) {
				char c = move(row-i, col);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		if(row+1 < 8 && move(row+1, col) == whoseNotTurn())
			for(int i=2; row+i < 8; i++) {
				char c = move(row+i, col);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		
		if(col-1 > -1 && row-1 > -1 && move(row-1, col-1) == whoseNotTurn())
			for(int i=2; col-i > -1 && row-i > -1; i++) {
				char c = move(row-i, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		if(col-1 > -1 && row+1 < 8 && move(row+1, col-1) == whoseNotTurn())
			for(int i=2; col-i > -1 && row+i < 8; i++) {
				char c = move(row+i, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		if(col+1 < 8 && row-1 > -1 && move(row-1, col+1) == whoseNotTurn())
			for(int i=2; col+i < 8 && row-i > -1; i++) {
				char c = move(row-i, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		if(col+1 < 8 && row+1 < 8 && move(row+1, col+1) == whoseNotTurn())
			for(int i=2; col+i < 8 && row+i < 8; i++) {
				char c = move(row+i, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) return true;
			}
		
		return false;
	}
	
	
	
	public boolean makeMove(int row, int col) {
		if(!isValid(row, col)) return false;
		setBoard(row, col);
		incCount(whoseTurn());
		
		if(col-1 > -1 && move(row, col-1) == whoseNotTurn()) 
			for(int i=2; col-i > -1; i++) {
				char c = move(row, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int to = col-i;
					for(int j=1; col-j > to; j++) {
						setBoard(row, col-j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(col+1 < 8 && move(row, col+1) == whoseNotTurn()) 
			for(int i=2; col+i < 8; i++) {
				char c = move(row, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int to = col+i;
					for(int j=1; col+j < to; j++) {
						setBoard(row, col+j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(row-1 > -1 && move(row-1, col) == whoseNotTurn()) 
			for(int i=2; row-i > -1; i++) {
				char c = move(row-i, col);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int to = row-i;
					for(int j=1; row-j > to; j++) {
						setBoard(row-j, col);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(row+1 < 8 && move(row+1, col) == whoseNotTurn()) 
			for(int i=2; row+i < 8; i++) {
				char c = move(row+i, col);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int to = row+i;
					for(int j=1; row+j < to; j++) {
						setBoard(row+j, col);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		
		if(col-1 > -1 && row-1 > -1 && move(row-1, col-1) == whoseNotTurn()) 
			for(int i=2; row-i > -1 && col-i > -1; i++) {
				char c = move(row-i, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int toRow = row-i, toCol = col-i;
					for(int j=1; row-j > toRow && col-j > toCol; j++) {
						setBoard(row-j, col-j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(col-1 > -1 && row+1 < 8 && move(row+1, col-1) == whoseNotTurn()) 
			for(int i=2; row+i < 8 && col-i > -1; i++) {
				char c = move(row+i, col-i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int toRow = row+i, toCol = col-i;
					for(int j=1; row+j < toRow && col-j > toCol; j++) {
						setBoard(row+j, col-j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(col+1 < 8 && row-1 > -1 && move(row-1, col+1) == whoseNotTurn()) 
			for(int i=2; row-i > -1 && col+i < 8; i++) {
				char c = move(row-i, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int toRow = row-i, toCol = col+i;
					for(int j=1; row-j > toRow && col+j < toCol; j++) {
						setBoard(row-j, col+j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		if(col+1 < 8 && row+1 < 8 && move(row+1, col+1) == whoseNotTurn()) 
			for(int i=2; row+i < 8 && col+i < 8; i++) {
				char c = move(row+i, col+i);
				if(!isOccupied(c)) break;
				if(c == whoseTurn()) {
					int toRow = row+i, toCol = col+i;
					for(int j=1; row+j < toRow && col+j < toCol; j++) {
						setBoard(row+j, col+j);
						incCount(whoseTurn());
						decCount(whoseNotTurn());
					}
					break;
				}	
			}
		
		whoseTurn = !whoseTurn;
		if(findAllMoves().length == 0) {
			noValid++;
			whoseTurn = !whoseTurn;
			if(findAllMoves().length == 0)
				noValid++;
		}
		else
			noValid = 0;
		if(countBlack == 0) countWhite = 64;
		else if(countWhite == 0) countBlack = 64;
		
		return true;
	}
	
	
	public char move(int row, int col) {
		return board[row][col];
	}
	public boolean isOccupied(char c) {
		return c != '+';
	}
	public char whoseNotTurn() {
		return whoseTurn ? 'b' : 'w';
	}
	public char whoseTurn() {
		return whoseTurn ? 'w' : 'b';
	}
	
	public boolean isGameFinished() {
		return noValid > 1;
	}
	
							
}
