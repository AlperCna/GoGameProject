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
import java.awt.event.MouseMotionAdapter;

public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 40;
    private Board board;
    private GameController controller;

    // 🆕 Mouse hover konumu
    private int hoverX = -1;
    private int hoverY = -1;

    public BoardPanel(Board board, GameFrame gameFrame, ScoringType scoringType, double komi) {
        this.board = board;
        this.controller = new GameController(board, gameFrame, scoringType, komi);

        int size = board.getSize() * CELL_SIZE;
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(239, 201, 146));

        // 🖱 Mouse hareketini takip et
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverX = e.getX() / CELL_SIZE;
                hoverY = e.getY() / CELL_SIZE;
                repaint();
            }
        });

        // 🖱 Mouse tıklamasını işle
        addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / CELL_SIZE;
        int y = e.getY() / CELL_SIZE;
        boolean success = controller.handleMove(x, y);
        if (success) {
            repaint();
        } else {
            Toolkit.getDefaultToolkit().beep(); // sadece bip sesi verir, pencere açmaz
        }
    }
});

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = board.getSize();

        // 🟫 Tahta çizgileri
        g.setColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            g.drawLine(CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE,
                    CELL_SIZE / 2 + (size - 1) * CELL_SIZE, CELL_SIZE / 2 + i * CELL_SIZE);
            g.drawLine(CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2,
                    CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (size - 1) * CELL_SIZE);
        }

        // ⚫⚪ Taşları çiz
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Stone s = board.getStone(x, y);
                if (s != Stone.EMPTY) {
                    drawStone(g, x, y, s);
                }
            }
        }

        // 👻 Hover efekt (taş önizlemesi)
        if (board.isValidCoordinate(hoverX, hoverY) && board.isCellEmpty(hoverX, hoverY)) {
            Stone current = controller.getCurrentPlayer();
            if (current == Stone.BLACK) {
                g.setColor(new Color(0, 0, 0, 60)); // siyah için saydam
            } else {
                g.setColor(new Color(255, 255, 255, 100)); // beyaz için saydam
            }
            g.fillOval(hoverX * CELL_SIZE + 5, hoverY * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }
    }

    private void drawStone(Graphics g, int x, int y, Stone stone) {
        if (stone == Stone.BLACK) g.setColor(Color.BLACK);
        else if (stone == Stone.WHITE) g.setColor(Color.WHITE);

        g.fillOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        g.setColor(Color.BLACK);
        g.drawOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
    }

    public GameController getController() {
        return controller;
    }

    public void passMove() {
        controller.handlePass();
        repaint();
    }
}
