package project.stratego.control;

import java.io.*;
import java.net.Socket;

/**
 * This class represents the client (connection) on the server side.
 */
public class StrategoServerThread implements Runnable {

    private volatile boolean stopThread = false;

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
        setUp();
        System.out.println("Client connected (ID: " + gameID + ", player index: " + playerIndex + ").");
    }

    private void setUp() {
        try {
            //out = new PrintWriter(clientSocket.getOutputStream(), true);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //System.out.println("Client (ID: " + gameID + ", player index: " + playerIndex + ") has \"out\": " + (out != null));
            //System.out.println("Client (ID: " + gameID + ", player index: " + playerIndex + ") has \"in\": " + (in != null));
        } catch (IOException e) {
            e.printStackTrace();
            server.remove(gameID);
            return;
        }
    }

    @Override
    public void run() {
        String incomingCommand = "";
        try {
            while (!stopThread && (!in.ready() || (in.ready() && (incomingCommand = in.readLine()) != null && !incomingCommand.equals("q")))) {
                if (/*in.ready() && */!incomingCommand.equals("")) {
                    interpretCommand(incomingCommand);
                    incomingCommand = "";
                    //System.out.println("Command coming in: " + incomingCommand);
                }
                Thread.sleep(1);
                //System.out.println("line: " + incomingCommand + " (pi: " + playerIndex + ")");
                /*if (in.ready()) {
                    incomingCommand = in.readLine();
                    if (incomingCommand == null || incomingCommand.equals("q")) {
                        clientSocket.close();
                        server.remove(gameID);
                        System.out.println("server thread should be closed");
                        return;
                    }
                }*/
            }
            clientSocket.close();
            server.remove(gameID);
            System.out.println("Server thread (ID: " + gameID + ", player index: " + playerIndex + ") is removed.");
        } catch (IOException e) {
            server.remove(gameID);
            e.printStackTrace();
        } catch (InterruptedException e) {
            server.remove(gameID);
            e.printStackTrace();
        }

        /*while (!stopThread) {
            try {
                incomingCommand = in.readLine();
                if (stopThread || incomingCommand == null || incomingCommand == "QUIT") {
                    // client disconnected
                    System.out.println("Incoming command is null for client (ID: " + gameID + ", player index: " + playerIndex + ").");
                    clientSocket.close();
                    server.remove(gameID);
                    return;
                } else {
                    //System.out.println("Incoming command is \"" + incomingCommand + "\" for client (ID: " + gameID + ", player index: " + playerIndex + ").");
                    interpretCommand(incomingCommand);
                }
            } catch (IOException e) {
                e.printStackTrace();
                server.remove(gameID);
                return;
            }
        }*/
    }

    private void interpretCommand(String command) {
        System.out.println("Command received from client (on server ID: " + gameID + ", player index: " + playerIndex + "): " + command + ".");
        if (command.equals("rg")) {
            // reset game
            ModelComManager.getInstance().requestResetGame(gameID);
        } else if (command.equals("rd")) {
            ModelComManager.getInstance().requestResetDeployment(gameID, playerIndex);
        } else if (command.equals("pr")) {
            // player ready
            ModelComManager.getInstance().requestPlayerReady(gameID, playerIndex);
        } else if (command.equals("ad")) {
            // auto deploy
            ModelComManager.getInstance().requestAutoDeploy(gameID, playerIndex);
        } else if (command.startsWith("tps")) {
            // tray piece selected
            String[] parts = command.split(" ");
            int pieceIndex = Integer.parseInt(parts[1]);
            ModelComManager.getInstance().requestTrayPieceSelected(gameID, playerIndex, pieceIndex);
        } else if (command.startsWith("bts")) {
            // board tile selected
            String[] parts = command.split(" ");
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            ModelComManager.getInstance().requestBoardTileSelected(gameID, playerIndex, row, col);
        }
    }

    public void sendCommand(String command) {
        if (out != null) {
            out.println(command);
            System.out.println("Server sent command: \"" + command + "\".");
        } else {
            System.out.println("Server could not send command to client.");
        }
    }

    public int getGameID() {
        return gameID;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void stopThread() {
        stopThread = true;
    }

}

