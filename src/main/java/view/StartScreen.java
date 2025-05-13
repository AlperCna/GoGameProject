/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.ScoringType;
import ClientandServer.GoClient;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public StartScreen(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(247, 241, 225));

        JLabel title = new JLabel("⚓ Go Oyunu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(30, 30, 30));
        add(Box.createVerticalStrut(40));
        add(title);
        add(Box.createVerticalStrut(30));

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

        JButton offlineButton = new JButton("🎮 Offline Oyna");
        offlineButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        offlineButton.setBackground(new Color(189, 215, 238));
        offlineButton.setFocusPainted(false);
        offlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(offlineButton);

        offlineButton.addActionListener(e -> {
            ScoringType type = getScoringType(scoringBox);
            double komi = getKomi(komiBox);
            int boardSize = getBoardSize(boardSizeBox);
            GameFrame gameFrame = new GameFrame(frame, type, boardSize, komi, false, null);
            frame.setContentPane(gameFrame.getMainPanel());
            frame.revalidate();
        });

        JButton onlineButton = new JButton("🌐 Online Oyna");
        onlineButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        onlineButton.setBackground(new Color(173, 235, 190));
        onlineButton.setFocusPainted(false);
        onlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(onlineButton);

        onlineButton.addActionListener(e -> {
            try {
                String ip = JOptionPane.showInputDialog(this, "Sunucu IP adresini giriniz:", "127.0.0.1");
                if (ip == null || ip.isBlank()) return;

                ScoringType type = getScoringType(scoringBox);
                double komi = getKomi(komiBox);
                int boardSize = getBoardSize(boardSizeBox);

                GoClient client = new GoClient(ip.trim(), 12345);
                GameFrame gameFrame = new GameFrame(frame, type, boardSize, komi, true, client);
                client.setBoardPanel(gameFrame.getBoardPanel());
                frame.setContentPane(gameFrame.getMainPanel());
                frame.revalidate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bağlantı hatası: " + ex.getMessage());
            }
        });
    }

    private ScoringType getScoringType(JComboBox<String> box) {
        return switch ((String) box.getSelectedItem()) {
            case "Çin" -> ScoringType.CHINESE;
            case "Taş" -> ScoringType.STONE;
            default -> ScoringType.JAPANESE;
        };
    }

    private double getKomi(JComboBox<String> box) {
        String selected = (String) box.getSelectedItem();
        return selected.equals("Komi yok") ? 0.0 : Double.parseDouble(selected);
    }

    private int getBoardSize(JComboBox<String> box) {
        return switch ((String) box.getSelectedItem()) {
            case "9x9" -> 9;
            case "19x19" -> 19;
            default -> 13;
        };
    }
}
