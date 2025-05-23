/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Alper
 */
import view.StartScreen;

import javax.swing.*;

public class Main {

    /**
     * The main method that starts the GUI application.
     * It creates a JFrame and sets StartScreen as the initial content.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Ensures GUI is created on the Event Dispatch Thread (best practice in Swing)
        SwingUtilities.invokeLater(() -> {
            // Create the main game window
            JFrame frame = new JFrame("Go Oyunu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the initial screen as StartScreen
            frame.setContentPane(new StartScreen(frame));

            // Set size and center the window
            frame.setSize(600, 600);
            frame.setLocationRelativeTo(null);

            // Make the window visible
            frame.setVisible(true);
        });
    }
}