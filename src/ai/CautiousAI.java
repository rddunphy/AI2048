package ai;

import model.Board;
import model.Direction;
import model.Move;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CautiousAI extends AI {
	
	private int depth;
	
	public CautiousAI(int depth) {
		this.depth = depth;
	}

	@Override
	public void move() {
		if (!board.checkGameOver()) {
			board.move(findBestMove(board, depth).getDir());
		}
	}

	private Move findBestMove(Board b, int d) {
		List<Direction> available = b.availableMoves();
		Set<Move> moves = new HashSet<>();
		for (Direction dir : available) {
			Board nb = new Board(b);
			nb.move(dir, false);
			if (d == 0) {
				moves.add(new Move(dir, nb.getFreeSquares().size()));
			} else {
				moves.add(new Move(dir, findBestMove(nb, d - 1).getScore()));
			}
		}
		return highestScoring(moves);
	}

	private Move highestScoring(Set<Move> moves) {
		int score = -1;
		Move move = null;
		for (Move m : moves) {
			if (m.getScore() > score) {
				move = m;
				score = m.getScore();
			}
		}
		return move;
	}
	
}
