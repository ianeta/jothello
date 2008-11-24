/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmtothello;

/**
 *
 * @author ODB
 */
public class Score
{
	public static final String BLACK = "black";
	public static final String WHITE = "white";
	public static final String DRAW = "draw";

	private boolean gameOver = true;
	private int blackScore;
	private int whiteScore;
	
	public Score(int black, int white, boolean gameOver)
	{
		this.blackScore = black;
		this.whiteScore = white;
		this.gameOver = gameOver;
		if(this.gameOver)
		{
			if(blackScore != whiteScore)
			{
				int remainingPoints = 64 - blackScore - whiteScore;
				if(blackScore > whiteScore)
				{
					blackScore += remainingPoints;
				}
				else
				{
					whiteScore += remainingPoints;
				}
			}
		}
	}

	public int getBlackScore()
	{
		return blackScore;
	}

	public int getWhiteScore()
	{
		return whiteScore;
	}

	public int getScore(char color)
	{
		if(color == C.BLACK)
		{
			return blackScore;
		}
		return whiteScore;
	}

	public void printScore()
	{
		System.out.print("black = " + blackScore + ", white = " + whiteScore);
		System.out.println(", " + whoIsWinner() + " wins!");
	}

	public String whoIsWinner()
	{
		if(blackScore > whiteScore)
		{
			return BLACK;
		}
		else if(blackScore < whiteScore)
		{
			return WHITE;
		}
		else
		{
			return DRAW;
		}
	}
}
