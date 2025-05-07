package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import model.Board;
import model.ScoringType;
import model.Stone;
import controller.GameController;

import javax.swing.*;
import java.awt.*;

import javax.swing.JFrame;



public class GameFrame {

    private JPanel mainPanel;
    private JLabel blackStats;
    private JLabel whiteStats;
    private BoardPanel boardPanel;
    private GameController controller;
    private JList<String> moveList;

    public GameFrame(JFrame parentFrame, ScoringType scoringType, int boardSize, double komi) {
        Board board = new Board(boardSize);
        boardPanel = new BoardPanel(board, this, scoringType, komi);
        controller = boardPanel.getController();

        // 🎮 Butonlar
        JButton passButton = createStyledButton("✋ Pas Geç");
        passButton.addActionListener(e -> boardPanel.passMove());

        JButton undoButton = createStyledButton("↩️ Geri Al");
        undoButton.addActionListener(e -> {
            controller.undoLastMove();
            boardPanel.repaint();
        });

        JButton resetButton = createStyledButton("🔄 Sıfırla");
        resetButton.addActionListener(e -> {
            controller.resetGame();
            boardPanel.repaint();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(247, 241, 225));
        buttonPanel.add(passButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(resetButton);

        // 🏁 Skor alanı
        blackStats = new JLabel();
        whiteStats = new JLabel();
        styleLabel(blackStats, Color.BLACK);
        styleLabel(whiteStats, new Color(60, 60, 60));

        JPanel scorePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        scorePanel.setBackground(new Color(247, 241, 225));
        scorePanel.setBorder(BorderFactory.createTitledBorder("🏁 Skorlar"));
        scorePanel.add(blackStats);
        scorePanel.add(whiteStats);

        // 📜 Hamle listesi
        moveList = new JList<>(controller.getMoveListModel());
        moveList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane moveScroll = new JScrollPane(moveList);
        moveScroll.setBorder(BorderFactory.createTitledBorder("📜 Hamleler"));
        moveScroll.setPreferredSize(new Dimension(200, 300));
        moveScroll.setBackground(new Color(255, 255, 250));

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBackground(new Color(247, 241, 225));
        eastPanel.add(scorePanel, BorderLayout.NORTH);
        eastPanel.add(moveScroll, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(247, 241, 225));
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        contentPanel.add(eastPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(247, 241, 225));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        updateStats();
        parentFrame.pack();

        int minSize = boardSize * 45;
        parentFrame.setMinimumSize(new Dimension(minSize + 250, minSize + 100));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(189, 215, 238));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void styleLabel(JLabel label, Color color) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(color);
    }

    public void updateStats() {
        int blackTotal = controller.getTotalScore(Stone.BLACK);
        int whiteTotal = controller.getTotalScore(Stone.WHITE);
        int blackEsir = controller.getBlackCaptures();
        int whiteEsir = controller.getWhiteCaptures();
        double komi = controller.getKomi();

        blackStats.setText("⚫ Siyah ➤ Puan: " + blackTotal + " (Esir: " + blackEsir + ")");
        whiteStats.setText("⚪ Beyaz ➤ Puan: " + whiteTotal + " (Esir: " + whiteEsir + ") + Komi: " + komi);
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
