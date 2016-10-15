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
        if (parts[0] == "rd") {
            if (parts[1] == "s") {
                // own deployed pieces reset (clear own board side)
            } else if (parts[1] == "o") {
                // opponent's deployed pieces reset (clear opponent's board side)
            }
        } else if (parts[0] == "pp") {
            if (parts[1] == "s") {
                // own piece was successfully placed
            } else if (parts[1] == "o") {
                // opponent's piece was placed
            }
        } else if (parts[0] == "pm") {
            // opponent's or own piece was moved
        } else if (parts[0] == "ph") {
            // piece should be hidden from this client's view
        } else if (parts[0] == "pr") {
            // piece should be revealed for this client
        } else if (parts[0] == "al") {
            // attack lost, attacking and defending piece are revealed, then the attacking one is removed
        } else if (parts[0] == "at") {
            // attack tied, attacking and defending piece are revealed, then both are removed
        } else if (parts[0] == "aw") {
            // attack won, attacking and defending piece are revealed, then the defending one is removed
        }
    }

}
