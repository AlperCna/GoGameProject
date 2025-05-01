package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Alper
 */


import model.Board;
import model.ScoringType;
import model.Stone;
import controller.GameController;
import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private JPanel mainPanel;
    private JLabel blackStats;
    private JLabel whiteStats;
    private BoardPanel boardPanel;
    private GameController controller;

    public GameFrame(JFrame parentFrame, ScoringType scoringType) {
        Board board = new Board();
        boardPanel = new BoardPanel(board, this, scoringType);
        controller = boardPanel.getController();

        JButton passButton = new JButton("✋ Pas Geç");
        passButton.setFont(new Font("Arial", Font.PLAIN, 14));
        passButton.addActionListener(e -> boardPanel.passMove());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(passButton);

        blackStats = new JLabel();
        whiteStats = new JLabel();
        styleLabel(blackStats, Color.BLACK);
        styleLabel(whiteStats, Color.DARK_GRAY);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(3, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder("🏁 Skorlar"));
        scorePanel.add(blackStats);
        scorePanel.add(whiteStats);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        contentPanel.add(scorePanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        updateStats();
        parentFrame.pack(); // pencere boyutunu içeriğe göre ayarla
        parentFrame.setMinimumSize(new Dimension(700, 700)); // garanti genişlik
    }

    private void styleLabel(JLabel label, Color color) {
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(color);
    }

    public void updateStats() {
        int blackTotal = controller.getTotalScore(Stone.BLACK);
        int whiteTotal = controller.getTotalScore(Stone.WHITE);
        int blackEsir = controller.getBlackCaptures();
        int whiteEsir = controller.getWhiteCaptures();

        blackStats.setText("⚫ Siyah ➤ Puan: " + blackTotal + " (Esir: " + blackEsir + ")");
        whiteStats.setText("⚪ Beyaz ➤ Puan: " + whiteTotal + " (Esir: " + whiteEsir + ")");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void showGameOverScreen(String winner) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        ScoringType type = boardPanel.getController().getScoringType();
        topFrame.setContentPane(new EndScreen(topFrame, winner, type));
        topFrame.revalidate();
        topFrame.repaint();
    }
}


