/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientandServer;

import controller.GameController;
import model.Board;
import model.Move;
import model.ScoringType;
import model.Stone;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientandServer.GoServer.PlayerConnection;

public class GameSession implements Runnable {

    private PlayerConnection player1;  // First connected player
    private PlayerConnection player2;  // Second connected player

    private GameController gameController;
    private PrintWriter out1, out2;    // Output streams to both players
    private BufferedReader in1, in2;   // Input streams from both players

    private boolean isPlayer1Turn = true; // Tracks which player's turn it is

    private int boardSize;
    private ScoringType scoringType;
    private double komi;

    /**
     * Constructs a new GameSession between two players.
     */
    public GameSession(PlayerConnection p1, PlayerConnection p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    /**
     * Main logic of the session. Sets up game rules and begins the turn-based communication loop.
     */
    @Override
    public void run() {
        try {
            // Initialize communication streams
            out1 = new PrintWriter(player1.socket.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(player1.socket.getInputStream()));
            out2 = new PrintWriter(player2.socket.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(player2.socket.getInputStream()));

            // --- STEP 1: Receive game settings from first player ---
            String setupLine;
            while (true) {
                setupLine = in1.readLine();
                if (setupLine != null && setupLine.startsWith("SETUP")) break;
            }

            System.out.println("📦 Kurallar alındı: " + setupLine);

            // Parse setup info
            String[] parts = setupLine.split(" ");
            boardSize = Integer.parseInt(parts[1]);
            scoringType = ScoringType.valueOf(parts[2]);
            komi = Double.parseDouble(parts[3]);

            // --- STEP 2: Forward setup to second player ---
            out2.println(setupLine);

            // --- STEP 3: Assign colors ---
            out1.println("BLACK");
            out2.println("WHITE");

            System.out.println("🎮 Yeni Oyun Başladı: " + player1.name + " (BLACK) vs " + player2.name + " (WHITE)");

            // Create a new board and controller
            Board board = new Board(boardSize);
            gameController = new GameController(board, null, scoringType, komi);

            // Start listener threads for each player
            ExecutorService pool = Executors.newFixedThreadPool(2);
            pool.execute(() -> handlePlayer(in1, out1, out2, Stone.BLACK, player1.name));
            pool.execute(() -> handlePlayer(in2, out2, out1, Stone.WHITE, player2.name));

        } catch (IOException e) {
            System.out.println("❌ GameSession başlatılamadı: " + e.getMessage());
        }
    }

    /**
     * Handles communication from one player during the game.
     *
     * @param in          BufferedReader from the player
     * @param out         PrintWriter to the player
     * @param opponentOut PrintWriter to the opponent
     * @param playerColor The color of the current player
     * @param playerName  The name of the current player
     */
    private void handlePlayer(BufferedReader in, PrintWriter out, PrintWriter opponentOut, Stone playerColor, String playerName) {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("📩 [" + playerName + "][" + playerColor + "] komut: " + line);

                if (line.startsWith("MOVE ")) {
                    // Parse move coordinates
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    boolean correctTurn = (playerColor == Stone.BLACK && isPlayer1Turn)
                                        || (playerColor == Stone.WHITE && !isPlayer1Turn);

                    if (!correctTurn) {
                        out.println("MESAJ Sıra sende değil!");
                        continue;
                    }

                    // Process move
                    boolean success = gameController.handleMove(x, y);
                    if (success) {
                        out1.println("MOVE " + x + " " + y);
                        out2.println("MOVE " + x + " " + y);
                        isPlayer1Turn = !isPlayer1Turn;
                    } else {
                        out.println("MESAJ Geçersiz hamle!");
                    }

                } else if (line.equals("PASS")) {
                    // Both players notified of pass
                    gameController.handlePass();
                    out1.println("PASS");
                    out2.println("PASS");
                    isPlayer1Turn = !isPlayer1Turn;

                } else if (line.equals("UNDO_REQUEST")) {
                    opponentOut.println("UNDO_REQUEST");

                } else if (line.equals("ACCEPT_UNDO")) {
                    Move undone = gameController.undoLastMove();
                    if (undone != null) {
                        out1.println("UNDO " + undone.x + " " + undone.y);
                        out2.println("UNDO " + undone.x + " " + undone.y);
                        isPlayer1Turn = (undone.color == Stone.BLACK);

                        // Notify new turn
                        String newTurn = (undone.color == Stone.BLACK) ? "B" : "W";
                        out1.println("TURN " + newTurn);
                        out2.println("TURN " + newTurn);
                    } else {
                        out.println("MESAJ Sadece kendi hamleni geri alabilirsin!");
                    }

                } else if (line.equals("REJECT_UNDO")) {
                    out1.println("MESAJ Rakibiniz geri almayı reddetti.");
                    out2.println("MESAJ Geri alma isteğini reddettiniz.");

                } else if (line.equals("RESET_REQUEST")) {
                    opponentOut.println("RESET_REQUEST");

                } else if (line.equals("ACCEPT_RESET")) {
                    gameController.resetGame();
                    out1.println("RESET");
                    out2.println("RESET");
                    isPlayer1Turn = true;

                } else if (line.equals("REJECT_RESET")) {
                    out1.println("MESAJ Rakibiniz sıfırlamayı reddetti.");
                    out2.println("MESAJ Sıfırlama isteğini reddettiniz.");

                } else if (line.equals("SURRENDER")) {
                    out.println("MESAJ Oyunu terk ettiniz.");
                    opponentOut.println("SURRENDER");
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Bağlantı koptu: " + playerName);
            try {
                out.println("DISCONNECT");
                opponentOut.println("DISCONNECT");
            } catch (Exception ignored) {}
        }
    }
}
