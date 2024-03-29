/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmtothello;

import backprop.Backprop;
import backprop.BoardState;
import backprop.Player;
import java.util.Queue;

/**
 *
 * @author Ol' Dirty Bastard
 */
public class NeuralThinker implements Thinker
{

	private int maxDepth = 5;
	private final String name;
	private RowCol bestMove;
	private final Backprop backprop;

	public NeuralThinker(Backprop backprop, String name)
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

	private double currentgameScore(OBoard board, char color)
	{
//		Score score = board.calculateScore(false);
		double score;
		try
		{
			if(backprop.numInput == 60) {
				score = backprop.calculateOutput(BoardState.toTranningExMoveHistory(board, Player.BLACK))[0];
			}
			else {
				score = backprop.calculateOutput(BoardState.toTranningEx(board, Player.BLACK))[0];
			}
			score = score - 0.5;
			if (color == C.WHITE)
			{
				score = score * -1.0;
			}
			return score;
		}
		catch (Exception ex)
		{
		}
		System.out.println("shouldn't be here");
		return 0.0;
//		return score.getScore(color) - score.getScore(board.opponentOf(color));
	}

  public void setDepth(int newDepth) {
    maxDepth = newDepth;
  }

}
