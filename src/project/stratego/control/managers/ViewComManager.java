package project.stratego.control.managers;

import project.stratego.control.client.StrategoClient;
import project.stratego.game.utils.PieceType;
import project.stratego.ui.Messages;
import project.stratego.ui.sections.StrategoFrame;
import project.stratego.ui.sections.InGameView;

public class ViewComManager {

    private static ViewComManager instance;

    public static ViewComManager getInstance() {
        if (instance == null) {
            instance = new ViewComManager();
        }
        return instance;
    }

    private ViewComManager() {}

    private StrategoClient client;
    private StrategoFrame frame;

    private boolean multiPlayer;

    /* Methods for managing the connection */

    public boolean isConnected() {
        return multiPlayer && client != null;
    }

    public void setStrategoClient(StrategoClient client) {
        this.client = client;
    }

    public void closeStrategoClient() {
        client.sendCommandToServer("rg");
        client.stopThread();
        client = null;
    }

    public void setStrategoFrame(StrategoFrame frame) {
        this.frame = frame;
    }

    public void configureMultiPlayer() {
        multiPlayer = true;
        frame.getInGameView().processAssignSide(-1);
    }

    public void configureSinglePlayer() {
        multiPlayer = false;
        if (client != null) {
            client.stopThread();
            client = null;
        }
        ModelComManager.getInstance().configureSinglePlayer();
        requestResetGame();
        sendAssignSide(InGameView.DEFAULT_PLAYER_ID);
    }

    /* Requests from view to model */

    public void requestStartGame() {
        if (multiPlayer && client == null) {
            frame.getInGameView().processResetGame();
            StrategoClient client = new StrategoClient();
            (new Thread(client)).start();
            this.client = client;
        } else {
            // send request for AI to set up and play
            ModelComManager.getInstance().requestPlayerReady(-1, 0);
        }
    }

    public void requestResetGame() {
        if (isConnected()) {
            client.sendCommandToServer("rg");
        } else {
            ModelComManager.getInstance().requestResetGame(-1);
        }
    }

    public void requestAutoDeploy() {
        if (isConnected()) {
            client.sendCommandToServer("ad");
        } else {
            ModelComManager.getInstance().requestAutoDeploy(-1, InGameView.DEFAULT_PLAYER_ID);
        }
    }

    public void requestResetDeployment() {
        if (isConnected()) {
            client.sendCommandToServer("rd");
        } else {
            ModelComManager.getInstance().requestResetDeployment(-1, InGameView.DEFAULT_PLAYER_ID);
        }
    }

    public void requestPlayerReady() {
        if (isConnected()) {
            client.sendCommandToServer("pr");
        } else {
            ModelComManager.getInstance().requestPlayerReady(-1, InGameView.DEFAULT_PLAYER_ID);
        }
    }

    public void requestPlayerQuit() {
        if (isConnected()) {
            client.sendCommandToServer("q");
        }
    }

    public void requestTrayPieceSelected(int index) {
        if (isConnected()) {
            client.sendCommandToServer("tps " + index);
        } else {
            ModelComManager.getInstance().requestTrayPieceSelected(-1, InGameView.DEFAULT_PLAYER_ID, index);
        }
    }
    
    public void requestBoardTileSelected(int row, int col) {
        if (isConnected()) {
            client.sendCommandToServer("bts " + row + " " + col);
        } else {
            ModelComManager.getInstance().requestBoardTileSelected(-1, InGameView.DEFAULT_PLAYER_ID, row, col);
        }
    }

    /* Commands from model to view */

    public void sendTrayActiveUpdate(int pieceIndex) {
        frame.getInGameView().processTrayActiveUpdate(pieceIndex);
    }

    public void sendActivePieceUpdate(int gameID, int row, int col) {

    }

    public void sendOpponentQuit() {
        //System.out.println("OPPONENT QUIT");
        Messages.showOpponentDisonnectedMessage();
        frame.getInGameView().processResetGame();
    }

    public void sendAssignSide(int playerIndex) {
        frame.getInGameView().processAssignSide(playerIndex);
    }

    public void sendHighlightDeployment(int highlight) {
        frame.getInGameView().processHighlightDeployment(highlight != 0);
    }

    public void sendResetGame() {
        frame.getInGameView().processResetGame();
    }

    public void sendResetDeployment(int playerIndex) {
        frame.getInGameView().processResetDeployment(playerIndex);
    }

    public void sendChangeTurn(int playerIndex) {
        frame.getInGameView().processChangeTurn(playerIndex);
    }

    public void sendPiecePlaced(int playerIndex, int pieceIndex, int row, int col) {
        //System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + " (ViewComManager).");
        frame.getInGameView().processPiecePlaced(playerIndex, pieceIndex, row, col);
        if (frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        frame.getInGameView().processPieceMoved(orRow, orCol, destRow, destCol);
    }

    public void sendHidePiece(int playerIndex, int row, int col) {
        if (frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendRevealPiece(int playerIndex, int row, int col) {
        if (frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processRevealPiece(row, col);
        }
    }

    // NOTE: the attack methods still have to be fixed (is the processing done on the client or server side?)

    public void sendAttackLost(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackLost(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackTied(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackTied(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackWon(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackWon(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendGameOver(int winnerPlayerIndex) {
        frame.getInGameView().processGameOver(winnerPlayerIndex);
        frame.getSideMenu().reset();
        if (multiPlayer) {
            closeStrategoClient();
        }
    }

    public void sendRevealAll() {
        frame.getInGameView().revealAll();
    }

}
