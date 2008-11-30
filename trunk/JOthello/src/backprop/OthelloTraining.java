package backprop;

import java.util.ArrayList;
import java.util.List;

import mmtothello.C;
import mmtothello.OBoard;

public class OthelloTraining {

	public static final int MOVEPARTITIONSIZE = 1;
	public static final int[] SCORERANGES = { -5, 0, 5 };

	public static final ScoreRange RANGE = new ScoreRange(SCORERANGES);

	public List<TrainingEx>[][] partitions = new List[(C.DEFAULT_DIMENSION * C.DEFAULT_DIMENSION)
			/ MOVEPARTITIONSIZE][SCORERANGES.length + 1];

	public OthelloTraining() {
		for (int i = 0; i < partitions.length; i++) {
			for (int j = 0; j < partitions[i].length; j++) {
				partitions[i][j] = new ArrayList<TrainingEx>();
			}
		}
	}

	public void addState(OBoard oBoard, Player winner) throws Exception {
		TrainingEx te = BoardState.toTranningEx(oBoard, winner);

		// is the calculate score part done correctly here?
		int blackScore = oBoard.calculateScore(isGameOver(oBoard))
				.getBlackScore();

		int move = this.getMovePartition(BoardState.getMoveNumber(te));

		int scoreRangeNum = RANGE.getPlace(blackScore);
		
		for(int i = 0; i < move; i++) {
			partitions[i][scoreRangeNum].add(te);
		}

	}
	
	public void runTranning() {
		//do the training
	}

	private int getMovePartition(int move) {
		return move / MOVEPARTITIONSIZE;
	}

	private boolean isGameOver(OBoard board) {
		if (board.canPlayerMove(C.BLACK) || board.canPlayerMove(C.WHITE)) {
			return false;
		}
		return true;
	}

}
