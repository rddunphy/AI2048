
public class Move {

	private Direction dir;
	private int score;
	
	public Move(Direction dir, int score) {
		this.setDir(dir);
		this.setScore(score);
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
