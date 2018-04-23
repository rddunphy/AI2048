package ai;

import model.Board;
import model.Direction;
import model.Square;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProbabalisticAI extends AI {

	private static final int maxdepth = 5;
	private static final double freeSquaresWeight = 0.2;
	private static final double cornerTileWeight = 0;
	private static final double edgeTilesWeight = 0.2;
	private static final double monotonicityWeight = 1;

	private int totalPosCounter;
	private int movePosCounter;

	public ProbabalisticAI() {
		totalPosCounter = 0;
	}

	@Override
	public void move() {
		movePosCounter = 0;
		System.out.println("____________");
		double score = -1;
		Direction move = null;
		List<Direction> available = board.availableMoves();
		for (Direction m : available) {
			Board nb = new Board(board);
			nb.move(m, false);
			double s = evaluateMoveScore(nb, maxdepth);
			if (s > score) {
				score = s;
				move = m;
			}
		}
		board.move(move);
		totalPosCounter += movePosCounter;
		System.out.println("Evaluated " + movePosCounter + " positions (total " + totalPosCounter + ")");
	}

	private double evaluateMoveScore(Board b, int d) {
		List<Square> free = b.getFreeSquares();
		d = limitDepth(d, free.size());
		double score = 0;
		for (Square s : free) {
			// place 2 in s
			b.put(s.row, s.col, 2);
			score += 0.9 * evaluatePositionScore(b, d);
			// place 4 in s
			b.put(s.row, s.col, 4);
			score += 0.1 * evaluatePositionScore(b, d);
			// place 0 in s
			b.put(s.row, s.col, 0);
		}
		return score / free.size();
	}

	private double evaluatePositionScore(Board b, int d) {
		if (b.checkGameOver()) {
			movePosCounter++;
			return 0;
		}
		if (d == 0) {
			return evaluateHeuristics(b);
		} else {
			double score = -1;
			List<Direction> available = b.availableMoves();
			for (Direction m : available) {
				Board nb = new Board(b);
				nb.move(m, false);
				double s = evaluateMoveScore(nb, d - 1);
				if (s > score)
					score = s;
			}
			return score;
		}
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

	private int limitDepth(int d, int free) {
		int limit = 5;
		if (free > 0)
			limit = 4;
		if (free > 1)
			limit = 3;
		if (free > 3)
			limit = 2;
		if (free > 8)
			limit = 1;
		return d < limit ? d : limit;
	}

}
