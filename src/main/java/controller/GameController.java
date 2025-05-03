/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Board;
import model.Move;
import model.RulesChecker;
import model.ScoringType;
import model.Stone;
import view.GameFrame;

import javax.swing.*;
import java.util.*;
import static model.ScoringType.CHINESE;
import static model.ScoringType.JAPANESE;
import static model.ScoringType.STONE;

public class GameController {

    private Board board;
    private RulesChecker rulesChecker;
    private GameFrame gameFrame;

    private Stone currentPlayer = Stone.BLACK;
    private boolean lastMoveWasPass = false;

    private int blackCaptures = 0;
    private int whiteCaptures = 0;

    private Stack<Move> moveHistory;
    private ScoringType scoringType;

    // 🆕 Hamle listesi
    private DefaultListModel<String> moveListModel = new DefaultListModel<>();
    private int moveCount = 0;

    public GameController(Board board, GameFrame gameFrame, ScoringType scoringType) {
        this.board = board;
        this.rulesChecker = new RulesChecker(board);
        this.gameFrame = gameFrame;
        this.moveHistory = new Stack<>();
        this.scoringType = scoringType;
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
        moveHistory.push(new Move(x, y, currentPlayer));

        // 🆕 Hamle açıklaması ekle
        moveCount++;
        String moveStr = moveCount + ". " + (currentPlayer == Stone.BLACK ? "⚫" : "⚪") + " (" + x + "," + y + ")";
        moveListModel.addElement(moveStr);

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

    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move last = moveHistory.pop();
            board.removeStone(last.x, last.y);
            if (!moveListModel.isEmpty()) {
                moveListModel.removeElementAt(moveListModel.size() - 1);
                moveCount--;
            }
            gameFrame.updateStats();
        }
    }

    public void resetGame() {
        board.clearBoard();
        currentPlayer = Stone.BLACK;
        blackCaptures = 0;
        whiteCaptures = 0;
        lastMoveWasPass = false;
        moveHistory.clear();
        moveListModel.clear();
        moveCount = 0;
        gameFrame.updateStats();
    }

    public int calculateTerritory(Stone player) {
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        int territory = 0;

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (!visited[x][y] && board.getStone(x, y) == Stone.EMPTY) {
                    Set<Stone> surroundingColors = new HashSet<>();
                    List<int[]> area = new ArrayList<>();
                    dfsTerritoryCheck(x, y, visited, area, surroundingColors);

                    if (surroundingColors.size() == 1 && surroundingColors.contains(player)) {
                        territory += area.size();
                    }
                }
            }
        }

        return territory;
    }

    private void dfsTerritoryCheck(int x, int y, boolean[][] visited, List<int[]> area, Set<Stone> borders) {
        if (!board.isValidCoordinate(x, y) || visited[x][y]) return;
        visited[x][y] = true;

        Stone s = board.getStone(x, y);
        if (s == Stone.EMPTY) {
            area.add(new int[]{x, y});
            dfsTerritoryCheck(x + 1, y, visited, area, borders);
            dfsTerritoryCheck(x - 1, y, visited, area, borders);
            dfsTerritoryCheck(x, y + 1, visited, area, borders);
            dfsTerritoryCheck(x, y - 1, visited, area, borders);
        } else {
            borders.add(s);
        }
    }

    public int countStones(Stone player) {
        int count = 0;
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.getStone(x, y) == player) count++;
            }
        }
        return count;
    }

    public int getTotalScore(Stone player) {
        return switch (scoringType) {
            case JAPANESE -> calculateTerritory(player) + (player == Stone.BLACK ? blackCaptures : whiteCaptures);
            case CHINESE -> calculateTerritory(player) + countStones(player);
            case STONE -> countStones(player);
        };
    }

    public ScoringType getScoringType() {
        return scoringType;
    }

    // 🆕 Listeyi GameFrame'e verebilmek için getter
    public DefaultListModel<String> getMoveListModel() {
        return moveListModel;
    }

    public Board getBoard() {
        return board;
    }
}
