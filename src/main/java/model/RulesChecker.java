/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * RulesChecker.java
 *
 * This class handles rule validation for the Go game.
 * Responsibilities include:
 * - Detecting suicide moves
 * - Checking liberties of stones
 * - Capturing opponent stone groups
 */

package model;

public class RulesChecker {
    private Board board;

    /**
     * Constructor to initialize the RulesChecker with a reference to the board.
     *
     * @param board The game board used for rule checking
     */
    public RulesChecker(Board board) {
        this.board = board;
    }

    /**
     * Recursively checks if a group of stones has at least one liberty.
     *
     * @param x        x-coordinate of the stone
     * @param y        y-coordinate of the stone
     * @param color    Color of the stones in the group
     * @param visited  2D boolean array to track visited positions
     * @return true if at least one liberty exists, false otherwise
     */
    public boolean hasLiberty(int x, int y, Stone color, boolean[][] visited) {
        if (!board.isValidCoordinate(x, y)) return false;
        if (visited[x][y]) return false;
        visited[x][y] = true;

        Stone current = board.getStone(x, y);
        if (current == Stone.EMPTY) return true;
        if (current != color) return false;

        // Recursively check all neighbors
        return hasLiberty(x + 1, y, color, visited) ||
               hasLiberty(x - 1, y, color, visited) ||
               hasLiberty(x, y + 1, color, visited) ||
               hasLiberty(x, y - 1, color, visited);
    }

    /**
     * Checks surrounding opponent groups after a move and captures them if they have no liberties.
     *
     * @param x            x-coordinate of the placed stone
     * @param y            y-coordinate of the placed stone
     * @param currentColor The color of the stone just placed
     * @return total number of opponent stones captured
     */
    public int captureStones(int x, int y, Stone currentColor) {
        int totalRemoved = 0;

        // Check each of the four directions around the placed stone
        for (int[] dir : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (board.isValidCoordinate(nx, ny)) {
                Stone neighbor = board.getStone(nx, ny);
                if (neighbor != Stone.EMPTY && neighbor != currentColor) {
                    boolean[][] visited = new boolean[board.getSize()][board.getSize()];
                    if (!hasLiberty(nx, ny, neighbor, visited)) {
                        totalRemoved += removeGroup(nx, ny, neighbor, new boolean[board.getSize()][board.getSize()]);
                    }
                }
            }
        }

        return totalRemoved;
    }

    /**
     * Checks if placing a stone at (x, y) would result in a suicide (i.e., no liberties).
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param color The color attempting to play
     * @return true if the move is a suicide, false otherwise
     */
    public boolean isSuicideMove(int x, int y, Stone color) {
        board.placeStone(x, y, color); // Temporarily place stone
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        boolean alive = hasLiberty(x, y, color, visited);
        board.removeStone(x, y); // Undo placement
        return !alive;
    }

    /**
     * Recursively removes a group of connected stones of the same color.
     *
     * @param x        x-coordinate
     * @param y        y-coordinate
     * @param color    Color of stones to remove
     * @param visited  2D array to track visited positions
     * @return number of stones removed
     */
    public int removeGroup(int x, int y, Stone color, boolean[][] visited) {
        if (!board.isValidCoordinate(x, y)) return 0;
        if (visited[x][y]) return 0;
        if (board.getStone(x, y) != color) return 0;

        board.removeStone(x, y);
        visited[x][y] = true;

        return 1
            + removeGroup(x + 1, y, color, visited)
            + removeGroup(x - 1, y, color, visited)
            + removeGroup(x, y + 1, color, visited)
            + removeGroup(x, y - 1, color, visited);
    }
}