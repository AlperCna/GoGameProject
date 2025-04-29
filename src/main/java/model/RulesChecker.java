/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class RulesChecker {
    private Board board;

    public RulesChecker(Board board) {
        this.board = board;
    }

    public boolean hasLiberty(int x, int y, Stone color, boolean[][] visited) {
        if (!board.isValidCoordinate(x, y)) return false;
        if (visited[x][y]) return false;
        visited[x][y] = true;

        Stone current = board.getStone(x, y);
        if (current == Stone.EMPTY) return true;
        if (current != color) return false;

        return hasLiberty(x+1, y, color, visited) ||
               hasLiberty(x-1, y, color, visited) ||
               hasLiberty(x, y+1, color, visited) ||
               hasLiberty(x, y-1, color, visited);
    }

    public int captureStones(int x, int y, Stone currentColor) {
    int totalRemoved = 0;

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

    public boolean isSuicideMove(int x, int y, Stone color) {
        board.placeStone(x, y, color);
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        boolean alive = hasLiberty(x, y, color, visited);
        board.removeStone(x, y);
        return !alive;
    }

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
