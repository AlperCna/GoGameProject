/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Alper
 */



import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public StartScreen(JFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("🏯 Go Oyunu", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JButton startButton = new JButton("Oyuna Başla");
        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startButton.addActionListener(e -> {
            frame.setContentPane(new GameFrame(frame).getMainPanel());
            frame.revalidate();
            frame.repaint();
        });

        JPanel centerPanel = new JPanel();
        centerPanel.add(startButton);
        add(centerPanel, BorderLayout.CENTER);
    }
}

