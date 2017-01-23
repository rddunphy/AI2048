
public class AITester {

	private AI ai;
	
	public AITester(AI ai) {
		this.ai = ai;
	}
	
	public static void main(String[] args) {
		AITester tester = new AITester(new ProbabalisticAI());
		tester.run(100);
	}
	
	public void run(int n) {
		double score = 0;
		for (int i = 0; i < n; i++) {
			score += runOnce();
		}
		score /= n;
		System.out.println("Test complete. Score over " + n + " games: " + score);
	}
	
	public int runOnce() {
		Board board = new Board();
		ai.setBoard(board);
		while (!board.checkGameOver()) {
			ai.move();
		}
		System.out.println("Game over. Score: " + board.getScore());
		return board.getScore();
	}
}
