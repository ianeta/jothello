package GenericParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import mmtothello.OBoard;

public class GenericParser
{

	private String[] filenames;
	private String path = "./generic/";

	public GenericParser()
	{
		fillFilenameList();
		for (String filename : filenames)
		{
			parseFile(path + filename);
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
				GameMoveList game = createGameMoveList(lines[0], lines[1]);
				line = reader.readLine();
				
			}
			reader.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private GameMoveList createGameMoveList(String winner, String moves)
	{
		List<OBoard> boards = new ArrayList<OBoard>();
		OBoard initialBoard = new OBoard();

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
	}

	public static void main(String[] args)
	{
		new GenericParser();
	}
}
