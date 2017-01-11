package project.stratego.control.client;

import javafx.application.Platform;
import project.stratego.control.managers.ViewComManager;
import project.stratego.ui.utils.Messages;

import java.io.*;
import java.net.Socket;

public class StrategoClient implements Runnable {

    private volatile boolean stopThread = false;

    //private static final String SERVER_ADDRESS = "82.165.162.249";
    private static final String SERVER_ADDRESS = "localhost";

    private static final int SERVER_PORT = 2000;

    private Socket socket;

    private PrintWriter out;
    private BufferedReader in;

    public StrategoClient() {
        setUp();
    }

    private void setUp() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Messages.showNoConnectionMessage();
            System.out.println("Could not connect to the server. Check whether server is running or whether the correct IP address is configured in the client program.");
            stopThread();
        }
    }

    public void run() {
        String incomingCommand = "";
        try {
            while (!stopThread && (!in.ready() || (in.ready() && (incomingCommand = in.readLine()) != null && !incomingCommand.equals("dc")))) {
                if (!incomingCommand.equals("")) {
                    //System.out.println("Client received: " + incomingCommand);
                    interpretCommand(incomingCommand);
                    incomingCommand = "";
                }
                Thread.sleep(1);
            }
            // quit
            if (socket != null) {
                //Platform.runLater(() -> ViewComManager.getInstance().sendResetGame());
                socket.close();
            }
            //System.out.println("client should be closed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void interpretCommand(String command) {
        String[] parts = command.split(" ");
        //System.out.println("Client received command: \"" + command + "\".");

        if (parts[0].equals("go")) {
            Platform.runLater(() -> ViewComManager.getInstance().sendGameOver(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("sa")) {
            // assign the client a side that the player play on
            //System.out.println("Client received side assign command (player index: " + Integer.parseInt(parts[1]) + ").");
            Platform.runLater(() -> ViewComManager.getInstance().sendAssignSide(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("rd")) {
            // own or opponent's deployed pieces reset (clear one board side)
            Platform.runLater(() -> ViewComManager.getInstance().sendResetDeployment(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("rg")) {
            // reset game
            Platform.runLater(() -> ViewComManager.getInstance().sendResetGame());
        } else if (parts[0].equals("ct")) {
            Platform.runLater(() -> ViewComManager.getInstance().sendChangeTurn(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("pp")) {
            Platform.runLater(() -> ViewComManager.getInstance().sendPiecePlaced(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
        } else if (parts[0].equals("pm")) {
            // opponent's or own piece was moved
            Platform.runLater(() -> ViewComManager.getInstance().sendPieceMoved(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
        } else if (parts[0].equals("ph")) {
            // piece should be hidden from this client's view
            Platform.runLater(() -> ViewComManager.getInstance().sendHidePiece(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
        } else if (parts[0].equals("pr")) {
            // piece should be revealed for this client
            Platform.runLater(() -> ViewComManager.getInstance().sendRevealPiece(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
        } else if (parts[0].equals("al")) {
            // attack lost, attacking and defending piece are revealed, then the attacking one is removed
            Platform.runLater(() -> ViewComManager.getInstance().sendAttackLost(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        } else if (parts[0].equals("at")) {
            // attack tied, attacking and defending piece are revealed, then both are removed
            Platform.runLater(() -> ViewComManager.getInstance().sendAttackTied(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        } else if (parts[0].equals("aw")) {
            // attack won, attacking and defending piece are revealed, then the defending one is removed
            Platform.runLater(() -> ViewComManager.getInstance().sendAttackWon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        } else if (parts[0].equals("hd")) {
            //Platform.runLater(() -> ViewComManager.getInstance().sendHighlightDeployment(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("oq")) {
            Platform.runLater(() -> ViewComManager.getInstance().sendOpponentQuit());
        }
    }

    public void sendCommandToServer(String command) {
        if (out != null) {
            out.println(command);
            //System.out.println("Command sent to server: " + command);
        } else {
            System.out.println("Client could not send command to server.");
        }
    }

    public void stopThread() {
        stopThread = true;
    }

    public boolean isRunning() {
        return !stopThread;
    }

}
