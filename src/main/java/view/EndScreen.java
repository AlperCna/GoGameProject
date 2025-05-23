/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.ScoringType;

import javax.swing.*;
import java.awt.*;


/*
 * EndScreen.java
 *
 * This class represents the screen shown at the end of the game.
 * It displays the winner, scoring method used, and provides buttons to start a new game or exit.
 */

public class EndScreen extends JPanel {

    /**
     * Constructs the end screen panel.
     *
     * @param frame        The main JFrame to switch panels
     * @param winner       The winner text (e.g., "Siyah", "Beyaz")
     * @param scoringType  The scoring system used in the game
     */
    public EndScreen(JFrame frame, String winner, ScoringType scoringType) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(247, 241, 225)); // light beige

        // Title label
        JLabel title = new JLabel("Oyun Bitti!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(30, 30, 30));

        // Winner label
        JLabel result = new JLabel("Kazanan: " + winner);
        result.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        result.setAlignmentX(Component.CENTER_ALIGNMENT);
        result.setForeground(new Color(50, 50, 50));

        // Scoring type label
        JLabel scoring = new JLabel("Skor Tipi: " + scoringType.toString());
        scoring.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoring.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoring.setForeground(new Color(80, 80, 80));

        // New Game button
        JButton restartButton = createStyledButton("Yeni Oyun");
        restartButton.addActionListener(e -> {
            // Return to StartScreen
            frame.setContentPane(new StartScreen(frame));
            frame.revalidate();
        });

        // Exit button
        JButton exitButton = createStyledButton("🚪 Çıkış");
        exitButton.addActionListener(e -> System.exit(0));

        // Layout structure
        add(Box.createVerticalStrut(40));
        add(title);
        add(Box.createVerticalStrut(20));
        add(result);
        add(Box.createVerticalStrut(10));
        add(scoring);
        add(Box.createVerticalStrut(30));
        add(restartButton);
        add(Box.createVerticalStrut(10));
        add(exitButton);
    }

    /**
     * Utility method to create consistently styled buttons.
     *
     * @param text Button label
     * @return Styled JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(189, 215, 238));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return button;
    }
}



