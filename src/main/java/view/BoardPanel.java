/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.GameController;
import model.Board;
import model.ScoringType;
import model.Stone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 40;
    private Board board;
    private GameController controller;

    public BoardPanel(Board board, GameFrame gameFrame, ScoringType scoringType) {
        this.board = board;
        this.controller = new GameController(board, gameFrame, scoringType);

        int size = board.getSize() * CELL_SIZE;
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(239, 201, 146));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;

                if (!controller.handleMove(x, y)) {
                    JOptionPane.showMessageDialog(null,
                            "Ko kuralı ihlali veya geçersiz hamle!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                }

                repaint();
            }
        });
    }

    public GameController getController() {
        return controller;
    }

    public void passMove() {
        controller.handlePass();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            g.drawLine(CELL_SIZE / 2, i * CELL_SIZE + CELL_SIZE / 2,
                    (size - 1) * CELL_SIZE + CELL_SIZE / 2, i * CELL_SIZE + CELL_SIZE / 2);
            g.drawLine(i * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2,
                    i * CELL_SIZE + CELL_SIZE / 2, (size - 1) * CELL_SIZE + CELL_SIZE / 2);
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Stone stone = board.getStone(x, y);
                if (stone == Stone.BLACK || stone == Stone.WHITE) {
                    drawStone(g, x, y, stone);
                }
            }
        }
    }

    private void drawStone(Graphics g, int x, int y, Stone stone) {
        int xPixel = x * CELL_SIZE + CELL_SIZE / 2 - 15;
        int yPixel = y * CELL_SIZE + CELL_SIZE / 2 - 15;

        g.setColor(stone == Stone.BLACK ? Color.BLACK : Color.WHITE);
        g.fillOval(xPixel, yPixel, 30, 30);
        g.setColor(Color.BLACK);
        g.drawOval(xPixel, yPixel, 30, 30);
    }
}
