/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Board;
import model.RulesChecker;
import model.ScoringType;
import model.Stone;
import view.GameFrame;

import java.util.*;

public class GameController {

    private Board board;
    private RulesChecker rulesChecker;
    private GameFrame gameFrame;

    private Stone currentPlayer = Stone.BLACK;
    private boolean lastMoveWasPass = false;

    private int blackCaptures = 0;
    private int whiteCaptures = 0;

    private Stack<String> history;
    private ScoringType scoringType;

    public GameController(Board board, GameFrame gameFrame, ScoringType scoringType) {
        this.board = board;
        this.rulesChecker = new RulesChecker(board);
        this.gameFrame = gameFrame;
        this.history = new Stack<>();
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

        String currentState = boardToString();
        if (history.contains(currentState)) return false;

        if (rulesChecker.isSuicideMove(x, y, currentPlayer)) return false;

        board.placeStone(x, y, currentPlayer);
        int removed = rulesChecker.captureStones(x, y, currentPlayer);
        if (currentPlayer == Stone.BLACK) blackCaptures += removed;
        else whiteCaptures += removed;

        history.push(currentState);
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

    // ✅ DÜZELTİLMİŞ TERRITORY HESAPLAMA (Japon sistemine uygun)
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

    // ✅ Boşluk grubu tarama ve çevresindeki taşları toplama
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