package project.stratego.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class StrategoServer {

    private static final int PORT = 2000;

    private static int IDCounter;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private ArrayList<StrategoServerThread> clients;

    public StrategoServer() {
        setUp();
    }

    private void setUp() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients = new ArrayList<>();
    }

    public void launch() {
        StrategoServerThread temp;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                if (clients.size() % 2 == 0) {
                    // all games are full, a new game has to be started to connect the client to it
                    ((ModelComManager) ManagerManager.getModelReceiver()).addStrategoGame(IDCounter);
                    temp = new StrategoServerThread(this, clientSocket, IDCounter, 0);
                    sendCommandToClient(IDCounter, 0, "sa 0");
                } else {
                    // client is connected to the last game that was created
                    temp = new StrategoServerThread(this, clientSocket, IDCounter, 1);
                    sendCommandToClient(IDCounter, 0, "sa 1");
                    IDCounter++;
                }
                clients.add(temp);
                temp.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommandToClient(int gameID, int playerIndex, String command) {
        for (StrategoServerThread s : clients) {
            if (s.getGameID() == gameID && s.getGameID() == playerIndex) {
                s.sendCommand(command);
                return;
            }
        }
    }

    public void remove(int gameID) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getGameID() == gameID) {
                clients.get(i).sendCommand("dc");
                clients.remove(i);
            }
        }
        ((ModelComManager) ManagerManager.getModelReceiver()).removeStrategoGame(gameID);
    }

}
