package project.stratego.control;

import java.io.*;
import java.net.Socket;

public class StrategoClient {

    private static final String SERVER_ADDRESS = "82.165.162.249";

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

    public void launch() {
        String incomingCommand;
        while (true) {
            try {
                incomingCommand = in.readLine();
                if (incomingCommand == null || incomingCommand == "dc") {
                    socket.close();
                    return;
                } else {
                    interpretCommand(incomingCommand);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void interpretCommand(String command) {
        String[] parts = command.split(" ");
        if (parts[0] == "sa") {
            // assign the client a side that the player play on
            ((ViewComManager) ManagerManager.getViewReceiver()).sendAssignSide(-1, Integer.parseInt(parts[1]));
        } else if (parts[0] == "rd") {
            // own deployed pieces reset (clear own board side)
            ((ViewComManager) ManagerManager.getViewReceiver()).sendResetDeployment(-1, Integer.parseInt(parts[1]));
        } else if (parts[0] == "pp") {
            ((ViewComManager) ManagerManager.getViewReceiver()).sendPiecePlaced(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
        } else if (parts[0] == "pm") {
            // opponent's or own piece was moved
            ((ViewComManager) ManagerManager.getViewReceiver()).sendPieceMoved(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
        } else if (parts[0] == "ph") {
            // piece should be hidden from this client's view
            ((ViewComManager) ManagerManager.getViewReceiver()).sendHidePiece(-1, -1, Integer.parseInt(parts[1]), Integer.parseInt(parts[1]));
        } else if (parts[0] == "pr") {
            // piece should be revealed for this client
            ((ViewComManager) ManagerManager.getViewReceiver()).sendRevealPiece(-1, -1, Integer.parseInt(parts[1]), Integer.parseInt(parts[1]));
        } else if (parts[0] == "al") {
            // attack lost, attacking and defending piece are revealed, then the attacking one is removed
            ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackLost(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
        } else if (parts[0] == "at") {
            // attack tied, attacking and defending piece are revealed, then both are removed
            ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackTied(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
        } else if (parts[0] == "aw") {
            // attack won, attacking and defending piece are revealed, then the defending one is removed
            ((ViewComManager) ManagerManager.getViewReceiver()).sendAttackWon(-1, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
        }
    }

    public void sendCommandToServer(String command) {
        if (out != null) {
            out.println(command);
        } else {
            System.out.println("Client could not send command to server.");
        }
    }

}
