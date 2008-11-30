package GenericParse;

import backprop.Player;
import java.util.List;
import mmtothello.OBoard;

public class GameMoveList
{
	private final Player winner;
	private final List<OBoard> moves;

	public GameMoveList(List<OBoard> moves, Player winner)
	{
		this.winner = winner;
		this.moves = moves;
	}

	public Player getWinner()
	{
		return winner;
	}

	public List<OBoard> getMoves()
	{
		return moves;
	}
}
