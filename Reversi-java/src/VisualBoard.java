import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import game.Board;
import PengWin_Package.*;

public class VisualBoard implements ActionListener {

    public JFrame frame;
    public JPanel boardPanel;
    public JButton[][] boardButtons;
    public Board reversi = new Board();
    private JLabel scoreLabel;
    
    public Bot_01 penguin = new Bot_01();
    private boolean playerIsWhite = false; // Default player color is black
    
    public Color color = Color.decode("#21788a");
    
    private HashMap<Character, ImageIcon> iconMap;
    
    public VisualBoard() {
        frame = new JFrame("Reversi");
        boardPanel = new JPanel(new GridLayout(8, 8));
        boardButtons = new JButton[8][8];
        
        loadPiece();
        initializeBoardButtons();
        
        scoreLabel = new JLabel("White 2-2 Black");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
     // Add color selection interface
        JPanel colorSelectionPanel = new JPanel();
        colorSelectionPanel.setLayout(new FlowLayout());
        colorSelectionPanel.add(new JLabel("Choose your color:"));

        JButton whiteButton = new JButton("White");
        whiteButton.addActionListener(e -> setPlayerColor(true));
        colorSelectionPanel.add(whiteButton);

        JButton blackButton = new JButton("Black");
        blackButton.addActionListener(e -> setPlayerColor(false));
        colorSelectionPanel.add(blackButton);
        
        blackButton.setSelected(true);

        frame.add(colorSelectionPanel, BorderLayout.SOUTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(scoreLabel, BorderLayout.NORTH);
        
        frame.setSize(600, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        
    }
    
    private ImageIcon loadIcon(String pieceName) {
        // Handle potential loading errors with try-catch
        try {
        	ImageIcon icon = new ImageIcon(getClass().getResource("/pieces/" + pieceName));
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.out.println("Error loading icon: " + pieceName);
            return null;
        }
    }
    
    private void loadPiece() {
        iconMap = new HashMap<>();
        
        iconMap.put('b', loadIcon("new-moon.png")); // Black piece icon
        iconMap.put('w', loadIcon("rec.png")); // White piece icon
        iconMap.put('+', null); // Empty square, no icon
    }
    
    private void initializeBoardButtons() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                
                // Set the initial piece (empty by default)
                char piece = reversi.getBoard(row, col); 
                button.setIcon(iconMap.get(piece));
                
                // Set button background color
                button.setBackground(color);
                
                // Add action listener
                button.addActionListener(this);
                
                // Store button in the array and add to panel
                boardButtons[row][col] = button;
                boardPanel.add(button);
            }
        }
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	char botColor = playerIsWhite ? 'b' : 'w';
    	if(reversi.whoseTurn() == botColor) return;
    	
        JButton clickedButton = (JButton) e.getSource();
        
        // Find button's position
        int row = -1;
        int col = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardButtons[i][j] == clickedButton) {
                    row = i;
                    col = j;
                    break;
                }
            }
            if (row != -1) break;
        }
        
    	if(!reversi.makeMove(row, col)) return;
    	
        updateVisualBoard();
        updateScoreLabel();
        
//        SwingUtilities.invokeLater(() -> {
//            while(reversi.whoseTurn() == botColor && !reversi.isGameFinished()) {
//            	int[] botMove = penguin.botMove(reversi, botColor);
//            	reversi.makeMove(botMove[0], botMove[1]);
//            	updateVisualBoard();
//                updateScoreLabel();
//                System.out.println("a");
//                if(reversi.whoseTurn() == botColor) {
//                	System.out.println("again!");
//                }
//            }
//        });
        
        startBotLoop(botColor);
    }
    
    private void startBotLoop(char botColor) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
            	
                // Bot decides the move
                int[] move = penguin.botMove(reversi, botColor);

                // Apply the move on the model (NOT on the UI thread)
                synchronized (reversi) {
                	reversi.makeMove(move[0], move[1]);
				}

                // Small delay so user sees the steps (optional)
                Thread.sleep(300);
                return null;
            }

            @Override
            protected void done() {
                updateVisualBoard();
                updateScoreLabel();
                if(reversi.whoseTurn() == botColor && !reversi.isGameFinished())
                	startBotLoop(botColor);
            }
        };

        worker.execute();
    }
    

    public void updateVisualBoard() {
		for(int row = 0; row < 8; row++)
			for(int col = 0; col < 8; col++) {
				char piece = reversi.getBoard(row, col);
				boardButtons[row][col].setIcon(iconMap.get(piece));
			}
	}
    
    private void updateScoreLabel() {
        scoreLabel.setText("White " + reversi.getCountWhite() + "-" + reversi.getCountBlack() + " Black");
    }
    
    private void setPlayerColor(boolean isWhite) {
        this.playerIsWhite = isWhite;
        
        char botColor = isWhite ? 'b' : 'w';
        
        int [] botMove = penguin.botMove(reversi, botColor);
        reversi.makeMove(botMove[0], botMove[1]);
        updateVisualBoard();
        updateScoreLabel();
    }
}

