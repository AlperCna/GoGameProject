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

public class GameFrame {

    private JPanel mainPanel;
    private JLabel blackStats;
    private JLabel whiteStats;
    private BoardPanel boardPanel;
    private GameController controller;
    private JList<String> moveList;

    public GameFrame(JFrame parentFrame, ScoringType scoringType, int boardSize) {
        // 🆕 Komi değeri kullanıcıdan alınır
        String komiStr = JOptionPane.showInputDialog(
            null,
            "Beyaz oyuncu için komi (örn: 6.5):",
            "Komi Belirle",
            JOptionPane.QUESTION_MESSAGE
        );
        double komi = 6.5;
        try {
            if (komiStr != null && !komiStr.isBlank()) {
                komi = Double.parseDouble(komiStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Geçersiz komi! Varsayılan 6.5 kullanılacak.");
        }

        // 🎮 Tahta oluşturuluyor
        Board board = new Board(boardSize);
        boardPanel = new BoardPanel(board, this, scoringType, komi); // ✅ KOMİ parametresi gönderiliyor
        controller = boardPanel.getController(); // ✅ controller boardPanel içinde oluşturuluyor

        // 🎮 Butonlar
        JButton passButton = new JButton("✋ Pas Geç");
        passButton.setFont(new Font("Arial", Font.PLAIN, 14));
        passButton.addActionListener(e -> boardPanel.passMove());

        JButton undoButton = new JButton("↩️ Geri Al");
        undoButton.setFont(new Font("Arial", Font.PLAIN, 14));
        undoButton.addActionListener(e -> {
            controller.undoLastMove();
            boardPanel.repaint();
        });

        JButton resetButton = new JButton("🔄 Sıfırla");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.addActionListener(e -> {
            controller.resetGame();
            boardPanel.repaint();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(passButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(resetButton);

        // 🏁 Skor alanı
        blackStats = new JLabel();
        whiteStats = new JLabel();
        styleLabel(blackStats, Color.BLACK);
        styleLabel(whiteStats, Color.DARK_GRAY);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(3, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder("🏁 Skorlar"));
        scorePanel.add(blackStats);
        scorePanel.add(whiteStats);

        // 📜 Hamle listesi
        moveList = new JList<>(controller.getMoveListModel());
        moveList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane moveScroll = new JScrollPane(moveList);
        moveScroll.setBorder(BorderFactory.createTitledBorder("📜 Hamleler"));
        moveScroll.setPreferredSize(new Dimension(200, 300));

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(scorePanel, BorderLayout.NORTH);
        eastPanel.add(moveScroll, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        contentPanel.add(eastPanel, BorderLayout.EAST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        updateStats();
        parentFrame.pack();

        int minSize = boardSize * 45;
        parentFrame.setMinimumSize(new Dimension(minSize + 250, minSize + 100));
    }

    public GameFrame(JFrame parentFrame, ScoringType scoringType) {
        this(parentFrame, scoringType, 13); // default 13x13
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


