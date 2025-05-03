/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Alper
 */


import model.ScoringType;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public StartScreen(JFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("🏯 Go Oyunu");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Skor tipi seçimi
        String[] scoringOptions = {"Japon", "Çin", "Taş"};
        JComboBox<String> scoringCombo = new JComboBox<>(scoringOptions);
        scoringCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        scoringCombo.setPreferredSize(new Dimension(200, 30));

        // 🆕 Tahta boyutu seçimi
        String[] boardSizes = {"9x9", "13x13", "19x19"};
        JComboBox<String> sizeCombo = new JComboBox<>(boardSizes);
        sizeCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        sizeCombo.setPreferredSize(new Dimension(200, 30));

        // Başlat butonu
        JButton startButton = new JButton("Oyuna Başla");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.setPreferredSize(new Dimension(200, 40));

        // Arayüz düzeni
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 20, 10);
        add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 10, 10);
        add(scoringCombo, gbc);

        gbc.gridy = 2;
        add(sizeCombo, gbc); // 🆕

        gbc.gridy = 3;
        add(startButton, gbc);

        // Buton işlevi
        startButton.addActionListener(e -> {
            String selected = (String) scoringCombo.getSelectedItem();
            ScoringType type = switch (selected) {
                case "Japon" -> ScoringType.JAPANESE;
                case "Çin" -> ScoringType.CHINESE;
                case "Taş" -> ScoringType.STONE;
                default -> ScoringType.JAPANESE;
            };

            String sizeStr = (String) sizeCombo.getSelectedItem();
            int size = switch (sizeStr) {
                case "9x9" -> 9;
                case "19x19" -> 19;
                default -> 13;
            };

            frame.setContentPane(new GameFrame(frame, type, size).getMainPanel());
            frame.revalidate();
            frame.repaint();
        });
    }
}
