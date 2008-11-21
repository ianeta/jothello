/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmtothello;

/**
 *
 * @author ODB
 */
public class MoveInfo
{

	private int x;
	private int y;
	private char color;
	private double score;

	public MoveInfo(char color, int x, int y, double score)
	{
		this.x = x;
		this.y = y;
		this.color = color;
		this.score = score;
	}

	public MoveInfo(MoveInfo move, double score)
	{
		if (move != null)
		{
			this.x = move.getX();
			this.y = move.getY();
			this.color = move.getColor();
		}
		else
		{
			this.x = -1;
			this.y = -1;
			this.color = C.EMPTY;
		}
		this.score = score;
	}

	public MoveInfo()
	{
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public char getColor()
	{
		return color;
	}

	public double getScore()
	{
		return score;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setColor(char color)
	{
		this.color = color;
	}

	public void setScore(double score)
	{
		this.score = score;
	}
}
