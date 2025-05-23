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

/*
 * GameController.java
 *
 * This class manages the core logic of the Go game.
 * It processes moves, captures, undo, pass, and scoring logic.
 * It also interacts with the GUI (GameFrame) to reflect current game state.
 */

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
    private DefaultListModel<String> moveListModel = new DefaultListModel<>();
    private int moveCount = 0;
    private double komi;

    /**
     * Initializes the game controller with board and scoring settings.
     *
     * @param board        The board object representing the game grid.
     * @param gameFrame    Reference to the GUI window (can be null for server-side use).
     * @param scoringType  The type of scoring system (Japanese, Chinese, Stone).
     * @param komi         Bonus points for the white player.
     */
    public GameController(Board board, GameFrame gameFrame, ScoringType scoringType, double komi) {
        this.board = board;
        this.rulesChecker = new RulesChecker(board);
        this.gameFrame = gameFrame;
        this.moveHistory = new Stack<>();
        this.scoringType = scoringType;
        this.komi = komi;
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

    /**
     * Handles placing a stone for the current player, updating captures and turn.
     *
     * @param x x-coordinate on the board.
     * @param y y-coordinate on the board.
     * @return true if the move is valid and applied, false otherwise.
     */
    public boolean handleMove(int x, int y) {
        if (!board.isValidCoordinate(x, y) || !board.isCellEmpty(x, y)) return false;
        if (rulesChecker.isSuicideMove(x, y, currentPlayer)) return false;

        board.placeStone(x, y, currentPlayer);
        moveHistory.push(new Move(x, y, currentPlayer));

        moveCount++;
        String moveStr = moveCount + ". " + (currentPlayer == Stone.BLACK ? "⚫" : "⚪") + " (" + x + "," + y + ")";
        moveListModel.addElement(moveStr);

        int removed = rulesChecker.captureStones(x, y, currentPlayer);
        if (currentPlayer == Stone.BLACK) blackCaptures += removed;
        else whiteCaptures += removed;

        lastMoveWasPass = false;
        switchTurn();

        if (gameFrame != null) gameFrame.updateStats();
        return true;
    }

    /**
     * Handles a pass action. If both players pass consecutively, ends the game.
     */
    public void handlePass() {
        if (lastMoveWasPass) {
            String winner = (getTotalScore(Stone.BLACK) > getTotalScore(Stone.WHITE)) ? "Siyah" : "Beyaz";
            if (gameFrame != null) gameFrame.showGameOverScreen(winner);
        } else {
            lastMoveWasPass = true;
            switchTurn();
        }

        if (gameFrame != null) gameFrame.updateStats();
    }

    /**
     * Switches the current player to the other color.
     */
    private void switchTurn() {
        currentPlayer = (currentPlayer == Stone.BLACK) ? Stone.WHITE : Stone.BLACK;
        if (gameFrame != null) gameFrame.updateTurnLabel();
    }

    /**
     * Undoes the last move if it belongs to the correct player.
     *
     * @return the undone move, or null if not allowed.
     */
    public Move undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move last = moveHistory.peek();
            Stone expectedColor = (currentPlayer == Stone.BLACK) ? Stone.WHITE : Stone.BLACK;
            if (last.color != expectedColor) return null;

            moveHistory.pop();
            board.removeStone(last.x, last.y);

            if (!moveListModel.isEmpty()) {
                moveListModel.removeElementAt(moveListModel.size() - 1);
                moveCount--;
            }

            currentPlayer = last.color;

            if (gameFrame != null) {
                gameFrame.updateStats();
                gameFrame.updateTurnLabel();
            }

            return last;
        }
        return null;
    }

    /**
     * Resets the board and internal state to begin a new game.
     */
    public void resetGame() {
        board.clearBoard();
        currentPlayer = Stone.BLACK;
        blackCaptures = 0;
        whiteCaptures = 0;
        lastMoveWasPass = false;
        moveHistory.clear();
        moveListModel.clear();
        moveCount = 0;
        if (gameFrame != null) gameFrame.updateStats();
    }

    /**
     * Calculates territory controlled by the player using DFS.
     *
     * @param player Stone color to evaluate.
     * @return total number of empty spaces surrounded by the player's stones.
     */
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

    /**
     * Helper method to perform DFS to evaluate territory surrounded by a player's stones.
     */
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

    /**
     * Counts the number of stones placed on the board for the given player.
     */
    public int countStones(Stone player) {
        int count = 0;
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.getStone(x, y) == player) count++;
            }
        }
        return count;
    }

    /**
     * Calculates total score of the player based on the selected scoring type.
     *
     * @param player Stone color to calculate score for.
     * @return integer score.
     */
    public int getTotalScore(Stone player) {
        int base = switch (scoringType) {
            case JAPANESE -> calculateTerritory(player) + (player == Stone.BLACK ? blackCaptures : whiteCaptures);
            case CHINESE -> calculateTerritory(player) + countStones(player);
            case STONE -> countStones(player);
        };

        if (player == Stone.WHITE) base += komi;
        return (int) base;
    }

    // --- Accessors for other components ---

    public ScoringType getScoringType() {
        return scoringType;
    }

    public DefaultListModel<String> getMoveListModel() {
        return moveListModel;
    }

    public Board getBoard() {
        return board;
    }

    public double getKomi() {
        return komi;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    /**
     * Triggers a stats update in the GameFrame UI.
     */
    public void updateStats() {
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
    }

    /**
     * Clears the history of moves, used for full resets.
     */
    public void clearMoveHistory() {
        moveHistory.clear();
        moveCount = 0;
    }

    /**
     * Adds a move to the move history and updates move count.
     */
    public void addMoveToHistory(int x, int y, Stone s) {
        Move m = new Move(x, y, s);
        if (!moveHistory.contains(m)) {
            moveHistory.push(m);
            moveCount++;
        }
    }

    /**
     * Manually sets the current player's turn.
     */
    public void setCurrentPlayer(Stone player) {
        this.currentPlayer = player;
    }
}
