package project.stratego.control;

import javafx.application.Platform;

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
            e.printStackTrace();
        }
    }

    public void run() {
        String incomingCommand = "";
        try {
            while (!stopThread && (!in.ready() || (in.ready() && (incomingCommand = in.readLine()) != null && !incomingCommand.equals("dc")))) {
                if (/*in.ready() && */!incomingCommand.equals("")) {
                    System.out.println("Client received: " + incomingCommand);
                    interpretCommand(incomingCommand);
                    incomingCommand = "";
                }
                Thread.sleep(1);
            }
            // quit
            sendCommandToServer("q");
            socket.close();
            System.out.println("client should be closed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*while (!stopThread) {
            try {
                System.out.println("check in client");
                incomingCommand = in.readLine();
                System.out.println("Incoming command: " + incomingCommand);
                if (stopThread || incomingCommand == null || incomingCommand.equals("dc")) {
                    socket.close();
                    System.out.println("client should be closed");
                    return;
                } else {
                    interpretCommand(incomingCommand);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("client should be closed");
    }

    private void interpretCommand(String command) {
        String[] parts = command.split(" ");
        System.out.println("Client received command: \"" + command + "\".");
        /*System.out.println("Broken up:");
        for (String s : parts) {
            System.out.println(s);
        }
        System.out.println();*/

        if (parts[0].equals("go")) {
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendGameOver());
        } else if (parts[0].equals("sa")) {
            // assign the client a side that the player play on
            System.out.println("Client received side assign command (player index: " + Integer.parseInt(parts[1]) + ").");
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendAssignSide(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("rd")) {
            // own or opponent's deployed pieces reset (clear one board side)
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendResetDeployment(Integer.parseInt(parts[1])));
        } else if (parts[0].equals("pp")) {
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendPiecePlaced(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
        } else if (parts[0].equals("pm")) {
            // opponent's or own piece was moved
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendPieceMoved(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
        } else if (parts[0].equals("ph")) {
            // piece should be hidden from this client's view
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendHidePiece(Integer.parseInt(parts[1]), Integer.parseInt(parts[1])));
        } else if (parts[0].equals("pr")) {
            // piece should be revealed for this client
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendRevealPiece(Integer.parseInt(parts[1]), Integer.parseInt(parts[1])));
        } else if (parts[0].equals("al")) {
            // attack lost, attacking and defending piece are revealed, then the attacking one is removed
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackLost(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        } else if (parts[0].equals("at")) {
            // attack tied, attacking and defending piece are revealed, then both are removed
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackTied(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        } else if (parts[0].equals("aw")) {
            // attack won, attacking and defending piece are revealed, then the defending one is removed
            Platform.runLater(() -> ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackWon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6])));
        }

        /*Platform.runLater(() -> {
            if (parts[0].equals("sa")) {
                // assign the client a side that the player play on
                System.out.println("Client received side assign command (player index: " + Integer.parseInt(parts[1]) + ").");
                ((ViewComManager) ManagerManager.getViewReceiver()).sendAssignSide(-1, Integer.parseInt(parts[1]));
            } else if (parts[0].equals("rd")) {
                // own or opponent's deployed pieces reset (clear one board side)
                ((ViewComManager) ManagerManager.getViewReceiver()).sendResetDeployment(-1, Integer.parseInt(parts[1]));
            } else if (parts[0].equals("pp")) {
                ((ViewComManager) ManagerManager.getViewReceiver()).sendPiecePlaced(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
            } else if (parts[0].equals("pm")) {
                // opponent's or own piece was moved
                ((ViewComManager) ManagerManager.getViewReceiver()).sendPieceMoved(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
            } else if (parts[0].equals("ph")) {
                // piece should be hidden from this client's view
                ((ViewComManager) ManagerManager.getViewReceiver()).sendHidePiece(-1, -1, Integer.parseInt(parts[1]), Integer.parseInt(parts[1]));
            } else if (parts[0].equals("pr")) {
                // piece should be revealed for this client
                ((ViewComManager) ManagerManager.getViewReceiver()).sendRevealPiece(-1, -1, Integer.parseInt(parts[1]), Integer.parseInt(parts[1]));
            } else if (parts[0].equals("al")) {
                // attack lost, attacking and defending piece are revealed, then the attacking one is removed
                ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackLost(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
            } else if (parts[0].equals("at")) {
                // attack tied, attacking and defending piece are revealed, then both are removed
                ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackTied(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
            } else if (parts[0].equals("aw")) {
                // attack won, attacking and defending piece are revealed, then the defending one is removed
                ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackWon(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
            }
        });*/

    }

    public void sendCommandToServer(String command) {
        if (out != null) {
            out.println(command);
            System.out.println("Command sent to server: " + command);
        } else {
            System.out.println("Client could not send command to server.");
        }
    }

    public void stopThread() {
        System.out.println("window close requested in client");
        stopThread = true;
    }

    public boolean isRunning() {
        return !stopThread;
    }

}
