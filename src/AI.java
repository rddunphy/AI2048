import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class AI implements ActionListener {
	
	protected Board board;
	private Timer timer;
	
	public AI (Board b) {
		board = b;
	}
	
	public void start() {
		if (!board.checkGameOver()) {
			timer = new Timer(10, this);
			timer.start();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		move();
		if (board.checkGameOver())
			timer.stop();
	}

	protected abstract void move();
}
