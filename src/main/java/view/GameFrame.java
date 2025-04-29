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

import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private JPanel mainPanel;
    private JLabel statsLabel;
    private BoardPanel boardPanel;

    public GameFrame(JFrame parentFrame) {
        Board board = new Board();
        boardPanel = new BoardPanel(board, this);

        // Buton Paneli
        JButton passButton = new JButton("Pas Geç");
        passButton.addActionListener(e -> boardPanel.passMove());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(passButton);

        // İstatistik Paneli
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        updateStats();

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsLabel, BorderLayout.NORTH);

        // Ana Panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void updateStats() {
        int blackStones = boardPanel.getController().countStones(model.Stone.BLACK);
        int whiteStones = boardPanel.getController().countStones(model.Stone.WHITE);
        int blackCaptures = boardPanel.getController().getBlackCaptures();
        int whiteCaptures = boardPanel.getController().getWhiteCaptures();

        statsLabel.setText(
                "<html><b>Oyun Durumu</b><br>" +
                        "Siyah Taş: " + blackStones + "<br>" +
                        "Beyaz Taş: " + whiteStones + "<br>" +
                        "Siyah Esir: " + blackCaptures + "<br>" +
                        "Beyaz Esir: " + whiteCaptures + "</html>");
    }

    public void showGameOverScreen(String winner) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        topFrame.setContentPane(new EndScreen(topFrame, winner));
        topFrame.revalidate();
        topFrame.repaint();
    }
}
