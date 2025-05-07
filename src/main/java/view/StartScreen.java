/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.ScoringType;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public StartScreen(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(247, 241, 225)); // Açık bej arka plan

        JLabel title = new JLabel("⚓ Go Oyunu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(30, 30, 30));
        add(Box.createVerticalStrut(40));
        add(title);
        add(Box.createVerticalStrut(30));

        // Skor tipi seçimi
        JLabel scoringLabel = new JLabel("Skor Tipi:");
        scoringLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoringLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(scoringLabel);

        String[] scoringOptions = {"Japon", "Çin", "Taş"};
        JComboBox<String> scoringBox = new JComboBox<>(scoringOptions);
        scoringBox.setMaximumSize(new Dimension(150, 30));
        scoringBox.setBackground(Color.WHITE);
        scoringBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoringBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(scoringBox);
        add(Box.createVerticalStrut(15));

        // Komi seçimi
        JLabel komiLabel = new JLabel("Komi:");
        komiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        komiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(komiLabel);

        String[] komiOptions = {"Komi yok", "0.5", "5.5", "6.5", "7.5", "9.5"};
        JComboBox<String> komiBox = new JComboBox<>(komiOptions);
        komiBox.setSelectedItem("6.5");
        komiBox.setMaximumSize(new Dimension(150, 30));
        komiBox.setBackground(Color.WHITE);
        komiBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        komiBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(komiBox);
        add(Box.createVerticalStrut(15));

        // Tahta boyutu seçimi
        JLabel boardLabel = new JLabel("Tahta Boyutu:");
        boardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(boardLabel);

        String[] boardSizes = {"9x9", "13x13", "19x19"};
        JComboBox<String> boardSizeBox = new JComboBox<>(boardSizes);
        boardSizeBox.setSelectedItem("13x13");
        boardSizeBox.setMaximumSize(new Dimension(150, 30));
        boardSizeBox.setBackground(Color.WHITE);
        boardSizeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boardSizeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(boardSizeBox);
        add(Box.createVerticalStrut(25));

        // Başlat butonu
        JButton startButton = new JButton("🎮 Oyuna Başla");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startButton.setBackground(new Color(189, 215, 238));
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(startButton);

        // Buton aksiyonu
        startButton.addActionListener(e -> {
            String selectedScoring = (String) scoringBox.getSelectedItem();
            ScoringType type = switch (selectedScoring) {
                case "Çin" -> ScoringType.CHINESE;
                case "Taş" -> ScoringType.STONE;
                default -> ScoringType.JAPANESE;
            };

            String komiSelected = (String) komiBox.getSelectedItem();
            double komi = 0.0;
            if (!komiSelected.equals("Komi yok")) {
                komi = Double.parseDouble(komiSelected);
            }

            String boardSelected = (String) boardSizeBox.getSelectedItem();
            int boardSize = switch (boardSelected) {
                case "9x9" -> 9;
                case "19x19" -> 19;
                default -> 13;
            };

            GameFrame gameFrame = new GameFrame(frame, type, boardSize, komi);
            frame.setContentPane(gameFrame.getMainPanel()); // ✅ JPanel alındı
            frame.revalidate();
        });
    }
}