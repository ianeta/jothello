/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmtothello;

import java.util.Queue;

/**
 *
 * @author Ol' Dirty Bastard
 */
public class AlphaBetaHelper
{
	public static void printValidMoves(Queue<MoveInfo> moves, char color)
	{
		if (color == C.BLACK)
		{
			System.out.print("black : ");
		}
		else if (color == C.WHITE)
		{
			System.out.print("white : ");
		}
		for (MoveInfo move : moves)
		{
			String m = move.getY() + "" + move.getX();
			System.out.print(m + " ");
		}
		System.out.println("");
	}

	public static void printMove(RowCol move, char color)
	{
		if (color == C.BLACK)
		{
			System.out.print("black chooses : ");
		}
		else if (color == C.WHITE)
		{
			System.out.print("white chooses : ");
		}
		System.out.println(move.row + "" + move.col);
	}
}
