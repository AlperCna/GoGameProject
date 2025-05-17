/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientandServer;

/**
 *
 * @author Alper
 */

//import controller.GameController;
//import model.Board;
//import model.Move;
//import model.ScoringType;
//import model.Stone;
//
//import java.io.*;
//import java.net.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import ClientandServer.GoServer.PlayerConnection;
//
//public class GameSession implements Runnable {
//
//    private PlayerConnection player1;
//    private PlayerConnection player2;
//
//    private GameController gameController;
//    private PrintWriter out1;
//    private PrintWriter out2;
//    private BufferedReader in1;
//    private BufferedReader in2;
//
//    private boolean isPlayer1Turn = true;
//
//    public GameSession(PlayerConnection p1, PlayerConnection p2) {
//        this.player1 = p1;
//        this.player2 = p2;
//    }
//
//    @Override
//    public void run() {
//        System.out.println("🎮 Yeni Oyun Başlatıldı: " + player1.name + " (BLACK) vs " + player2.name + " (WHITE)");
//
//        try {
//            out1 = new PrintWriter(player1.socket.getOutputStream(), true);
//            in1 = new BufferedReader(new InputStreamReader(player1.socket.getInputStream()));
//            out2 = new PrintWriter(player2.socket.getOutputStream(), true);
//            in2 = new BufferedReader(new InputStreamReader(player2.socket.getInputStream()));
//
//            out1.println("BLACK");
//            out2.println("WHITE");
//
//            Board board = new Board(13);
//            gameController = new GameController(board, null, ScoringType.JAPANESE, 6.5);
//
//            ExecutorService pool = Executors.newFixedThreadPool(2);
//            pool.execute(() -> handlePlayer(in1, out1, Stone.BLACK, player1.name));
//            pool.execute(() -> handlePlayer(in2, out2, Stone.WHITE, player2.name));
//
//        } catch (IOException e) {
//            System.out.println("❌ GameSession başlatılamadı: " + e.getMessage());
//        }
//    }
//
//    private void handlePlayer(BufferedReader in, PrintWriter out, Stone playerColor, String playerName) {
//        try {
//            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println("👉 [" + playerName + "][" + playerColor + "] " + line);
//
//                if (line.startsWith("MOVE")) {
//                    String[] parts = line.split(" ");
//                    int x = Integer.parseInt(parts[1]);
//                    int y = Integer.parseInt(parts[2]);
//
//                    boolean correctTurn = (playerColor == Stone.BLACK && isPlayer1Turn)
//                            || (playerColor == Stone.WHITE && !isPlayer1Turn);
//
//                    if (!correctTurn) {
//                        out.println("MESAJ Sıra sende değil!");
//                        continue;
//                    }
//
//                    boolean success = gameController.handleMove(x, y);
//                    if (success) {
//                        out1.println("MOVE " + x + " " + y);
//                        out2.println("MOVE " + x + " " + y);
//                        isPlayer1Turn = !isPlayer1Turn;
//                    } else {
//                        out.println("MESAJ Geçersiz hamle!");
//                    }
//
//                } else if (line.equals("PASS")) {
//                    gameController.handlePass();
//                    out1.println("PASS");
//                    out2.println("PASS");
//                    isPlayer1Turn = !isPlayer1Turn;
//
//                } else if (line.equals("UNDO")) {
//                    Move undone = gameController.undoLastMove();
//                    if (undone != null) {
//                        out1.println("UNDO " + undone.x + " " + undone.y);
//                        out2.println("UNDO " + undone.x + " " + undone.y);
//                        isPlayer1Turn = !isPlayer1Turn;
//                    }
//
//                } else if (line.equals("RESET")) {
//                    gameController.resetGame();
//                    out1.println("RESET");
//                    out2.println("RESET");
//                    isPlayer1Turn = true;
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("❌ Bağlantı koptu: " + playerName);
//        }
//    }
//}










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

    private PlayerConnection player1;
    private PlayerConnection player2;

    private GameController gameController;
    private PrintWriter out1;
    private PrintWriter out2;
    private BufferedReader in1;
    private BufferedReader in2;

    private boolean isPlayer1Turn = true;

    public GameSession(PlayerConnection p1, PlayerConnection p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    @Override
    public void run() {
        try {
            out1 = new PrintWriter(player1.socket.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(player1.socket.getInputStream()));
            out2 = new PrintWriter(player2.socket.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(player2.socket.getInputStream()));

            out1.println("BLACK");
            out2.println("WHITE");

            Board board = new Board(13);
            gameController = new GameController(board, null, ScoringType.JAPANESE, 6.5);

            ExecutorService pool = Executors.newFixedThreadPool(2);
            pool.execute(() -> handlePlayer(in1, out1, out2, Stone.BLACK));
            pool.execute(() -> handlePlayer(in2, out2, out1, Stone.WHITE));

        } catch (IOException e) {
            System.out.println("❌ GameSession başlatılamadı: " + e.getMessage());
        }
    }

    private void handlePlayer(BufferedReader in, PrintWriter out, PrintWriter opponentOut, Stone playerColor) {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MOVE ")) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    boolean correctTurn = (playerColor == Stone.BLACK && isPlayer1Turn)
                            || (playerColor == Stone.WHITE && !isPlayer1Turn);

                    if (!correctTurn) {
                        out.println("MESAJ Sıra sende değil!");
                        continue;
                    }

                    boolean success = gameController.handleMove(x, y);
                    if (success) {
                        out1.println("MOVE " + x + " " + y);
                        out2.println("MOVE " + x + " " + y);
                        isPlayer1Turn = !isPlayer1Turn;
                    } else {
                        out.println("MESAJ Geçersiz hamle!");
                    }

                } else if (line.equals("PASS")) {
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
                        isPlayer1Turn = !isPlayer1Turn;
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
            System.out.println("❌ Bağlantı koptu: " + playerColor);
        }
    }
}
