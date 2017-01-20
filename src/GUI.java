import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

public class GUI implements Observer {

	private Board model;
	private JFrame frame;
	private JPanel content;
	private JPanel scorePanel;
	private JLabel scoreLabel;
	private JPanel board;
	private JPanel gameOverPanel;
	private ArrowKeyListener keyListener;
	private Colours colours;
	private Set<BoardPanel> boardPanels;
	private AI ai;
	private static final int aiDelay = 20;

	public GUI() {
		model = new Board();
		model.addObserver(this);
		ai = new RubbishAI();
		this.colours = new Colours();
		frame = new JFrame("2048");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keyListener = new ArrowKeyListener(this, model);
		frame.addKeyListener(keyListener);
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
		gameOverPanel = new JPanel();
		gameOverPanel.setBackground(new Color(255, 255, 255, 100));
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.CENTER;
		gbl.setConstraints(gameOverPanel, gbc);
		gameOverPanel.setLayout(gbl);
		JLabel gameOverLabel = new JLabel("Game over!");
		gameOverLabel.setFont(
				new Font(gameOverLabel.getFont().getName(), Font.PLAIN, gameOverLabel.getFont().getSize() * 4));
		gameOverPanel.add(gameOverLabel);
		frame.setGlassPane(gameOverPanel);
		addMenuBar();
		frame.pack();
		frame.setVisible(true);
		update(null, null);
	}

	private void newGame() {
		gameOverPanel.setVisible(false);
		model.reset();
		keyListener.setEnabled(true);
		update(null, null);
	}
	
	public void moveAI() {
		keyListener.setEnabled(false);
		ai.setBoard(model);
		ai.move();
		keyListener.setEnabled(true);
	}

	private void runAI() {
		keyListener.setEnabled(false);
		ai.setBoard(model);
		ai.run(aiDelay);
	}

	private void addMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		JMenuItem item = new JMenuItem("New");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newGame();
			}
		});
		menu.add(item);
		menubar.add(menu);
		menu = new JMenu("AI");
		item = new JMenuItem("Move");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveAI();
			}
		});
		menu.add(item);
		item = new JMenuItem("Run");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runAI();
			}
		});
		menu.add(item);
		JMenu submenu = new JMenu("Change AI");
		JRadioButtonMenuItem rubbishAiButton = new JRadioButtonMenuItem("Rubbish AI");
		rubbishAiButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if (button.isSelected())
					ai = new RubbishAI();
			}
		});
		submenu.add(rubbishAiButton);
		JRadioButtonMenuItem greedyAiButton = new JRadioButtonMenuItem("Greedy AI");
		greedyAiButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if (button.isSelected()) {
					ai = new GreedyAI(2);
				}
			}
		});
		submenu.add(greedyAiButton);
		JRadioButtonMenuItem cautiousAiButton = new JRadioButtonMenuItem("Cautious AI");
		cautiousAiButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if (button.isSelected())
					ai = new CautiousAI(1);
			}
		});
		submenu.add(cautiousAiButton);
		JRadioButtonMenuItem probAiButton = new JRadioButtonMenuItem("Probabalistic AI");
		probAiButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if (button.isSelected())
					ai = new ProbabalisticAI();
			}
		});
		submenu.add(probAiButton);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rubbishAiButton);
		bg.add(greedyAiButton);
		bg.add(cautiousAiButton);
		bg.add(probAiButton);
		probAiButton.setSelected(true);
		menu.add(submenu);
		menubar.add(menu);
		frame.setJMenuBar(menubar);
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
	
	private void updateScore() {
		scoreLabel.setText(Integer.toString(model.getScore()) + " - " + Integer.toString(model.getGrade()) + "%");
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
		updateScore();
		if (arg1 != null && arg1.equals("end")) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					gameOverPanel.setVisible(true);
				}
			});
		}
	}

}
