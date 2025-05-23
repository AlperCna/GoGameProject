/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.GameController;
import model.Board;
import model.ScoringType;
import model.Stone;
import ClientandServer.GoClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;


/*
 * BoardPanel.java
 *
 * This class handles the visual representation and interaction of the Go board.
 * It draws the grid, stones, hover effects, and handles user clicks.
 * It also connects with the controller and client to send/receive moves.
 */

public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 40;

    private Board board;
    private GameController controller;
    private int hoverX = -1;
    private int hoverY = -1;

    private boolean isOnline;
    private GoClient client;

    /**
     * Constructor that initializes the visual board and sets up mouse listeners.
     *
     * @param board      The logical game board
     * @param gameFrame  The main game window frame
     * @param scoringType Type of scoring system
     * @param komi        Komi value
     * @param isOnline    True if playing online
     * @param client      Reference to GoClient (nullable for offline mode)
     */
    public BoardPanel(Board board, GameFrame gameFrame, ScoringType scoringType, double komi, boolean isOnline, GoClient client) {
        this.board = board;
        this.controller = new GameController(board, gameFrame, scoringType, komi);
        this.isOnline = isOnline;
        this.client = client;

        int size = board.getSize() * CELL_SIZE;
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(239, 201, 146));

        // Mouse hover tracking
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverX = e.getX() / CELL_SIZE;
                hoverY = e.getY() / CELL_SIZE;
                repaint();
            }
        });

        // Mouse click handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                if (isOnline && client != null) {
                    client.sendMove(x, y);
                } else {
                    boolean success = controller.handleMove(x, y);
                    if (success) {
                        repaint();
                        controller.getGameFrame().updateTurnLabel();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        });
    }

    /**
     * Paints the board, grid lines, and stones.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = board.getSize();

        // Draw grid lines
        g2.setColor(Color.BLACK);
        for (int i = 0; i < size; i++) {
            g2.drawLine(CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE,
                        CELL_SIZE / 2 + (size - 1) * CELL_SIZE, CELL_SIZE / 2 + i * CELL_SIZE);
            g2.drawLine(CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2,
                        CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (size - 1) * CELL_SIZE);
        }

        // Draw stones
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Stone s = board.getStone(x, y);
                if (s != Stone.EMPTY) {
                    drawStone(g2, x, y, s);
                }
            }
        }

        // Draw hover indicator
        if (board.isValidCoordinate(hoverX, hoverY) && board.isCellEmpty(hoverX, hoverY)) {
            Stone current = controller.getCurrentPlayer();
            if (current == Stone.BLACK) {
                g.setColor(new Color(0, 0, 0, 60));
            } else {
                g.setColor(new Color(255, 255, 255, 100));
            }
            g.fillOval(hoverX * CELL_SIZE + 5, hoverY * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }
    }

    /**
     * Draws a stone on the board.
     */
    private void drawStone(Graphics2D g, int x, int y, Stone stone) {
        if (stone == Stone.BLACK) {
            g.setColor(Color.BLACK);
        } else if (stone == Stone.WHITE) {
            g.setColor(Color.WHITE);
        }

        g.fillOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        g.setColor(Color.BLACK);
        g.drawOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
    }

    /**
     * Returns the controller associated with this panel.
     */
    public GameController getController() {
        return controller;
    }

    /**
     * Informs the controller of a pass move (used in remote game).
     */
    public void passMove() {
        controller.handlePass();
        repaint();
    }

    /**
     * Applies a remote move received from the server.
     */
    public void applyRemoteMove(int x, int y) {
        controller.handleMove(x, y);
        repaint();
    }

    /**
     * Undoes a move visually (used when undo is accepted).
     */
    public void applyRemoteUndo() {
        controller.undoLastMove();
        repaint();
    }

    /**
     * Resets the entire board visually and logically.
     */
    public void applyRemoteReset() {
        controller.resetGame();
        repaint();
    }

    /**
     * Sends a pass request (local or remote depending on mode).
     */
    public void sendPass() {
        if (isOnline && client != null) {
            client.sendPass();
        } else {
            controller.handlePass();
            repaint();
        }
    }

    /**
     * Sends an undo request (local or remote).
     */
    public void sendUndo() {
        if (isOnline && client != null) {
            client.sendUndo();
        } else {
            controller.undoLastMove();
            repaint();
        }
    }

    /**
     * Sends a reset request (local or remote).
     */
    public void sendReset() {
        if (isOnline && client != null) {
            client.sendReset();
        } else {
            controller.resetGame();
            repaint();
        }
    }

    /**
     * Applies a complete board state from server and updates the current turn.
     */
    public void applyBoardState(String data, String turn) {
        controller.getBoard().clearBoard();
        controller.getMoveListModel().clear();
        controller.clearMoveHistory();

        String[] entries = data.split(";");
        for (String entry : entries) {
            if (entry.isEmpty()) continue;

            String[] parts = entry.split(":");
            String[] coords = parts[0].split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            Stone s = parts[1].equals("B") ? Stone.BLACK : Stone.WHITE;

            controller.getBoard().placeStone(x, y, s);
            controller.addMoveToHistory(x, y, s);

            int index = controller.getMoveListModel().size() + 1;
            String moveStr = index + ". " + (s == Stone.BLACK ? "⚫" : "⚪") + " (" + x + "," + y + ")";
            controller.getMoveListModel().addElement(moveStr);
        }

        controller.setCurrentPlayer(turn.equals("B") ? Stone.BLACK : Stone.WHITE);
        controller.updateStats();
        repaint();
    }

    /**
     * Removes a specific stone from the board (used in undo).
     */
    public void removeStoneFromBoard(int x, int y) {
        controller.getBoard().removeStone(x, y);

        if (!controller.getMoveListModel().isEmpty()) {
            controller.getMoveListModel().remove(controller.getMoveListModel().getSize() - 1);
        }

        controller.updateStats();
        repaint();
    }

    /**
     * Resets the board state completely (used after full reset).
     */
    public void resetBoardCompletely() {
        controller.resetGame();
        repaint();
    }

    /**
     * Returns the current player's color for UI logic.
     */
    public Stone getMyColor() {
        return client != null ? client.getMyColor() : Stone.BLACK;
    }

    /**
     * Triggers a surrender action (used in local and online modes).
     */
    public void sendSurrender() {
        if (isOnline && client != null) {
            client.sendSurrender();
        } else {
            controller.getGameFrame().showGameOverScreen("Rakip");
        }
    }
}
