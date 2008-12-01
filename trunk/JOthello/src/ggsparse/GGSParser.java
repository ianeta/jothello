/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ggsparse;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import mmtothello.C;
import mmtothello.OBoard;
import mmtothello.Score;

/**
 *
 * @author ODB
 */
public class GGSParser
{
	private int b1 = -1;
	private int b2 = -2;
	private State state = State.START;
	private int gameCount = 0;
	private StringBuffer movesBuffer;
	private char[] tempBuffer = new char[2];
//	private List<String> movesList;
	private String path = "./ggf/";
	private String[] filenames;
	private List<String> parsedGames;

	public GGSParser()
	{
//		movesList = new ArrayList<String>();
		fillFilenameList();
		for(String filename : filenames)
		{
			parseFile(path + filename);
		}
//		parseFile("Othello.25e4.ggf");
		System.out.println(gameCount);
	}

	private void parseFile(String filename)
	{
		try
		{
			parsedGames = new ArrayList<String>();
			FileInputStream fileStream = new FileInputStream(new File(filename));
			int b;
			while ((b = fileStream.read()) > -1)
			{
				parseByte(b);
			}
			fileStream.close();

			PrintWriter outStream = new PrintWriter(new FileOutputStream(new File(filename + ".txt"), true));
			for(String s : parsedGames)
			{
				outStream.println(s);
			}
			outStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void parseByte(int b)
	{
		switch (state)
		{
			case START:
				if (b == ';' && b1 == '(')
				{
					gameCount++;
					movesBuffer = new StringBuffer();
					state = State.MOVE;
				}
				break;
			case MOVE:
				if (b == '[' && (b1 == 'B' || b1 == 'W') && b2 == ']')
				{
					state = State.MOVEVALUE1;
				}
				else if (b == ')' && b1 == ';')
				{
					makeMoves();
					state = State.START;
				}
				break;
			case MOVEVALUE1:
				tempBuffer[0] = ((char) (0xFF & b));
				state = State.MOVEVALUE2;
				break;
			case MOVEVALUE2:
				tempBuffer[1] = ((char) (0xFF & b));
				if (!((tempBuffer[0] == 'p' || tempBuffer[0] == 'P') && (tempBuffer[1] == 'a' || tempBuffer[1] == 'A')))
				{
					movesBuffer.append(tempBuffer);
				}
				state = State.MOVE;
				break;
			default:
				break;
		}
		b2 = b1;
		b1 = b;
	}

//	private void verifyMovesBuffer()
//	{
//		if ((movesBuffer.length() / 2) <= 60 && movesBuffer.length() >= 40)
//		{
//			String moves = movesBuffer.toString().toLowerCase();
//			String firstMove = moves.substring(0, 2);
//			if (firstMove.equals("c4") || firstMove.equals("d3") || firstMove.equals("f5") || firstMove.equals("e6"))
//			{
//				movesList.add(moves);
//				System.out.println(moves);
//			}
//		}
//	}

	private void makeMoves()
	{
		if ((movesBuffer.length() / 2) <= 60 && movesBuffer.length() >= 40)
		{
			String allMoves = movesBuffer.toString().toLowerCase();
			OBoard board = new OBoard();
			char color = C.BLACK;
			for (int i = 0; i < allMoves.length(); i += 2)
			{
				int col = char2Int(allMoves.charAt(i));
				int row = Integer.parseInt(allMoves.substring(i + 1, i + 2)) - 1;
				if (!canPlayerMove(board, color))
				{
					color = board.opponentOf(color);
				}
				if (board.canSet(row, col, color))
				{
					board.incMoveNumber();
					board.set(row, col, color);
					board.flipAll(row, col);
					color = board.opponentOf(color);
				}
				else
				{
//					System.out.println("bad moves");
					return;
				}
			}
			Score score = board.calculateScore(true);
			if (isGameOver(board))
			{
//				score.printScore();
//				System.out.println(board.getMoveNumber());
				String s = score.whoIsWinner() + " " + allMoves;
				parsedGames.add(s);
			}

		}
	}

	private boolean canPlayerMove(OBoard board, char color)
	{
		if (board.canPlayerMove(color))
		{
			return true;
		}
		return false;
	}

	private boolean isGameOver(OBoard board)
	{
		if (board.canPlayerMove(C.BLACK) || board.canPlayerMove(C.WHITE))
		{
			return false;
		}
		return true;
	}

	private int char2Int(char c)
	{
		switch (c)
		{
			case 'A':
				return 0;
			case 'a':
				return 0;
			case 'B':
				return 1;
			case 'b':
				return 1;
			case 'C':
				return 2;
			case 'c':
				return 2;
			case 'D':
				return 3;
			case 'd':
				return 3;
			case 'E':
				return 4;
			case 'e':
				return 4;
			case 'F':
				return 5;
			case 'f':
				return 5;
			case 'G':
				return 6;
			case 'g':
				return 6;
			case 'H':
				return 7;
			case 'h':
				return 7;
			default:
				return -1;
		}
	}

	private enum State
	{
		START, MOVE, MOVEVALUE1, MOVEVALUE2, END;
	}

	private void fillFilenameList()
	{
		filenames = new File(path).list();
	}

	public static void main(String[] args)
	{
		new GGSParser();
	}
}
