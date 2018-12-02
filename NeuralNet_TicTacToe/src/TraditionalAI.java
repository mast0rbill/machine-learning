import java.util.ArrayList;
import java.util.Random;

public class TraditionalAI {

	// 1 = RANDOM
	// 2 = FILL
	// 3 = LOGIC

	public static Board MakeMove(Board matchBoard, Random rand, int aiType) {
		ArrayList<Board> allPossibleMoves = matchBoard.AllPossibleMoves();
		if(allPossibleMoves.size() == 0) { //tie
			return null;
		}
		
		Board newBoard = new Board(matchBoard);
		
		//if(aiType == 0) {
			LogicAI(newBoard.board, rand);
		/*} else if(aiType == 1) {
			FillAI(newBoard.board);
		} else if(aiType == 2) {
			RandomAI(newBoard.board, rand);
		}*/
		
		return newBoard;
	}
	
	public static void RandomAI(double[][] boardState, Random rand) {
		boolean taken = false;

		do {
			int randX = rand.nextInt(3);
			int randY = rand.nextInt(3);

			if (boardState[randY][randX] == 0.0) {
				boardState[randY][randX] = 1.0;
				taken = false;
			} else {
				taken = true;
			}
		} while (taken);
	}
	
	public static void FillAI(double[][] boardState) {
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				if(boardState[y][x] == 0.0) {
					boardState[y][x] = 1.0;
					return;
				}
			}
		}
	}

	public static void LogicAI(double[][] boardState, Random rand) {
		for (int i = 0; i < 3; i++) // Horizontal check
		{
			if (boardState[i][0] == 1.0 && boardState[i][1] == 1.0 && boardState[i][2] == 0.0) {
				boardState[i][2] = 1.0;
				return;
			}
			if (boardState[i][1] == 1.0 && boardState[i][2] == 1.0 && boardState[i][0] == 0.0) {
				boardState[i][0] = 1.0;
				return;
			}
			if (boardState[i][2] == 1.0 && boardState[i][0] == 1.0 && boardState[i][1] == 0.0) {
				boardState[i][1] = 1.0;
				return;
			}
		}
		for (int i = 0; i < 3; i++) // Vertical
		{
			if (boardState[0][i] == 1.0 && boardState[1][i] == 1.0 && boardState[2][i] == 0.0) {
				boardState[2][i] = 1.0;
				return;
			}
			if (boardState[1][i] == 1.0 && boardState[2][i] == 1.0 && boardState[0][i] == 0.0) {
				boardState[0][i] = 1.0;
				return;
			}
			if (boardState[2][i] == 1.0 && boardState[0][i] == 1.0 && boardState[1][i] == 0.0) {
				boardState[1][i] = 1.0;
				return;
			}
		}
		int i = 0;
		// diagonal >v
		if (boardState[i][i] == 1.0 && boardState[i + 1][i + 1] == 1.0 && boardState[i + 2][i + 2] == 0.0) {
			boardState[i + 2][i + 2] = 1.0;
			return;
		}
		if (boardState[i][i] == 1.0 && boardState[i + 1][i + 1] == 0.0 && boardState[i + 2][i + 2] == 1.0) {
			boardState[i + 1][i + 1] = 1.0;
			return;
		}
		if (boardState[i][i] == 0.0 && boardState[i + 1][i + 1] == 1.0 && boardState[i + 2][i + 2] == 1.0) {
			boardState[i][i] = 1.0;
			return;
		}

		// diagonal >^
		if (boardState[i][i + 2] == 1.0 && boardState[i + 1][i + 1] == 1.0 && boardState[i + 2][i] == 0.0) {
			boardState[i + 2][i] = 1.0;
			return;
		}
		if (boardState[i][i + 2] == 1.0 && boardState[i + 1][i + 1] == 0.0 && boardState[i + 2][i] == 1.0) {
			boardState[i + 1][i + 1] = 1.0;
			return;
		}
		if (boardState[i][i + 2] == 0.0 && boardState[i + 1][i + 1] == 1.0 && boardState[i + 2][i] == 1.0) {
			boardState[i][i + 2] = 1.0;
			return;
		}

		int counter = 0;
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				if (boardState[k][j] != 0.0) {
					counter++;
				}
			}
		}

		if (counter == 0) // Empty board
		{
			boardState[1][1] = 1.0;
			return;
		}

		if (counter == 1) {
			if (boardState[1][1] == -1.0) {
				boardState[2][2] = 1.0;
			} else
				boardState[1][1] = 1.0;
			return;
		}

		// ========================================Enemy win
		// check===========================================
		/// ===============================================================================================
		for (int q = 0; q < 3; q++) // Horizontal check
		{
			if (boardState[q][0] == -1.0 && boardState[q][1] == -1.0 && boardState[q][2] == 0.0) {
				boardState[q][2] = 1.0;
				return;
			}
			if (boardState[q][1] == -1.0 && boardState[q][2] == -1.0 && boardState[q][0] == 0.0) {
				boardState[q][0] = 1.0;
				return;
			}
			if (boardState[q][2] == -1.0 && boardState[q][0] == -1.0 && boardState[q][1] == 0.0) {
				boardState[q][1] = 1.0;
				return;
			}
		}
		for (int q = 0; q < 3; q++) // Vertical
		{
			if (boardState[0][q] == -1.0 && boardState[1][q] == -1.0 && boardState[2][q] == 0.0) {
				boardState[2][q] = 1.0;
				return;
			}
			if (boardState[1][q] == -1.0 && boardState[2][q] == -1.0 && boardState[0][q] == 0.0) {
				boardState[0][q] = 1.0;
				return;
			}
			if (boardState[2][q] == -1.0 && boardState[0][q] == -1.0 && boardState[1][q] == 0.0) {
				boardState[1][q] = 1.0;
				return;
			}
		}
		i = 0;
		// diagonal >v
		if (boardState[i][i] == -1.0 && boardState[i + 1][i + 1] == -1.0 && boardState[i + 2][i + 2] == 0.0) {
			boardState[i + 2][i + 2] = 1.0;
			return;
		}
		if (boardState[i][i] == -1.0 && boardState[i + 1][i + 1] == 0.0 && boardState[i + 2][i + 2] == -1.0) {
			boardState[i + 1][i + 1] = 1.0;
			return;
		}
		if (boardState[i][i] == 0.0 && boardState[i + 1][i + 1] == -1.0 && boardState[i + 2][i + 2] == -1.0) {
			boardState[i][i] = 1.0;
			return;
		}

		// diagonal >^
		if (boardState[i][i + 2] == -1.0 && boardState[i + 1][i + 1] == -1.0 && boardState[i + 2][i] == 0.0) {
			boardState[i + 2][i] = 1.0;
			return;
		}
		if (boardState[i][i + 2] == -1.0 && boardState[i + 1][i + 1] == 0.0 && boardState[i + 2][i] == -1.0) {
			boardState[i + 1][i + 1] = 1.0;
			return;
		}
		if (boardState[i][i + 2] == 0.0 && boardState[i + 1][i + 1] == -1.0 && boardState[i + 2][i] == -1.0) {
			boardState[i][i + 2] = 1.0;
			return;
		} // ===============================================================================================

		TraditionalAI.RandomAI(boardState, rand);
	}

}
