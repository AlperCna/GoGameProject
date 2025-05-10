package ClientandServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Alper
 */
import controller.GameController;
import model.Board;
import model.Move;
import model.ScoringType;
import model.Stone;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GoServer {

    private static final int PORT = 12345;
    private static GameController gameController;
    private static PrintWriter out1;
    private static PrintWriter out2;
    private static boolean isPlayer1Turn = true;

    public static void main(String[] args) {
        System.out.println("🟩 Go sunucusu başlatıldı. Oyuncular bekleniyor...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket player1 = serverSocket.accept();
            System.out.println("🔗 Oyuncu 1 bağlandı.");
            out1 = new PrintWriter(player1.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            out1.println("BLACK");

            Socket player2 = serverSocket.accept();
            System.out.println("🔗 Oyuncu 2 bağlandı.");
            out2 = new PrintWriter(player2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            out2.println("WHITE");

            Board board = new Board(13);
            gameController = new GameController(board, null, ScoringType.JAPANESE, 6.5);

            ExecutorService pool = Executors.newFixedThreadPool(2);
            pool.execute(new ServerSidePlayerHandler(in1, out1, Stone.BLACK));
            pool.execute(new ServerSidePlayerHandler(in2, out2, Stone.WHITE));
        } catch (IOException e) {
            System.out.println("❌ Sunucu hatası: " + e.getMessage());
        }
    }

    static class ServerSidePlayerHandler implements Runnable {

        private BufferedReader in;
        private PrintWriter out;
        private Stone playerColor;

        public ServerSidePlayerHandler(BufferedReader in, PrintWriter out, Stone color) {
            this.in = in;
            this.out = out;
            this.playerColor = color;
        }

        @Override
        public void run() {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    System.out.println("👉 Sunucuda komut alındı: " + line);

                    if (line.startsWith("MOVE")) {
                        String[] parts = line.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);

                        boolean isCorrectTurn = (playerColor == Stone.BLACK && isPlayer1Turn)
                                || (playerColor == Stone.WHITE && !isPlayer1Turn);

                        if (!isCorrectTurn) {
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
                    } else if (line.equals("UNDO")) {
                        Move undone = gameController.undoLastMove();
                        if (undone != null) {
                            out1.println("UNDO " + undone.x + " " + undone.y);
                            out2.println("UNDO " + undone.x + " " + undone.y);
                            isPlayer1Turn = !isPlayer1Turn;
                        }
                    } else if (line.equals("RESET")) {
                        gameController.resetGame();
                        out1.println("RESET");
                        out2.println("RESET");
                        isPlayer1Turn = true;
                    }

                }
            } catch (IOException e) {
                System.out.println("❌ " + playerColor + " bağlantı hatası.");
            }
        }
    }
}
