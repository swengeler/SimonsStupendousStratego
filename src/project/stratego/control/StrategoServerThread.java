package project.stratego.control;

import java.io.*;
import java.net.Socket;

/**
 * This class represents the client (connection) on the server side.
 */
public class StrategoServerThread implements Runnable {

    private final int gameID;
    private final int playerIndex;

    private StrategoServer server;
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    public StrategoServerThread(StrategoServer server, Socket clientSocket, int gameID, int playerIndex) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.gameID = gameID;
        this.playerIndex = playerIndex;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            server.remove(gameID);
            return;
        }
        String incomingCommand;
        while (true) {
            try {
                incomingCommand = in.readLine();
                if (incomingCommand == null || incomingCommand == "QUIT") {
                    // client disconnected
                    clientSocket.close();
                    server.remove(gameID);
                    return;
                } else {
                    interpretCommand(incomingCommand);
                }
            } catch (IOException e) {
                e.printStackTrace();
                server.remove(gameID);
                return;
            }

        }
    }

    private void interpretCommand(String command) {
        if (command == "rg") {
            // reset game
            ((ModelComManager) ManagerManager.getModelReceiver()).sendResetGame(gameID);
        } else if (command == "pr") {
            // player ready
            ((ModelComManager) ManagerManager.getModelReceiver()).sendPlayerReady(gameID, playerIndex);
        } else if (command == "ad") {
            // auto deploy
            ((ModelComManager) ManagerManager.getModelReceiver()).sendAutoDeploy(gameID, playerIndex);
        } else if (command.startsWith("tps")) {
            // tray piece selected
            String[] parts = command.split(" ");
            int pieceIndex = Integer.parseInt(parts[1]);
            ((ModelComManager) ManagerManager.getModelReceiver()).sendTrayPieceSelected(gameID, playerIndex, pieceIndex);
        } else if (command.startsWith("bts")) {
            // board tile selected
            String[] parts = command.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            ((ModelComManager) ManagerManager.getModelReceiver()).sendBoardTileSelected(gameID, playerIndex, row, col);
        }
    }

    public void sendCommand(String command) {
        if (out != null)
            out.println(command);
        else
            System.out.println("Client could not send command");
    }

    public int getGameID() {
        return gameID;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

}
