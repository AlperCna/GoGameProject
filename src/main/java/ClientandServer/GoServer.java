package ClientandServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Alper
 */


import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GoServer {

    private static final int PORT = 12345;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final BlockingQueue<PlayerConnection> waitingPlayers = new LinkedBlockingQueue<>();
    private static final AtomicInteger playerCounter = new AtomicInteger(1); // Her oyuncuya numara vermek için

    public static void main(String[] args) {
        System.out.println("🟩 Go sunucusu başlatıldı. Oyuncular bekleniyor...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket playerSocket = serverSocket.accept();
                int id = playerCounter.getAndIncrement();
                PlayerConnection player = new PlayerConnection(playerSocket, "Player" + id);
                System.out.println("🔗 Yeni oyuncu bağlandı: " + player.name);

                waitingPlayers.put(player);

                if (waitingPlayers.size() >= 2) {
                    PlayerConnection p1 = waitingPlayers.take();
                    PlayerConnection p2 = waitingPlayers.take();

                    pool.execute(new GameSession(p1, p2));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Sunucu hatası: " + e.getMessage());
        }
    }

    public static class PlayerConnection {
        public Socket socket;
        public String name;

        public PlayerConnection(Socket socket, String name) {
            this.socket = socket;
            this.name = name;
        }
    }
}

