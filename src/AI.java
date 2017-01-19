import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class AI implements ActionListener {
	
	protected Board board;
	private Timer timer;
	
	public void run(int delay) {
		if (!board.checkGameOver()) {
			timer = new Timer(delay, this);
			timer.start();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		move();
		if (board.checkGameOver())
			timer.stop();
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}

	protected abstract void move();
}
