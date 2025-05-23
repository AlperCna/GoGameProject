package ClientandServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BoardPanel boardPanel;
    private Stone myColor;
    private JFrame stage;

    public GoClient(String ip, int port, String playerName, JFrame stage) throws IOException {
        this.stage = stage;
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        sendName(playerName);
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
        out.println("UNDO_REQUEST");
    }

    public void sendReset() {
        out.println("RESET_REQUEST");
    }

    public void sendSurrender() {
        out.println("SURRENDER");
        showWinMessage((myColor == Stone.BLACK) ? Stone.WHITE : Stone.BLACK);
    }

    public void sendName(String name) {
        out.println("NAME " + name);
    }

    public void sendSetup(int boardSize, ScoringType scoringType, double komi) {
        out.println("SETUP " + boardSize + " " + scoringType + " " + komi);
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("BLACK")) {
                    myColor = Stone.BLACK;
                } else if (line.equals("WHITE")) {
                    myColor = Stone.WHITE;
                } else if (line.startsWith("MOVE ")) {
                    String[] parts = line.split(" ");
                    boardPanel.applyRemoteMove(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                } else if (line.equals("PASS")) {
                    boardPanel.passMove();
                } else if (line.startsWith("UNDO ")) {
                    String[] parts = line.split(" ");
                    boardPanel.removeStoneFromBoard(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                } else if (line.equals("RESET")) {
                    boardPanel.resetBoardCompletely();
                } else if (line.equals("UNDO_REQUEST")) {
                    int result = JOptionPane.showConfirmDialog(null, "Rakibiniz geri alma istiyor. Kabul ediyor musunuz?", "Geri Al", JOptionPane.YES_NO_OPTION);
                    out.println(result == JOptionPane.YES_OPTION ? "ACCEPT_UNDO" : "REJECT_UNDO");
                } else if (line.equals("RESET_REQUEST")) {
                    int result = JOptionPane.showConfirmDialog(null, "Rakibiniz oyunu sıfırlamak istiyor. Kabul ediyor musunuz?", "Sıfırla", JOptionPane.YES_NO_OPTION);
                    out.println(result == JOptionPane.YES_OPTION ? "ACCEPT_RESET" : "REJECT_RESET");
                } else if (line.equals("SURRENDER")) {
                    JOptionPane.showMessageDialog(null, "Rakibiniz oyunu terk etti. Kazandınız!");
                    showWinMessage(myColor);
                } else if (line.equals("DISCONNECT")) {
                    JOptionPane.showMessageDialog(null, "Rakibiniz bağlantıyı kesti. Kazandınız!");
                    showWinMessage(myColor);
                } else if (line.startsWith("MESAJ ")) {
                    JOptionPane.showMessageDialog(null, line.substring(6));
                } else if (line.startsWith("SETUP ")) {
                    String[] parts = line.split(" ");
                    int boardSize = Integer.parseInt(parts[1]);
                    ScoringType type = ScoringType.valueOf(parts[2]);
                    double komi = Double.parseDouble(parts[3]);

                    SwingUtilities.invokeLater(() -> {
                        view.GameFrame gameFrame = new view.GameFrame(stage, type, boardSize, komi, true, this);
                        setBoardPanel(gameFrame.getBoardPanel());
                        stage.setContentPane(gameFrame.getMainPanel());
                        stage.revalidate();
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Sunucu ile bağlantı kesildi.");
            System.exit(0);
        }
    }

    private void showWinMessage(Stone winner) {
        String text = (winner == Stone.BLACK) ? "Siyah" : "Beyaz";
        boardPanel.getController().getGameFrame().showGameOverScreen(text);
    }
}
