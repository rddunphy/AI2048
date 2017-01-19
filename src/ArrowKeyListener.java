import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ArrowKeyListener implements KeyListener {

	private Board model;
	private boolean enabled;

	public ArrowKeyListener(Board model) {
		this.model = model;
		this.enabled = true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (enabled) {
			switch (e.getKeyCode()) {
			case 37: // left
				model.move(Direction.LEFT);
				break;
			case 38: // up
				model.move(Direction.UP);
				break;
			case 39: // right
				model.move(Direction.RIGHT);
				break;
			case 40: // down;
				model.move(Direction.DOWN);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
