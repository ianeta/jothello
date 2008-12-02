/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmtothello;

import java.util.Queue;

import backprop.Backprop;
import backprop.BoardState;
import backprop.Player;

/**
 *
 * @author Ol' Dirty Bastard
 */
public class TestNeuralThinker implements Thinker
{

	private static final int maxDepth = 5;
	private final String name;
	private RowCol bestMove;
	private final Backprop backprop;

	public TestNeuralThinker(Backprop backprop, String name)
	{
		this.backprop = backprop;
		this.name = name;
	}

	public RowCol nextMove(char color, OBoard board)
	{
		bestMove = new RowCol(-1, -1);
		double v = maxValue(color, board, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//		System.out.println(v);
		AlphaBetaHelper.printMove(bestMove, color);
		return bestMove;
	}

	public String getName()
	{
		return name;
	}

	private double maxValue(char color, OBoard board, int depth, double a, double b)
	{
		Queue<MoveInfo> validMoves = board.getValidMoves(color);
		if (depth >= maxDepth)
		{
			//game isn't over, create a heuristic for current layout
			return currentgameScore(board, color);
		}
		else if (validMoves.size() < 1)
		{
			//game is over, check who wins
			return endgameScore(board, color, depth);
		}

		if(depth == 0)
		{
			AlphaBetaHelper.printValidMoves(validMoves, color);
		}

		double v = Double.NEGATIVE_INFINITY;
		while (!validMoves.isEmpty())
		{
			MoveInfo move = validMoves.remove();
			double tempV = max(v, minValue(color, new OBoard(board, move), depth + 1, a, b));
			if (depth == 0)
			{
				if (tempV > v)
				{
					bestMove.row = move.getY();
					bestMove.col = move.getX();
				}
			}
			v = tempV;
			if (v >= b)
			{
				return b;
			}
			a = max(a, v);
		}
		return v;
	}

	private double minValue(char color, OBoard board, int depth, double a, double b)
	{
		Queue<MoveInfo> validMoves = board.getValidMoves(board.opponentOf(color));
		if (depth >= maxDepth)
		{
			//game isn't over, create a heuristic for current layout
			return currentgameScore(board, color);
		}
		else if (validMoves.size() < 1)
		{
			//game is over, check who wins
			return endgameScore(board, color, depth);
		}

		double v = Double.POSITIVE_INFINITY;
		while (!validMoves.isEmpty())
		{
			MoveInfo move = validMoves.remove();
			v = min(v, maxValue(color, new OBoard(board, move), depth + 1, a, b));
			if (v <= a)
			{
				return v;
			}
			b = min(b, v);
		}
		return v;
	}

	private double max(double score1, double score2)
	{
		if (score1 > score2)
		{
			return score1;
		}
		return score2;
	}

	private double min(double score1, double score2)
	{
		if (score1 < score2)
		{
			return score1;
		}
		return score2;
	}

	private double endgameScore(OBoard board, char color, int depth)
	{
		Score score = board.calculateScore(true);
		int totalScore = score.getScore(color) - score.getScore(board.opponentOf(color));
		return (totalScore * 100.0) / depth;
	}

	//not sure if i am doing this right
	private double currentgameScore(OBoard board, char color) {
		// Score score = board.calculateScore(false);
		double score;
		try {
			if (color == C.WHITE) {
				if (backprop.numInput == 60) {
					score = backprop
							.calculateOutput(BoardState
									.toTrainingExMoveHistory(board,
											Player.WHITE, true))[0];
				} else {
					score = backprop.calculateOutput(BoardState.toTranningEx(
							board, Player.WHITE, true))[0];
				}
			} else if (color == C.BLACK) {
				if (backprop.numInput == 60) {
					score = backprop
							.calculateOutput(BoardState
									.toTrainingExMoveHistory(board,
											Player.BLACK, true))[0];
				} else {
					score = backprop.calculateOutput(BoardState.toTranningEx(
							board, Player.BLACK, true))[0];
				}
			} else {
				throw new Exception(
						"Error with the color in method currentGameScore");
			}

			return score;
		} catch (Exception ex) {
			System.out.println("shouldn't be here");
			ex.printStackTrace();
		}
		System.out.println("shouldn't be here");
		return 0.0;
		// return score.getScore(color) -
		// score.getScore(board.opponentOf(color));
	}
}
