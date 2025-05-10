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
import model.Stone;
import view.BoardPanel;
import javax.swing.JOptionPane;

public class GoClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BoardPanel boardPanel;
    private Stone myColor;  // Oyuncunun taş rengi

    public GoClient(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(this::listen).start();
    }

    public void setBoardPanel(BoardPanel panel) {
        this.boardPanel = panel;
    }

    public Stone getMyColor() {
        return myColor;
    }

    public void sendMove(int x, int y) {
        out.println("MOVE " + x + " " + y);
    }

    public void sendPass() {
        out.println("PASS");
    }

    public void sendUndo() {
        out.println("UNDO");
    }

    public void sendReset() {
        out.println("RESET");
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("👉 Sunucudan gelen: " + line);

                if (line.equals("BLACK")) {
                    myColor = Stone.BLACK;
                } else if (line.equals("WHITE")) {
                    myColor = Stone.WHITE;
                } else if (line.startsWith("MOVE")) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    boardPanel.applyRemoteMove(x, y);
                } else if (line.equals("PASS")) {
                    boardPanel.passMove();
                } else if (line.startsWith("UNDO")) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    boardPanel.removeStoneFromBoard(x, y);
                } else if (line.equals("RESET")) {
                    boardPanel.resetBoardCompletely();
                } else if (line.startsWith("MESAJ")) {
                    String msg = line.substring(6);
                    JOptionPane.showMessageDialog(null, msg);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Sunucudan bağlantı kesildi.");
        }
    }
}


