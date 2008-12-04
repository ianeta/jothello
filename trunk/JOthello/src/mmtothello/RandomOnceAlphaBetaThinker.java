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
public class RandomOnceAlphaBetaThinker implements Thinker
{
	private int maxDepth = 5;
  private int currentMoveNumber = 0; 
  private int moveNumberForRandom = 0;
	private static final String name = "Moody Alpha-Beta";
	private RowCol bestMove;
	public RowCol nextMove(char color, OBoard board)
	{
		bestMove = new RowCol(-1, -1);
//    if (isStartOfGame(board)) return getRandomMove(color, board);
    if (isStartOfGame(board)) {
      currentMoveNumber = 0;
      moveNumberForRandom = random(30);
    } else currentMoveNumber ++;
    if (currentMoveNumber == moveNumberForRandom) { 
      return getRandomMove(color, board);
    }
    
    
		double v = maxValue(color, board, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//		System.out.println(v);
//		AlphaBetaHelper.printMove(bestMove, color);
		return bestMove;
	}
  
  private boolean isStartOfGame(OBoard board) {
    int numB = board.count(C.BLACK);
    int numW = board.count(C.WHITE);
    int totalCount = numB + numW;
    return totalCount <= 5; 
  }

  private RowCol getRandomMove(char color, OBoard board) {
		Queue<MoveInfo> validMoves = board.getValidMoves(color);
		if (validMoves.size() < 1) {
			return bestMove;
		}
    int n = validMoves.size();
    int idx = random(n);
    MoveInfo mi = null;
    for (int i = 0; i <= idx; i++) { 
      mi = validMoves.remove();
    }
    bestMove.col = mi.getY();
    bestMove.row = mi.getX();
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
    

//		if(depth == 0)
//		{
//			AlphaBetaHelper.printValidMoves(validMoves, color);
//		}

		double v = Double.NEGATIVE_INFINITY;
		while(!validMoves.isEmpty())
		{
			MoveInfo move = validMoves.remove();
			double tempV = max(v, minValue(color, new OBoard(board, move), depth + 1, a, b));
			if(depth == 0)
			{
				if(tempV > v /*|| (tempV == v && greedyScoreOf(bestMove) == greedyScoreOf(move) && randomlyDoIt()) */) 
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
  
  private boolean randomlyDoIt() {
    boolean answer = random(2)==0;
//    if (answer) {
//      System.out.println("Random move happened!");
//    } else {
//      System.out.println("Random move DID NOT happen.");
//    }
    return answer;
  }

  private int random(int m) { 
    return (int)( Math.random() * m ) % m;
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
		Score score = board.calculateScore(true);
		int totalScore = score.getScore(color) - score.getScore(board.opponentOf(color));
		return (totalScore * 100.0) / depth;
	}

	private double currentgameScore(OBoard board, char color)
	{
		Score score = board.calculateScore(false);
		return score.getScore(color) - score.getScore(board.opponentOf(color));
	}

  public void setDepth(int newDepth) {
    maxDepth = newDepth;
  }
}
