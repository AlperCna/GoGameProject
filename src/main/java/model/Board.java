/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Board {
    private final int SIZE = 13;
    private Stone[][] grid;

    public Board() {
        grid = new Stone[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Stone.EMPTY;
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public Stone getStone(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return grid[x][y];
        }
        return null;
    }

    public boolean isCellEmpty(int x, int y) {
        return isValidCoordinate(x, y) && grid[x][y] == Stone.EMPTY;
    }

    public boolean placeStone(int x, int y, Stone color) {
        if (isValidCoordinate(x, y) && isCellEmpty(x, y)) {
            grid[x][y] = color;
            return true;
        }
        return false;
    }

    public void removeStone(int x, int y) {
        if (isValidCoordinate(x, y)) {
            grid[x][y] = Stone.EMPTY;
        }
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
    

public void clearBoard() {
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            grid[i][j] = Stone.EMPTY;
        }
    }
}


}
