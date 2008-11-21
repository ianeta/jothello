/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmtothello;

import java.util.Queue;

/**
 *
 * @author ODB
 */
public class SimpleAlphaBetaThinker implements Thinker
{
	private static final int maxDepth = 6;
	private static final String name = "Simple Alpha-Beta";
	private RowCol bestMove;

	public RowCol nextMove(char color, OBoard board)
	{
		bestMove = new RowCol(-1, -1);
		System.out.println(maxValue(color, board, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
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

		double v = Double.NEGATIVE_INFINITY;
		while(!validMoves.isEmpty())
		{
			MoveInfo move = validMoves.remove();
			double tempV = max(v, minValue(color, new OBoard(board, move), depth + 1, a, b));
			if(depth == 0)
			{
				if(tempV > v)
				{
					bestMove.row = move.getY();
					bestMove.col = move.getX();
				}
			}
			v = tempV;
			if(v >= b)
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
		while(!validMoves.isEmpty())
		{
			MoveInfo move = validMoves.remove();
			v = min(v, maxValue(color, new OBoard(board, move), depth + 1, a, b));
			if(v <= a)
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
		int totalScore = board.countNow(color) - board.countNow(board.opponentOf(color));
//		if (totalScore > 0)
//		{
//			return 1000.0 / depth;
//		}
//		else if (totalScore < 0)
//		{
//			return -1000.0 / depth;
//		}
//		return 0.0;
		return (totalScore * 100.0) / depth;
	}

	private double currentgameScore(OBoard board, char color)
	{
		return board.countNow(color) - board.countNow(board.opponentOf(color));
	}
}
