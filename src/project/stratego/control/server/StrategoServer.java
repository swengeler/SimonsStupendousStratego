package project.stratego.control.server;

import project.stratego.control.managers.ModelComManager;

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
            System.out.println("Server started successfully.");
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
                    ModelComManager.getInstance().addStrategoGame(IDCounter);
                    temp = new StrategoServerThread(this, clientSocket, IDCounter, 0);
                    clients.add(temp);
                    (new Thread(temp)).start();
                    sendCommandToClient(IDCounter, 0, "sa 0");
                } else {
                    // client is connected to the last game that was created
                    temp = new StrategoServerThread(this, clientSocket, IDCounter, 1);
                    clients.add(temp);
                    (new Thread(temp)).start();
                    sendCommandToClient(IDCounter, 1, "sa 1");
                    sendCommandToClient(IDCounter, 0, "hd 1");
                    sendCommandToClient(IDCounter, 1, "hd 1");
                    System.out.println("Second client connected");
                    IDCounter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommandToClient(int gameID, int playerIndex, String command) {
        for (StrategoServerThread s : clients) {
            if (s.getGameID() == gameID && s.getPlayerIndex() == playerIndex) {
                System.out.println("Client found to send command to: \"" + command + "\".");
                s.sendCommand(command);
                return;
            }
        }
    }

    public void remove(int gameID) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getGameID() == gameID) {
                System.out.println("Game removed (ID: " + gameID + ").");
                clients.get(i).sendCommand("dc");
                clients.get(i).stopThread();
                clients.remove(i);
            }
        }
        ModelComManager.getInstance().removeStrategoGame(gameID);
    }

    public boolean gameStarted(int gameID) {
        for (StrategoServerThread c : clients) {
            if (c.getGameID() == gameID && c.getPlayerIndex() == 1) {
                System.out.println("Game has started (ID: " + gameID + ").");
                return true;
            }
        }
        return false;
    }

}
