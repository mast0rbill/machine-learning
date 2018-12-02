import java.util.ArrayList;

public class Board {

	public double[][] board = new double[3][3];

	public Board() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = 0;
			}
		}
	}

	public Board(Board b) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = b.board[y][x];
			}
		}
	}

	public Board(ArrayList<Double> boardPos) {
		int counter = 0;

		ArrayList<Double> compressed = new ArrayList<Double>();
		
		for(int i = 0; i < boardPos.size(); i += 3) {
			if(boardPos.get(i) == 1.0) {
				compressed.add(0.0);
			} else if(boardPos.get(i + 1) == 1.0) {
				compressed.add(1.0);
			} else if(boardPos.get(i + 2) == 1.0) {
				compressed.add(-1.0);
			}
		}
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = compressed.get(counter);
				counter++;
			}
		}
	}

	public void PrepareForRecurse() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = 10.0;
			}
		}
	}
	
	public static boolean Contains(ArrayList<Board> arr, Board b) {
		for(Board a : arr) {
			if(BoardEquals(a, b)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean BoardEquals(Board a, Board b) {
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				if(a.board[y][x] != b.board[y][x]) {
					return false;
				}
			}
		}
		
		return true;
	}

	public int AmountOfDoubles() {
		int doubles = 0;

		if ((board[0][0] == 1.0 && board[0][1] == 1.0) || (board[0][1] == 1.0 && board[0][2] == 1.0)) {
			doubles++;
		}

		if ((board[1][0] == 1.0 && board[1][1] == 1.0) || (board[1][1] == 1.0 && board[1][2] == 1.0)) {
			doubles++;
		}

		if ((board[2][0] == 1.0 && board[2][1] == 1.0) || (board[2][1] == 1.0 && board[2][2] == 1.0)) {
			doubles++;
		}

		if ((board[0][0] == 1.0 && board[1][0] == 1.0) || (board[1][0] == 1.0 && board[2][0] == 1.0)) {
			doubles++;
		}

		if ((board[0][1] == 1.0 && board[1][1] == 1.0) || (board[1][1] == 1.0 && board[2][1] == 1.0)) {
			doubles++;
		}

		if ((board[0][2] == 1.0 && board[1][2] == 1.0) || (board[1][2] == 1.0 && board[2][2] == 1.0)) {
			doubles++;
		}

		if ((board[0][0] == 1.0 && board[1][1] == 1.0) || (board[1][1] == 1.0 && board[2][2] == 1.0)) {
			doubles++;
		}

		if ((board[0][2] == 1.0 && board[1][1] == 1.0) || (board[1][1] == 1.0 && board[2][0] == 1.0)) {
			doubles++;
		}

		return doubles;
	}

	public void FlipBoardForEnemy() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (board[y][x] == 1.0) {
					board[y][x] = -1.0;
				} else if (board[y][x] == -1.0) {
					board[y][x] = 1.0;
				}
			}
		}
	}

	public Board PlayerMakeMove(int x, int y) {
		Board newBoard = new Board(this);

		if (newBoard.board[y][x] != 0.0) {
			return null;
		}

		newBoard.board[y][x] = -1.0;
		return newBoard;
	}

	public ArrayList<Board> AllPossibleMoves() {
		ArrayList<Board> possibleMoves = new ArrayList<Board>();

		// make this function
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (board[y][x] == 0.0) {
					Board newBoard = new Board(this);
					newBoard.board[y][x] = 1.0;
					possibleMoves.add(newBoard);
				}
			}
		}

		return possibleMoves;
	}

	public ArrayList<Double> StreamToInput() {
		ArrayList<Double> d = new ArrayList<Double>();
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				double zero = 0.0;
				double one = 0.0;
				double negativeOne = 0.0;
				
				if(board[y][x] == 0.0) {
					zero = 1.0;
				} else if(board[y][x] == 1.0) {
					one = 1.0;
				} else if(board[y][x] == -1.0) {
					negativeOne = 1.0;
				}
				
				d.add(zero);
				d.add(one);
				d.add(negativeOne);
			}
		}

		return d;
	}

	public boolean Won() {
		// horizontal wins
		for (int y = 0; y < 3; y++) {
			if (board[y][0] == 1.0 && board[y][1] == 1.0 && board[y][2] == 1.0) {
				return true;
			}
		}

		// vertical wins
		for (int x = 0; x < 3; x++) {
			if (board[0][x] == 1.0 && board[1][x] == 1.0 && board[2][x] == 1.0) {
				return true;
			}
		}

		// diagonal wins
		if ((board[0][0] == 1.0 && board[1][1] == 1.0 && board[2][2] == 1.0)
				|| (board[0][2] == 1.0 && board[1][1] == 1.0 && board[2][0] == 1.0)) {
			return true;
		}

		return false;
	}

	public boolean Won_Player() {
		// horizontal wins
		for (int y = 0; y < 3; y++) {
			if (board[y][0] == -1.0 && board[y][1] == -1.0 && board[y][2] == -1.0) {
				return true;
			}
		}

		// vertical wins
		for (int x = 0; x < 3; x++) {
			if (board[0][x] == -1.0 && board[1][x] == -1.0 && board[2][x] == -1.0) {
				return true;
			}
		}

		// diagonal wins
		if ((board[0][0] == -1.0 && board[1][1] == -1.0 && board[2][2] == -1.0)
				|| (board[0][2] == -1.0 && board[1][1] == -1.0 && board[2][0] == -1.0)) {
			return true;
		}

		return false;
	}

	public void Print() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (board[y][x] == 1.0) {
					System.out.print("X ");
				} else if (board[y][x] == -1.0) {
					System.out.print("O ");
				} else if (board[y][x] == 0.0) {
					System.out.print("- ");
				} else {
					System.out.print("@ ");
				}
			}

			System.out.println();
		}
	}

}
