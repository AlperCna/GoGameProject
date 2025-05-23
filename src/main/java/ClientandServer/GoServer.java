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

    // The port number on which the server listens for incoming connections
    private static final int PORT = 12345;

    // Thread pool to handle multiple games concurrently
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    // Queue to hold players waiting to be paired
    private static final BlockingQueue<PlayerConnection> waitingPlayers = new LinkedBlockingQueue<>();

    // Counter to generate fallback player names
    private static final AtomicInteger playerCounter = new AtomicInteger(1);

    /**
     * Entry point of the Go game server.
     * Listens for incoming player connections and starts a new game session when two players are available.
     */
    public static void main(String[] args) {
        System.out.println("Go sunucusu başlatıldı. Oyuncular bekleniyor...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept a new player connection
                Socket playerSocket = serverSocket.accept();

                // Temporary input stream to receive player name
                BufferedReader tempIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                String nameLine = tempIn.readLine(); // Expected format: "NAME Alper"

                // Set default name if none provided
                String name = "Player" + playerCounter.getAndIncrement();
                if (nameLine != null && nameLine.startsWith("NAME ")) {
                    name = nameLine.substring(5).trim();
                }

                // Create a new player connection object
                PlayerConnection player = new PlayerConnection(playerSocket, name);
                System.out.println("🔗 Yeni oyuncu bağlandı: " + player.name);

                // Add the player to the waiting queue
                waitingPlayers.put(player);

                // When two players are available, start a new game session
                if (waitingPlayers.size() >= 2) {
                    PlayerConnection p1 = waitingPlayers.take();
                    PlayerConnection p2 = waitingPlayers.take();

                    // Run the game session on a separate thread
                    pool.execute(new GameSession(p1, p2));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Sunucu hatası: " + e.getMessage());
        }
    }

    /**
     * A helper class to store player socket and name information.
     */
    public static class PlayerConnection {
        public Socket socket;
        public String name;

        public PlayerConnection(Socket socket, String name) {
            this.socket = socket;
            this.name = name;
        }
    }
}

