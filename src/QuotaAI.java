import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QuotaAI extends AI {

	private static final int quota = 100000;
	private static final double freeSquaresWeight = 0.2;
	private static final double cornerTileWeight = 0;
	private static final double edgeTilesWeight = 0.2;
	private static final double monotonicityWeight = 1;

	private int totalPosCounter;
	private int movePosCounter;
	private int moveCounter;
	private int maxPosCounterValue;
	private int minPosCounterValue;

	public QuotaAI() {
		totalPosCounter = 0;
		moveCounter = 0;
		maxPosCounterValue = -1;
		minPosCounterValue = -1;
	}

	@Override
	protected void move() {
		movePosCounter = 0;
		System.out.println("____________");
		double score = -1;
		Direction move = null;
		List<Direction> available = board.availableMoves();
		for (Direction m : available) {
			Board nb = new Board(board);
			nb.move(m, false);
			double s = evaluateMoveScore(nb, quota / available.size());
			if (s > score) {
				score = s;
				move = m;
			}
		}
		board.move(move);
		doStats();
	}
	
	private void doStats() {
		totalPosCounter += movePosCounter;
		moveCounter++;
		if (movePosCounter < minPosCounterValue || minPosCounterValue < 0)
			minPosCounterValue = movePosCounter;
		if (movePosCounter > maxPosCounterValue || maxPosCounterValue < 0)
			maxPosCounterValue = movePosCounter;
		System.out.println("Move " + moveCounter + " complete");
		System.out.println("Evaluated " + movePosCounter + " positions (total " + totalPosCounter + ")");
		System.out.println("Positions evaluated: [" + minPosCounterValue + ", " + totalPosCounter / moveCounter + ", " + maxPosCounterValue + "]");
	}

	private double evaluateMoveScore(Board b, int q) {
		List<Square> free = b.getFreeSquares();
		if (q < 2 * free.size()) {
			return -1;
		}
		q /= 2 * free.size();
		double score = 0;
		for (Square s : free) {
			// place 2 in s
			b.put(s.row, s.col, 2);
			score += 0.9 * evaluatePositionScore(b, q);
			// place 4 in s
			b.put(s.row, s.col, 4);
			score += 0.1 * evaluatePositionScore(b, q);
			// place 0 in s
			b.put(s.row, s.col, 0);
		}
		return score / free.size();
	}

	private double evaluatePositionScore(Board b, int q) {
		if (b.checkGameOver()) {
			movePosCounter++;
			return 0;
		}
		double score = -1;
		List<Direction> available = b.availableMoves();
		//if (q < available.size())
			//return evaluateHeuristics(b);
		q /= available.size();
		for (Direction m : available) {
			Board nb = new Board(b);
			nb.move(m, false);
			double s = evaluateMoveScore(nb, q);
			if (s < 0)
				return evaluateHeuristics(b); // quota exceeded
			if (s > score)
				score = s; // maximise over all possible move scores
		}
		return score;
	}

	private double evaluateHeuristics(Board b) {
		movePosCounter++;
		double score = freeSquaresScore(b) * freeSquaresWeight;
		score += cornerTileScore(b) * cornerTileWeight;
		score += edgeTilesScore(b) * edgeTilesWeight;
		score += monotonicityScore(b) * monotonicityWeight;
		score /= freeSquaresWeight + cornerTileWeight + edgeTilesWeight + monotonicityWeight;
		return score;
	}

	private double freeSquaresScore(Board b) {
		return ((double) 1 + b.getFreeSquares().size()) / 16;
	}

	/**
	 * Bonus if largest tile is in corner
	 * 
	 * @param b
	 * @return
	 */
	private double cornerTileScore(Board b) {
		int highestCorner = 0;
		if (b.get(0, 0) > highestCorner)
			highestCorner = b.get(0, 0);
		if (b.get(3, 0) > highestCorner)
			highestCorner = b.get(3, 0);
		if (b.get(0, 3) > highestCorner)
			highestCorner = b.get(0, 3);
		if (b.get(3, 3) > highestCorner)
			highestCorner = b.get(3, 3);
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				if (b.get(r, c) > highestCorner)
					return 0;
			}
		}
		return 1;
	}

	private double edgeTilesScore(Board b) {
		double score = 0;
		Map<Square, Integer> tiles = new HashMap<>();
		for (Square s : b.getTileSquares()) {
			tiles.put(s, b.get(s.row, s.col));
		}
		List<Square> l = new LinkedList<>(tiles.keySet());
		Collections.sort(l, new Comparator<Square>() {
			@Override
			public int compare(Square s1, Square s2) {
				return tiles.get(s2).compareTo(tiles.get(s1));
			}

		});
		if (isEdgeTile(l.get(0)))
			score += 0.4;
		if (l.size() > 1 && isEdgeTile(l.get(1)))
			score += 0.3;
		if (l.size() > 2 && isEdgeTile(l.get(2)))
			score += 0.2;
		if (l.size() > 3 && isEdgeTile(l.get(3)))
			score += 0.1;
		return score;
	}

	private double monotonicityScore(Board b) {
		// for each row and each column, calculate the total value, and how
		// monotonic it is. Multiply monotonicity by value, divide by total
		// value of all rows and columns (2 * total value of all squares)
		double score = 0;
		int totalvalue = 0;
		for (int r = 0; r < 4; r++) {
			int rowvalue = 0;
			int[] rowvalues = new int[4];
			for (int c = 0; c < 4; c++) {
				rowvalue += b.get(r, c);
				rowvalues[c] = b.get(r, c);
			}
			totalvalue += rowvalue;
			score += rowvalue * calculateMonotonicity(rowvalues);
		}
		for (int c = 0; c < 4; c++) {
			int colvalue = 0;
			int[] colvalues = new int[4];
			for (int r = 0; r < 4; r++) {
				colvalue += b.get(r, c);
				colvalues[r] = b.get(r, c);
			}
			totalvalue += colvalue;
			score += colvalue * calculateMonotonicity(colvalues);
		}
		score /= totalvalue;
		return score;
	}

	private double calculateMonotonicity(int[] row) {
		int[] diff = new int[3];
		diff[0] = Integer.compare(row[0], row[1]);
		diff[1] = Integer.compare(row[1], row[2]);
		diff[2] = Integer.compare(row[2], row[3]);
		boolean increasing = false;
		boolean decreasing = false;
		for (int i : diff) {
			if (i > 0)
				increasing = true;
			else if (i < 0)
				decreasing = true;
		}
		return (increasing && decreasing) ? 0 : 1;
	}

	private boolean isEdgeTile(Square s) {
		if (s.col == 0 || s.col == 3 || s.row == 0 || s.row == 3)
			return true;
		return false;
	}

}
