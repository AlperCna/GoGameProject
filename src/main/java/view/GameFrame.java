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
import model.Stone;

import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private JPanel mainPanel;
    private JLabel blackStats;
    private JLabel whiteStats;
    private BoardPanel boardPanel;

    public GameFrame(JFrame parentFrame) {
        Board board = new Board();
        boardPanel = new BoardPanel(board, this);

        JButton passButton = new JButton("✋ Pas Geç");
        passButton.setFont(new Font("Arial", Font.BOLD, 14));
        passButton.addActionListener(e -> boardPanel.passMove());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(passButton);

        blackStats = new JLabel();
        whiteStats = new JLabel();
        styleLabel(blackStats, Color.BLACK);
        styleLabel(whiteStats, Color.DARK_GRAY);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(5, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder("🏁 Skorlar"));
        scorePanel.add(blackStats);
        scorePanel.add(whiteStats);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(scorePanel, BorderLayout.EAST);

        updateStats();
    }

    private void styleLabel(JLabel label, Color color) {
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(color);
    }

    public void updateStats() {
        var controller = boardPanel.getController();
        int blackCaptures = controller.getBlackCaptures();
        int whiteCaptures = controller.getWhiteCaptures();
        int blackTerritory = controller.calculateTerritory(Stone.BLACK);
        int whiteTerritory = controller.calculateTerritory(Stone.WHITE);
        int blackTotal = controller.getTotalScore(Stone.BLACK);
        int whiteTotal = controller.getTotalScore(Stone.WHITE);

        blackStats.setText("⚫ Siyah ➤ Esir: " + blackCaptures +
                ", Alan: " + blackTerritory + ", Toplam: " + blackTotal);
        whiteStats.setText("⚪ Beyaz ➤ Esir: " + whiteCaptures +
                ", Alan: " + whiteTerritory + ", Toplam: " + whiteTotal);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void showGameOverScreen(String winner) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        topFrame.setContentPane(new EndScreen(topFrame, winner));
        topFrame.revalidate();
        topFrame.repaint();
    }
}
