/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Board.java
 *
 * Represents the Go game board. Manages the placement, removal, and lookup of stones.
 * Provides utility functions to validate coordinates and reset the board state.
 */

package model;

public class Board {
    private int SIZE;
    private Stone[][] grid;

    /**
     * Constructor that initializes the board with a given size.
     *
     * @param size The size of the board (e.g., 9, 13, 19)
     */
    public Board(int size) {
        this.SIZE = size;
        grid = new Stone[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Stone.EMPTY;
            }
        }
    }

    /**
     * Returns the size of the board.
     *
     * @return the board size
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Retrieves the stone at the specified coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the stone at (x, y), or null if out of bounds
     */
    public Stone getStone(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return grid[x][y];
        }
        return null;
    }

    /**
     * Checks whether a cell is empty.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the cell is within bounds and empty
     */
    public boolean isCellEmpty(int x, int y) {
        return isValidCoordinate(x, y) && grid[x][y] == Stone.EMPTY;
    }

    /**
     * Attempts to place a stone on the board.
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param color the color of the stone to place
     * @return true if placement is successful
     */
    public boolean placeStone(int x, int y, Stone color) {
        if (isValidCoordinate(x, y) && isCellEmpty(x, y)) {
            grid[x][y] = color;
            return true;
        }
        return false;
    }

    /**
     * Removes a stone from the board (sets cell to EMPTY).
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void removeStone(int x, int y) {
        if (isValidCoordinate(x, y)) {
            grid[x][y] = Stone.EMPTY;
        }
    }

    /**
     * Clears the entire board by setting all cells to EMPTY.
     */
    public void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Stone.EMPTY;
            }
        }
    }

    /**
     * Checks whether the given coordinates are within the board boundaries.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if (x, y) is a valid position on the board
     */
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
}
