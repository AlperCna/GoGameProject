package ClientandServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * GoClient.java
 *
 * This class represents the client-side network handler for the Go game.
 * It manages the connection between the player's GUI application and the remote server.
 * The client is responsible for:
 *  - Connecting to the server using IP and port
 *  - Sending player actions (move, pass, undo, reset, surrender)
 *  - Receiving game updates and messages from the server
 *  - Updating the GUI accordingly through BoardPanel and GameFrame
 */


import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import model.Stone;
import view.BoardPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.ScoringType;
import view.GameFrame;

public class GoClient {
    private Socket socket;                 // Network socket for connecting to the server
    private PrintWriter out;              // Output stream to send messages to the server
    private BufferedReader in;            // Input stream to receive messages from the server
    private BoardPanel boardPanel;        // Reference to the game board UI component
    private Stone myColor;                // The color assigned to this player (BLACK or WHITE)
    private JFrame stage;                 // Main GUI frame for screen transitions

    /**
     * Constructor that connects to the Go game server and starts listening for server messages.
     *
     * @param ip         IP address of the server (e.g., "127.0.0.1" or AWS public IP)
     * @param port       Port number the server is listening on
     * @param playerName Player's chosen display name
     * @param stage      Main application frame for switching panels
     * @throws IOException if connection to the server fails
     */
    public GoClient(String ip, int port, String playerName, JFrame stage) throws IOException {
        this.stage = stage;
        socket = new Socket(ip, port);  // Establish socket connection to server
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        sendName(playerName);           // Send player name to the server
        new Thread(this::listen).start(); // Start listening to server messages in a background thread
    }

    /**
     * Sets the BoardPanel which this client will update.
     */
    public void setBoardPanel(BoardPanel panel) {
        this.boardPanel = panel;
    }

    /**
     * Gets the current player's color (BLACK or WHITE).
     */
    public Stone getMyColor() {
        return myColor;
    }

    // --- Outgoing Communication ---

    /** Sends a move (x, y) to the server */
    public void sendMove(int x, int y) {
        out.println("MOVE " + x + " " + y);
    }

    /** Sends a pass message to the server */
    public void sendPass() {
        out.println("PASS");
    }

    /** Sends an undo request to the server */
    public void sendUndo() {
        out.println("UNDO_REQUEST");
    }

    /** Sends a reset request to the server */
    public void sendReset() {
        out.println("RESET_REQUEST");
    }

    /** Sends a surrender message and triggers local win screen */
    public void sendSurrender() {
        out.println("SURRENDER");
        showWinMessage((myColor == Stone.BLACK) ? Stone.WHITE : Stone.BLACK);
    }

    /** Sends the player’s name to the server */
    public void sendName(String name) {
        out.println("NAME " + name);
    }

    /** Sends game setup info (board size, scoring type, komi) to the server */
    public void sendSetup(int boardSize, ScoringType scoringType, double komi) {
        out.println("SETUP " + boardSize + " " + scoringType + " " + komi);
    }

    /**
     * Listens for server messages and processes them to update the local game state.
     * Runs in a separate thread.
     */
    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {

                // Server assigns color to this client
                if (line.equals("BLACK")) {
                    myColor = Stone.BLACK;
                } else if (line.equals("WHITE")) {
                    myColor = Stone.WHITE;

                // Opponent made a move
                } else if (line.startsWith("MOVE ")) {
                    String[] parts = line.split(" ");
                    boardPanel.applyRemoteMove(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

                // Opponent passed
                } else if (line.equals("PASS")) {
                    boardPanel.passMove();

                // Undo response - remove a specific move
                } else if (line.startsWith("UNDO ")) {
                    String[] parts = line.split(" ");
                    boardPanel.removeStoneFromBoard(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

                // Reset board
                } else if (line.equals("RESET")) {
                    boardPanel.resetBoardCompletely();

                // Incoming undo request - prompt user
                } else if (line.equals("UNDO_REQUEST")) {
                    int result = JOptionPane.showConfirmDialog(null, "Rakibiniz geri alma istiyor. Kabul ediyor musunuz?", "Geri Al", JOptionPane.YES_NO_OPTION);
                    out.println(result == JOptionPane.YES_OPTION ? "ACCEPT_UNDO" : "REJECT_UNDO");

                // Incoming reset request - prompt user
                } else if (line.equals("RESET_REQUEST")) {
                    int result = JOptionPane.showConfirmDialog(null, "Rakibiniz oyunu sıfırlamak istiyor. Kabul ediyor musunuz?", "Sıfırla", JOptionPane.YES_NO_OPTION);
                    out.println(result == JOptionPane.YES_OPTION ? "ACCEPT_RESET" : "REJECT_RESET");

                // Opponent surrendered
                } else if (line.equals("SURRENDER")) {
                    JOptionPane.showMessageDialog(null, "Rakibiniz oyunu terk etti. Kazandınız!");
                    showWinMessage(myColor);

                // Opponent disconnected
                } else if (line.equals("DISCONNECT")) {
                    JOptionPane.showMessageDialog(null, "Rakibiniz bağlantıyı kesti. Kazandınız!");
                    showWinMessage(myColor);

                // Server message popup
                } else if (line.startsWith("MESAJ ")) {
                    JOptionPane.showMessageDialog(null, line.substring(6));

                // Setup parameters received from first player
                } else if (line.startsWith("SETUP ")) {
                    String[] parts = line.split(" ");
                    int boardSize = Integer.parseInt(parts[1]);
                    ScoringType type = ScoringType.valueOf(parts[2]);
                    double komi = Double.parseDouble(parts[3]);

                    // Launch game screen with received settings
                    SwingUtilities.invokeLater(() -> {
                        GameFrame gameFrame = new GameFrame(stage, type, boardSize, komi, true, this);
                        setBoardPanel(gameFrame.getBoardPanel());
                        stage.setContentPane(gameFrame.getMainPanel());
                        stage.revalidate();
                    });

                // Update current turn info
                } else if (line.startsWith("TURN ")) {
                    String turn = line.substring(5);
                    Stone current = turn.equals("B") ? Stone.BLACK : Stone.WHITE;
                    boardPanel.getController().setCurrentPlayer(current);
                    SwingUtilities.invokeLater(() -> boardPanel.getController().getGameFrame().updateTurnLabel());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Sunucu ile bağlantı kesildi.");
            System.exit(0);
        }
    }

    /**
     * Displays the Game Over screen indicating the winner.
     *
     * @param winner The color of the winning player
     */
    private void showWinMessage(Stone winner) {
        String text = (winner == Stone.BLACK) ? "Siyah" : "Beyaz";
        boardPanel.getController().getGameFrame().showGameOverScreen(text);
    }
}
