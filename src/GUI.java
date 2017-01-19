import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GUI implements Observer {

	private Board model;
	private JFrame frame;
	private JPanel content;
	private JPanel scorePanel;
	private JLabel scoreLabel;
	private JPanel board;
	private Colours colours;
	private Set<BoardPanel> boardPanels;
	
	public GUI() {
		model = new Board();
		model.addObserver(this);
		this.colours = new Colours();
		frame = new JFrame("2048");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()) {
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
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		content = new JPanel();
		content.setLayout(new BorderLayout());
		frame.setContentPane(content);
		scorePanel = new JPanel();
		scoreLabel = new JLabel();
		scorePanel.add(scoreLabel);
		scoreLabel.setText(Integer.toString(model.getScore()));
		content.add(scorePanel, BorderLayout.NORTH);
		board = new JPanel();
		populateBoard();
		content.add(board, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		update(null, null);
		AI ai = new RubbishAI(model);
		ai.start();
	}

	private void populateBoard() {
		boardPanels = new HashSet<>();
		board.setLayout(new GridLayout(4, 4));
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				BoardPanel p = new BoardPanel(r, c);
				boardPanels.add(p);
				board.add(p);
			}
		}
	}

	@SuppressWarnings("serial")
	class BoardPanel extends JPanel {

		private JLabel number;
		private int row;
		private int col;

		public BoardPanel(int row, int col) {
			this.row = row;
			this.col = col;
			this.setPreferredSize(new Dimension(100, 100));
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.CENTER;
			gbl.setConstraints(this, gbc);
			this.setLayout(gbl);
			number = new JLabel();
			number.setFont(new Font(number.getFont().getName(), Font.PLAIN, number.getFont().getSize() * 2));
			this.add(number);
			update();
		}
		
		public void update() {
			int n = model.get(row, col);
			if (n == 0) {
				number.setText("");
				this.setBackground(null);
			} else {
				number.setText(Integer.toString(n));
				Color c = colours.getColour(n);
				this.setBackground(c);
				if (c.getRed() + c.getGreen() + c.getBlue() < 350)
					number.setForeground(Color.WHITE);
				else
					number.setForeground(Color.BLACK);
			}
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				new GUI();
		    }
		});
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		for (BoardPanel p : boardPanels) {
			p.update();
		}
		scoreLabel.setText(Integer.toString(model.getScore()) + " - " + Integer.toString(model.getGrade()));
		if (arg1 != null && arg1.equals("end"))
			JOptionPane.showMessageDialog(frame, "Game over!");
	}

}
