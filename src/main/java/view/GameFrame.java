package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import model.Board;
import model.ScoringType;
import model.Stone;
import controller.GameController;
import ClientandServer.GoClient;

import javax.swing.*;
import java.awt.*;


/*
 * GameFrame.java
 *
 * Represents the main game window where players interact with the Go board.
 * Includes score display, move list, control buttons (pass, undo, reset, surrender),
 * and manages updates to the game state and turn information.
 */

public class GameFrame {

    private JPanel mainPanel;
    private JLabel blackStats;
    private JLabel whiteStats;
    private JLabel turnLabel;
    private BoardPanel boardPanel;
    private GameController controller;
    private JList<String> moveList;

    private JButton passButton;
    private JButton undoButton;
    private JButton resetButton;
    private JButton surrenderButton;

    private boolean isOnline;

    /**
     * Constructs the full game UI, including board, buttons, stats, and layout setup.
     *
     * @param parentFrame  The parent JFrame to embed the panel
     * @param scoringType  The scoring system used in the game
     * @param boardSize    The size of the Go board (e.g., 9, 13, 19)
     * @param komi         Komi value given to white
     * @param isOnline     Whether the game is online or offline
     * @param client       The GoClient instance (null for offline mode)
     */
    public GameFrame(JFrame parentFrame, ScoringType scoringType, int boardSize, double komi, boolean isOnline, GoClient client) {
        this.isOnline = isOnline;

        // Initialize board and controller
        Board board = new Board(boardSize);
        boardPanel = new BoardPanel(board, this, scoringType, komi, isOnline, client);
        controller = boardPanel.getController();

        // Turn label at top
        turnLabel = new JLabel(" Yükleniyor...");
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        turnLabel.setForeground(new Color(40, 40, 40));
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(247, 241, 225));
        topPanel.add(turnLabel, BorderLayout.CENTER);

        // Control buttons
        passButton = createStyledButton("Pas Geç");
        passButton.addActionListener(e -> boardPanel.sendPass());

        undoButton = createStyledButton("️Geri Al");
        undoButton.addActionListener(e -> boardPanel.sendUndo());

        resetButton = createStyledButton("Sıfırla");
        resetButton.addActionListener(e -> boardPanel.sendReset());

        surrenderButton = createStyledButton("Pes Et");
        surrenderButton.addActionListener(e -> boardPanel.sendSurrender());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(247, 241, 225));
        buttonPanel.add(passButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(surrenderButton);

        // Score display
        blackStats = new JLabel();
        whiteStats = new JLabel();
        styleLabel(blackStats, Color.BLACK);
        styleLabel(whiteStats, new Color(60, 60, 60));

        JPanel scorePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        scorePanel.setBackground(new Color(247, 241, 225));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Skorlar"));
        scorePanel.add(blackStats);
        scorePanel.add(whiteStats);

        // Move list
        moveList = new JList<>(controller.getMoveListModel());
        moveList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane moveScroll = new JScrollPane(moveList);
        moveScroll.setBorder(BorderFactory.createTitledBorder("Hamleler"));
        moveScroll.setPreferredSize(new Dimension(200, 300));
        moveScroll.setBackground(new Color(255, 255, 250));

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBackground(new Color(247, 241, 225));
        eastPanel.add(scorePanel, BorderLayout.NORTH);
        eastPanel.add(moveScroll, BorderLayout.CENTER);

        // Layout the entire content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(247, 241, 225));
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        contentPanel.add(eastPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(247, 241, 225));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        updateStats();
        updateTurnLabel();
        parentFrame.pack();

        // Set minimum size based on board dimensions
        int minSize = boardSize * 45;
        parentFrame.setMinimumSize(new Dimension(minSize + 250, minSize + 100));
    }

    /**
     * Creates a custom styled JButton.
     *
     * @param text The text to display on the button
     * @return JButton with consistent style
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(189, 215, 238));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    /**
     * Applies consistent font and color to a score label.
     */
    private void styleLabel(JLabel label, Color color) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(color);
    }

    /**
     * Updates the score labels based on current captures and komi.
     */
    public void updateStats() {
        int blackTotal = controller.getTotalScore(Stone.BLACK);
        int whiteTotal = controller.getTotalScore(Stone.WHITE);
        int blackEsir = controller.getBlackCaptures();
        int whiteEsir = controller.getWhiteCaptures();
        double komi = controller.getKomi();

        blackStats.setText(" Siyah ➤ Puan: " + blackTotal + " (Esir: " + blackEsir + ")");
        whiteStats.setText(" Beyaz ➤ Puan: " + whiteTotal + " (Esir: " + whiteEsir + ") + Komi: " + komi);
    }

    /**
     * Updates the turn label and enables/disables buttons based on whose turn it is.
     */
    public void updateTurnLabel() {
        Stone current = controller.getCurrentPlayer();
        Stone myColor = boardPanel.getMyColor();
        boolean myTurn = current == myColor;

        if (myTurn) {
            turnLabel.setText("Sıra sende!");
        } else {
            turnLabel.setText("Rakip oynuyor...");
        }

        if (isOnline) {
            passButton.setEnabled(myTurn);
            surrenderButton.setEnabled(myTurn);
        } else {
            passButton.setEnabled(true);
            undoButton.setEnabled(true);
            resetButton.setEnabled(true);
            surrenderButton.setEnabled(true);
        }
    }

    /**
     * Returns the main JPanel that represents the game screen.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Displays the Game Over screen showing the winner and scoring type.
     *
     * @param winner The winner text (e.g., "Siyah", "Beyaz")
     */
    public void showGameOverScreen(String winner) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        if (topFrame != null) {
            ScoringType type = boardPanel.getController().getScoringType();
            topFrame.setContentPane(new EndScreen(topFrame, winner, type));
            topFrame.revalidate();
            topFrame.repaint();
        }
    }

    /**
     * Returns the BoardPanel instance containing the board and stone interactions.
     */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}

