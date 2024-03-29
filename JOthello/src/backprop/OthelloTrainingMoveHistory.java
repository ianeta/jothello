package backprop;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import mmtothello.C;
import mmtothello.OBoard;

public class OthelloTrainingMoveHistory {

	public static final int MOVEPARTITIONSIZE = 1;
	public static final int[] SCORERANGES = { -5, 0, 1, 6 };
	// if you change score ranges need to change this too
	public static final int CENTERSCORERANGE = 2;
	public static final double LEARNINGRATE = 0.005;
	public static final double MOMENTUM = 0.0005;

	public static final ScoreRange RANGE = new ScoreRange(SCORERANGES);

	public transient List<TrainingEx>[][] partitions = new List[C.TOTAL_MOVES
			/ MOVEPARTITIONSIZE + 1][SCORERANGES.length + 1];

	public Backprop[][] anns = new Backprop[C.TOTAL_MOVES / MOVEPARTITIONSIZE
			+ 1][SCORERANGES.length + 1];

	public OthelloTrainingMoveHistory() {
		for (int i = 0; i < partitions.length; i++) {
			for (int j = 0; j < partitions[i].length; j++) {
				partitions[i][j] = new ArrayList<TrainingEx>();
			}
		}
	}

	
	public void addGame(List<OBoard> game, Player winner) throws Exception {

		List<TrainingEx> gameTrain = new ArrayList<TrainingEx>();

		for (OBoard oBoard : game) {
			gameTrain.add(BoardState.toTranningExMoveHistory(oBoard, winner));
		}

		int i = 0;
		for (OBoard oBoard : game) {

			int blackScore = oBoard.calculateScore(isGameOver(oBoard))
					.getBlackScore();
			int whiteScore = oBoard.calculateScore(isGameOver(oBoard))
					.getWhiteScore();

			int move = this.getMovePartition(i);

			int scoreRangeNum = RANGE.getPlace(blackScore - whiteScore);

			for (int j = i + 1; j < game.size(); j++) {
				partitions[move][scoreRangeNum].add(gameTrain.get(j));
			}
			i++;
		}
	}
	

	public void addGameNew(List<OBoard> game, Player winner) throws Exception {

		List<TrainingEx> gameTrainWin = new ArrayList<TrainingEx>();
		// List<TrainingEx> gameTrainLose = new ArrayList<TrainingEx>();

		Player currentMove = Player.WHITE;
		for (OBoard oBoard : game) {
			TrainingEx te;
			if (currentMove == winner) {
				te = BoardState.toTrainingExMoveHistory(oBoard, winner, true);
				//System.out.println("Here1");
			} else {
				te = BoardState.toTrainingExMoveHistory(oBoard, winner, false);
				//System.out.println("Her20000000");
			}
			gameTrainWin.add(te);
			// te = BoardState.toTranningEx(oBoard, winner, false);
			// System.out.println(te);
			// gameTrainLose.add(te);
			if (currentMove == Player.WHITE) {
				currentMove = Player.BLACK;
			} else {
				currentMove = Player.WHITE;
			}
		}

		int i = 0;
		for (OBoard oBoard : game) {
			int winnerScore;
			int loserScore;

			if (winner == Player.BLACK) {
				winnerScore = oBoard.calculateScore(isGameOver(oBoard))
						.getBlackScore();
				loserScore = oBoard.calculateScore(isGameOver(oBoard))
						.getWhiteScore();
			} else if (winner == Player.WHITE) {
				loserScore = oBoard.calculateScore(isGameOver(oBoard))
						.getBlackScore();
				winnerScore = oBoard.calculateScore(isGameOver(oBoard))
						.getWhiteScore();
			} else {
				throw new Exception(
						"Invalid player type for the winner in addGame method");
			}

			int move = this.getMovePartition(i);

			int scoreRangeNum = RANGE.getPlace(winnerScore - loserScore);

			//System.out.println(gameTrainWin.get(0));
			for (int j = i + 1; j < game.size(); j++) {
				partitions[move][scoreRangeNum].add(gameTrainWin.get(j));
				// partitions[move][scoreRangeNum].add(gameTrainLose.get(j));

			}
			i++;
		}
	}

	/*
	 * public void addState(OBoard oBoard, Player winner) throws Exception {
	 * TrainingEx te = BoardState.toTranningEx(oBoard, winner);
	 * 
	 * // is the calculate score part done correctly here? int blackScore =
	 * oBoard.calculateScore(isGameOver(oBoard)) .getBlackScore();
	 * 
	 * // int move = this.getMovePartition(BoardState.getMoveNumber(te)); //int
	 * move = oBoard.getMoveNumber() - 1;
	 * 
	 * int scoreRangeNum = RANGE.getPlace(blackScore);
	 * 
	 * for (int i = 0; i < move; i++) { partitions[i][scoreRangeNum].add(te); }
	 * 
	 * }
	 */

	public Backprop[][] runTraining(String filename) {
		// do the training
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		Backprop defaultAnn = null;
		for (int i = 0; i < partitions.length; i++) {
			for (int j = 0; j < partitions[i].length; j++) {

				if (i != 0 && partitions[i][j].size() < C.MIN_GAMES_THRESHOLD) {
					System.out.println("Using the default nw for: " + filename
							+ "." + i + "." + j + ".saved");
					anns[i][j] = defaultAnn;

				} else {
					anns[i][j] = new Backprop(C.DEFAULT_DIMENSION
							* C.DEFAULT_DIMENSION - 4,
							(C.DEFAULT_DIMENSION * C.DEFAULT_DIMENSION) / 2,
							LEARNINGRATE);
					anns[i][j].init_weights();
					try {
						anns[i][j].update_weights(partitions[i][j]
								.toArray(new TrainingEx[0]), MOMENTUM);

					} catch (Exception e) {
						System.out.println(e.getMessage()
								+ " for the following ann: " + filename + "."
								+ i + "." + j + ".saved");
						// e.printStackTrace();
					}
					if (i == 0 && j == CENTERSCORERANGE) {
						defaultAnn = anns[i][j];
						System.out.println("Default Ann is: " + filename + "."
								+ i + "." + j + ".saved");
					}
				}

				try {
					fos = new FileOutputStream(filename + "." + i + "." + j
							+ ".saved");
					out = new ObjectOutputStream(fos);
					out.writeObject(anns[i][j]);
					out.close();
				} catch (IOException ex) {
					System.out.println("Error saving to disk");
					ex.printStackTrace();
				}

			}

		}
		return anns;
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
