package project.stratego.control;

import project.stratego.game.utils.PieceType;
import project.stratego.ui.StrategoFrame;

/**
 *
 */
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

    /* Methods for managing the connection */

    public boolean isConnected() {
        return client != null;
    }

    public void setStrategoClient(StrategoClient client) {
        this.client = client;
    }

    public void closeStrategoClient() {
        client.stopThread();
        client = null;
    }

    public void setStrategoFrame(StrategoFrame frame) {
        this.frame = frame;
    }

    /* Requests from view to model */

    public void requestResetGame() {
        client.sendCommandToServer("rs");
    }

    public void requestAutoDeploy() {
        client.sendCommandToServer("ad");
    }

    public void requestResetDeployment() {
        client.sendCommandToServer("rd");
    }

    public void requestPlayerReady() {
        client.sendCommandToServer("pr");
    }

    public void requestTrayPieceSelected(int playerIndex, int index) {
        if (playerIndex == 0 || playerIndex == 1) {
            client.sendCommandToServer("tps " + index);
        }
    }
    
    public void requestBoardTileSelected(int playerIndex, int row, int col) {
        if (playerIndex == 0 || playerIndex == 1) {
            client.sendCommandToServer("bts " + row + " " + col);
        }
    }

    /* Commands from model to view */

    public void sendTrayActiveUpdate(int gameID, int pieceIndex) {

    }

    public void sendActivePieceUpdate(int gameID, int row, int col) {

    }

    public void sendAssignSide(int playerIndex) {
        frame.getInGameView().processAssignSide(playerIndex);
    }

    public void sendResetDeployment(int playerIndex) {
        frame.getInGameView().processResetDeployment(playerIndex);
    }

    public void sendPiecePlaced(int playerIndex, int pieceIndex, int row, int col) {
        System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + " (ViewComManager).");
        frame.getInGameView().processPiecePlaced(playerIndex, pieceIndex, row, col);
        if (frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        frame.getInGameView().processPieceMoved(orRow, orCol, destRow, destCol);
    }

    public void sendHidePiece(int row, int col) {
        frame.getInGameView().processHidePiece(row, col);
    }

    public void sendRevealPiece(int row, int col) {
        frame.getInGameView().processRevealPiece(row, col);
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

    public void sendGameOver() {
        frame.getInGameView().processGameOver();
    }

}
