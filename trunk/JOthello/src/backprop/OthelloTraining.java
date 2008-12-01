package backprop;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import mmtothello.C;
import mmtothello.OBoard;

public class OthelloTraining {

	public static final int MOVEPARTITIONSIZE = 1;
	public static final int[] SCORERANGES = { -5, 0, 1, 6 };
	// if you change score ranges need to change this too
	public static final int CENTERSCORERANGE = 2;
	public static final double LEARNINGRATE = 0.05;

	public static final ScoreRange RANGE = new ScoreRange(SCORERANGES);

	public transient List<TrainingEx>[][] partitions = new List[C.TOTAL_MOVES
			/ MOVEPARTITIONSIZE][SCORERANGES.length + 1];

	public Backprop[][] anns = new Backprop[C.TOTAL_MOVES / MOVEPARTITIONSIZE][SCORERANGES.length + 1];

	public OthelloTraining() {
		for (int i = 0; i < partitions.length; i++) {
			for (int j = 0; j < partitions[i].length; j++) {
				partitions[i][j] = new ArrayList<TrainingEx>();
			}
		}
	}

	public void addGame(List<OBoard> game, Player winner) throws Exception {

		List<TrainingEx> gameTrain = new ArrayList<TrainingEx>();

		for (OBoard oBoard : game) {
			gameTrain.add(BoardState.toTranningEx(oBoard, winner));
		}

		int i = 0;
		for (OBoard oBoard : game) {

			int blackScore = oBoard.calculateScore(isGameOver(oBoard))
					.getBlackScore();
			int whiteScore = oBoard.calculateScore(isGameOver(oBoard))
					.getWhiteScore();

			int move = this.getMovePartition(i);

			int scoreRangeNum = RANGE.getPlace(blackScore - whiteScore);

			for (int j = i; j < game.size(); j++) {
				partitions[move][scoreRangeNum].add(gameTrain.get(j));
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
							* C.DEFAULT_DIMENSION,
							(C.DEFAULT_DIMENSION * C.DEFAULT_DIMENSION) / 2,
							LEARNINGRATE);
					anns[i][j].init_weights();
					try {
						anns[i][j].update_weights(partitions[i][j]
								.toArray(new TrainingEx[1]));

					} catch (Exception e) {
						System.out.println(e.getMessage()
								+ " for the following ann: " + filename + "."
								+ i + "." + j + ".saved");
						e.printStackTrace();
					}
					if (i == 0 && j == CENTERSCORERANGE) {
						defaultAnn = anns[i][j];
						System.out.println("Default Ann is: " + filename + "."
								+ i + "." + j + ".saved");
					}
				}

				// save the file to disk
				/*
				 * try { fos = new FileOutputStream(filename + "." + i + "." + j
				 * + ".saved"); out = new ObjectOutputStream(fos);
				 * out.writeObject(anns[i][j]); out.close(); } catch
				 * (IOException ex) {
				 * System.out.println("Error saving to disk");
				 * ex.printStackTrace(); }
				 */

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
