/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Board;
import model.RulesChecker;
import model.Stone;
import view.GameFrame;

public class GameController {

    private Board board;
    private RulesChecker rulesChecker;
    private GameFrame gameFrame;

    private Stone currentPlayer = Stone.BLACK;
    private boolean lastMoveWasPass = false;

    private int blackCaptures = 0;
    private int whiteCaptures = 0;

    public GameController(Board board, GameFrame gameFrame) {
        this.board = board;
        this.rulesChecker = new RulesChecker(board);
        this.gameFrame = gameFrame;
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
        if (rulesChecker.isSuicideMove(x, y, currentPlayer)) return false;

        board.placeStone(x, y, currentPlayer);
        int removed = rulesChecker.captureStones(x, y, currentPlayer);
        if (currentPlayer == Stone.BLACK) blackCaptures += removed;
        else whiteCaptures += removed;

        lastMoveWasPass = false;
        switchTurn();
        gameFrame.updateStats();
        return true;
    }

    public void handlePass() {
        if (lastMoveWasPass) {
            String winner = calculateWinner();
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

    private String calculateWinner() {
        int black = 0, white = 0;
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                Stone s = board.getStone(x, y);
                if (s == Stone.BLACK) black++;
                else if (s == Stone.WHITE) white++;
            }
        }
        return (black > white) ? "Siyah" : "Beyaz";
    }

    public int countStones(Stone color) {
        int count = 0;
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.getStone(x, y) == color) count++;
            }
        }
        return count;
    }
}
