import java.util.List;

public class RubbishAI extends AI {

	public RubbishAI(Board b) {
		super(b);
	}

	protected void move() {
		if (!board.checkGameOver()) {
			List<Direction> moves = board.availableMoves();
			if (moves.contains(Direction.LEFT))
				board.move(Direction.LEFT);
			else if (moves.contains(Direction.UP))
				board.move(Direction.UP);
			else if (moves.contains(Direction.RIGHT))
				board.move(Direction.RIGHT);
			else
				board.move(Direction.DOWN);
		}
	}


}
