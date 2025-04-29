/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;

public class EndScreen extends JPanel {

    public EndScreen(JFrame frame, String winner) {
        setLayout(new BorderLayout());

        JLabel message = new JLabel("Kazanan: " + winner, SwingConstants.CENTER);
        message.setFont(new Font("Serif", Font.BOLD, 24));
        add(message, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton restart = new JButton("Yeniden Başla");
        JButton exit = new JButton("Çıkış");

        restart.addActionListener(e -> {
            frame.setContentPane(new GameFrame(frame).getMainPanel());
            frame.revalidate();
            frame.repaint();
        });

        exit.addActionListener(e -> System.exit(0));

        buttonPanel.add(restart);
        buttonPanel.add(exit);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

