package backprop;

import mmtothello.C;
import mmtothello.OBoard;

public class BoardState {

	//public static final int DIM = 8;

	public static TrainingEx toTranningEx(OBoard oBoard, Player winner)
			throws Exception {
		
		if(winner == Player.EMPTY) {
			throw new Exception("Invalid player type for winner");
		}

		int board[] = new int[C.DEFAULT_DIMENSION * C.DEFAULT_DIMENSION];
		char[][] b = oBoard.getB();
		if (b.length != C.DEFAULT_DIMENSION || b[0].length != C.DEFAULT_DIMENSION) {
			throw new Exception("Invalid board size");
		}

		int place = 0;
		for (int i = 0; i < C.DEFAULT_DIMENSION; i++) {
			for (int j = 0; j < C.DEFAULT_DIMENSION; j++) {
				if (b[i][j] == C.BLACK) {
					board[place] = Player.BLACK.getCode();
				} else if (b[i][j] == C.WHITE) {
					board[place] = Player.WHITE.getCode();
				} else if (b[i][j] == C.EMPTY) {
					board[place] = Player.EMPTY.getCode();
				} else {
					throw new Exception("Invalid character for board location");
				}
				place++;
			}
		}
		
		
		return new TrainingEx(board, (double) winner.getCode());
	}

	public static int getMoveNumber(TrainingEx te) {
		int lastMove = C.DEFAULT_DIMENSION * C.DEFAULT_DIMENSION;

		for (int i = 1; i < te.input.length; i++) {
			if (Player.EMPTY.getCode() == te.input[i]) {
				lastMove--;
			}
		}

		return lastMove;
	}

}
