package xofparse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmtothello.C;
import mmtothello.OBoard;
import mmtothello.Score;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author ODB
 */
public class XOFParser
{

	private static final String TYPE = "normal";
	private static final String DIMENSION = "othello-8x8";
	private List<String> parsedGames;
	private int count = 0;

	public XOFParser()
	{
		parsedGames = new ArrayList<String>();
		parseXML("woc2006.xml");
		parseXML("woc2005.xml");
//		parseXML("woc2004.xml");
//		parseXML("woc2003.xml");
//		parseXML("yahoo-japan.xml");
		System.out.println(count);
		PrintWriter outStream;
		try
		{
			outStream = new PrintWriter(new FileOutputStream(new File("championshipgames.txt"), true));

			for (String s : parsedGames)
			{
				outStream.println(s);
			}
			outStream.close();
			System.out.println(parsedGames.size());
		}
		catch (FileNotFoundException ex)
		{
			Logger.getLogger(XOFParser.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void parseXML(String filename)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new FileInputStream(filename));
			Element root = doc.getRootElement();
			List children = root.getChildren();
			for (Object o : children)
			{
				Element element = (Element) o;
				if (element.getName().equals("game-collection"))
				{
					List games = element.getChildren();
					for (Object game : games)
					{
						parseGame((Element) game);
					}
				}
			}
			Element gameCollection = root.getChild("game-collection");
//			if(gameCollection == null)
//			{
//				System.out.println("null!");
//			}
//			List games = gameCollection.getChildren();
//			for (Object game : games)
//			{
//				parseGame((Element) game);
//			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void parseGame(Element game)
	{
		count++;
		Element result = game.getChild("result");
		Element moves = game.getChild("moves");
		String type = result.getAttributeValue("type");
		String dimension = moves.getAttributeValue("game");
		String expectedScore = game.getValue().substring(0, 5);
		int expectedBlackScore = Integer.parseInt(expectedScore.substring(0, 2));
		int expectedWhiteScore = Integer.parseInt(expectedScore.substring(3, 5));

		if (dimension.equals(DIMENSION) && type.equals(TYPE))
		{
			String winner = result.getAttributeValue("winner");
			String allMoves = moves.getValue();

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
				board.set(row, col, color);
				board.flipAll(row, col);
				color = board.opponentOf(color);
			}
			Score score = board.calculateScore(true);
			if (score.getBlackScore() != expectedBlackScore || score.getWhiteScore() != expectedWhiteScore)
			{
				System.out.println("black = " + score.getBlackScore() + ", expected = " + expectedBlackScore + ", white = " + score.getWhiteScore() + ", expected = " + expectedWhiteScore);
			}
			String s = score.whoIsWinner() + " " + allMoves;
			parsedGames.add(s);
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

	public static void main(String[] args)
	{
		new XOFParser();
	}
}
