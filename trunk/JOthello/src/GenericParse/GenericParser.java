package GenericParse;

import backprop.OthelloTraining;
import backprop.OthelloTrainingMoveHistory;
import backprop.Player;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import mmtothello.C;
import mmtothello.OBoard;

public class GenericParser
{

	private String[] filenames;
	private String path = "./generic/";
	private int gameCount = 0;
	private int drawGame = 0;
	private OthelloTrainingMoveHistory othelloTraining;

	public GenericParser()
	{
		othelloTraining = new OthelloTrainingMoveHistory();
		fillFilenameList();
		for (String filename : filenames)
		{
			if (filename.endsWith("txt"))
			{
				parseFile(path + filename);
			}
		}
	}

	private void fillFilenameList()
	{
		filenames = new File(path).list();
	}

	private void parseFile(String filename)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			String line = reader.readLine();
			while (line != null)
			{
				String[] lines = line.split(" ");
				if (lines.length == 2)
				{
					GameMoveList game = createGameMoveList(lines[0], lines[1]);
					if (game != null)
					{
						//place it in the machine learning bad boy
						othelloTraining.addGame(game.getMoves(), game.getWinner());
						gameCount++;
					}
					line = reader.readLine();
				}
			}
//			System.out.println(gameCount + " " + drawGame);
//			System.out.println(gameCount + drawGame);
			reader.close();
			othelloTraining.runTraining("moveHistoryOld");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private GameMoveList createGameMoveList(String winner, String moves)
	{
		List<OBoard> boards = new ArrayList<OBoard>();
		OBoard board = new OBoard();
		boards.add(board);
		char color = C.BLACK;

		for (int i = 0; i < moves.length(); i += 2)
		{
			int col = char2Int(moves.charAt(i));
			int row = Integer.parseInt(moves.substring(i + 1, i + 2)) - 1;
			if (!canPlayerMove(board, color))
			{
				color = board.opponentOf(color);
			}
			if (board.canSet(row, col, color))
			{
				board = new OBoard(board);
				board.set(row, col, color);
				board.flipAll(row, col);
				color = board.opponentOf(color);
				boards.add(board);
			}
			else
			{
				//this shouldn't happen if the input file is good
				System.out.println("DOH!");
				return null;
			}
		}

		Player winningPlayer = null;
		if (winner.equals("black"))
		{
			winningPlayer = Player.BLACK;
		}
		else if (winner.equals("white"))
		{
			winningPlayer = Player.WHITE;
		}

		if (winningPlayer != null)
		{
			//this is the situation we want
			return new GameMoveList(boards, winningPlayer);
		}

		//if we get here, it's likely a draw game
		drawGame++;
		return null;
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

	private boolean canPlayerMove(OBoard board, char color)
	{
		if (board.canPlayerMove(color))
		{
			return true;
		}
		return false;
	}

	public static void main(String[] args)
	{
		new GenericParser();
	}
}
