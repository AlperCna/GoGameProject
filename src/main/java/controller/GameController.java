/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Board;
import model.RulesChecker;
import model.Stone;
import view.GameFrame;

import java.util.Stack;

public class GameController {

    private Board board;
    private RulesChecker rulesChecker;
    private GameFrame gameFrame;

    private Stone currentPlayer = Stone.BLACK;
    private boolean lastMoveWasPass = false;

    private int blackCaptures = 0;
    private int whiteCaptures = 0;

    private Stack<String> history; // Ko pozisyon geçmişi

    public GameController(Board board, GameFrame gameFrame) {
        this.board = board;
        this.rulesChecker = new RulesChecker(board);
        this.gameFrame = gameFrame;
        this.history = new Stack<>();
    }

    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public boolean handleMove(int x, int y) {
        if (!board.isValidCoordinate(x, y) || !board.isCellEmpty(x, y)) return false;

        String currentState = boardToString();
        if (history.contains(currentState)) {
            return false; // Aynı pozisyon, Ko kuralı ihlali
        }

        if (rulesChecker.isSuicideMove(x, y, currentPlayer)) return false;

        board.placeStone(x, y, currentPlayer);
        int removed = rulesChecker.captureStones(x, y, currentPlayer);
        if (currentPlayer == Stone.BLACK) blackCaptures += removed;
        else whiteCaptures += removed;

        history.push(currentState); // Geçmiş durumu kaydet
        lastMoveWasPass = false;
        switchTurn();
        gameFrame.updateStats();
        return true;
    }

    public void handlePass() {
        if (lastMoveWasPass) {
            String winner = (getTotalScore(Stone.BLACK) > getTotalScore(Stone.WHITE)) ? "Siyah" : "Beyaz";
            gameFrame.showGameOverScreen(winner);
        } else {
            lastMoveWasPass = true;
            switchTurn();
        }
        gameFrame.updateStats();
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == Stone.BLACK) ? Stone.WHITE : Stone.BLACK;
    }

    public int calculateTerritory(Stone player) {
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        int territory = 0;

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.getStone(x, y) == Stone.EMPTY && !visited[x][y]) {
                    if (isTerritory(x, y, player, visited)) {
                        territory++;
                    }
                }
            }
        }

        return territory;
    }

    private boolean isTerritory(int x, int y, Stone player, boolean[][] visited) {
        if (!board.isValidCoordinate(x, y)) return true;
        if (visited[x][y]) return true;
        visited[x][y] = true;

        Stone s = board.getStone(x, y);
        if (s == Stone.EMPTY) {
            return isTerritory(x + 1, y, player, visited) &&
                   isTerritory(x - 1, y, player, visited) &&
                   isTerritory(x, y + 1, player, visited) &&
                   isTerritory(x, y - 1, player, visited);
        } else {
            return s == player;
        }
    }

    public int getTotalScore(Stone player) {
        int captures = (player == Stone.BLACK) ? blackCaptures : whiteCaptures;
        int territory = calculateTerritory(player);
        return captures + territory;
    }

    private String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                sb.append(board.getStone(i, j) == Stone.BLACK ? "B" : 
                          board.getStone(i, j) == Stone.WHITE ? "W" : ".");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
