import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class Board extends Observable {

	private int[][] state = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
	private static final int startTiles = 2;
	private static final double prob4 = 0.1;
	private int score;

	public Board() {
		score = 0;
		for (int i = 0; i < startTiles; i++) {
			placeTile();
		}
	}

	public Board(Board b) {
		score = b.getScore();
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[0].length; c++) {
				state[r][c] = b.get(r, c);
			}
		}
	}

	private void placeTile() {
		Random r = new Random();
		int val = 2;
		if (r.nextDouble() < prob4)
			val = 4;
		List<Square> free = getFreeSquares();
		Square s = free.get(r.nextInt(free.size()));
		state[s.row][s.col] = val;
		checkGameOver();
	}

	public boolean checkGameOver() {
		if (getFreeSquares().isEmpty()) {
			for (int r = 0; r < state.length; r++) {
				for (int c = 0; c < state[0].length; c++) {
					if (c < state[0].length - 1 && state[r][c] == state[r][c + 1])
						return false;
					if (r < state.length - 1 && state[r][c] == state[r + 1][c])
						return false;
				}
			}
			this.setChanged();
			notifyObservers("end");
			return true;
		}
		return false;
	}

	public List<Direction> availableMoves() {
		List<Direction> moves = new ArrayList<>();
		Board b = new Board(this);
		b.move(Direction.UP);
		if (!this.equals(b))
			moves.add(Direction.UP);
		b = new Board(this);
		b.move(Direction.DOWN);
		if (!this.equals(b))
			moves.add(Direction.DOWN);
		b = new Board(this);
		b.move(Direction.RIGHT);
		if (!this.equals(b))
			moves.add(Direction.RIGHT);
		b = new Board(this);
		b.move(Direction.LEFT);
		if (!this.equals(b))
			moves.add(Direction.LEFT);
		return moves;
	}

	private List<Square> getFreeSquares() {
		List<Square> squares = new ArrayList<>();
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[0].length; c++) {
				if (state[r][c] == 0) {
					squares.add(new Square(r, c));
				}
			}
		}
		return squares;
	}

	public void move(Direction dir) {
		int[][] oldState = new int[4][4];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				oldState[r][c] = state[r][c];
			}
		}
		shift(dir);
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (state[r][c] != oldState[r][c]) {
					placeTile();
					this.setChanged();
					notifyObservers(state);
					return;
				}
			}
		}
		// throw new IllegalMoveException("Illegal move: Can't move " + dir);
	}

	private void shift(Direction dir) {
		boolean[][] collapsed = { { false, false, false, false }, { false, false, false, false },
				{ false, false, false, false }, { false, false, false, false } };
		switch (dir) {
		case UP:
			for (int r = 1; r < state.length; r++) {
				for (int c = 0; c < state[0].length; c++) {
					if (state[r][c] != 0) {
						boolean collapse = false;
						int newrow = r;
						for (int i = r - 1; i >= 0; i--) {
							if (state[i][c] != 0) {
								if (state[i][c] == state[r][c] && !collapsed[i][c]) {
									collapsed[i][c] = true;
									collapse = true;
									newrow = i;
								}
								break;
							} else {
								newrow = i;
							}
						}
						if (newrow != r) {
							state[newrow][c] = state[r][c];
							if (collapse) {
								state[newrow][c] *= 2;
								score += state[newrow][c];
							}
							state[r][c] = 0;
						}
					}
				}
			}
			break;
		case DOWN:
			for (int r = state.length - 2; r >= 0; r--) {
				for (int c = 0; c < state[0].length; c++) {
					if (state[r][c] != 0) {
						boolean collapse = false;
						int newrow = r;
						for (int i = r + 1; i < state.length; i++) {
							if (state[i][c] != 0) {
								if (state[i][c] == state[r][c] && !collapsed[i][c]) {
									collapsed[i][c] = true;
									collapse = true;
									newrow = i;
								}
								break;
							} else {
								newrow = i;
							}
						}
						if (newrow != r) {
							state[newrow][c] = state[r][c];
							if (collapse) {
								state[newrow][c] *= 2;
								score += state[newrow][c];
							}
							state[r][c] = 0;
						}
					}
				}
			}
			break;
		case LEFT:
			for (int c = 1; c < state[0].length; c++) {
				for (int r = 0; r < state.length; r++) {
					if (state[r][c] != 0) {
						boolean collapse = false;
						int newcol = c;
						for (int i = c - 1; i >= 0; i--) {
							if (state[r][i] != 0) {
								if (state[r][i] == state[r][c] && !collapsed[r][i]) {
									collapsed[r][i] = true;
									collapse = true;
									newcol = i;
								}
								break;
							} else {
								newcol = i;
							}
						}
						if (newcol != c) {
							state[r][newcol] = state[r][c];
							if (collapse) {
								state[r][newcol] *= 2;
								score += state[r][newcol];
							}
							state[r][c] = 0;
						}
					}
				}
			}
			break;
		case RIGHT:
			for (int c = state[0].length - 2; c >= 0; c--) {
				for (int r = 0; r < state.length; r++) {
					if (state[r][c] != 0) {
						boolean collapse = false;
						int newcol = c;
						for (int i = c + 1; i < state[0].length; i++) {
							if (state[r][i] != 0) {
								if (state[r][i] == state[r][c] && !collapsed[r][i]) {
									collapsed[r][i] = true;
									collapse = true;
									newcol = i;
								}
								break;
							} else {
								newcol = i;
							}
						}
						if (newcol != c) {
							state[r][newcol] = state[r][c];
							if (collapse) {
								state[r][newcol] *= 2;
								score += state[r][newcol];
							}
							state[r][c] = 0;
						}
					}
				}
			}
			break;
		default:
			System.out.println("dkfjke");
		}
	}

	public int get(int row, int col) {
		return state[row][col];
	}

	public int getScore() {
		return score;
	}

	public int getGrade() {
		if (score < 2000)
			return 0;
		else if (score <= 32000)
			return (int) Math.round(40 + 30 * (score - 2000.0) / 30000);
		else
			return (int) Math.round(70 + 30 * (score - 32000.0) / score);
	}
	
	public void reset() {
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[0].length; c++) {
				state[r][c] = 0;
			}
		}
		score = 0;
		for (int i = 0; i < startTiles; i++) {
			placeTile();
		}
	}

	@Override
	public String toString() {
		String out = "";
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[0].length; c++) {
				out += state[r][c] + "\t";
			}
			out += "\n";
		}
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != Board.class)
			return false;
		Board b = (Board) o;
		for (int r = 0; r < state.length; r++) {
			for (int c = 0; c < state[0].length; c++) {
				if (state[r][c] != b.state[r][c])
					return false;
			}
		}
		return true;
	}
}
