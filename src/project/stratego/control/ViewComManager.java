package project.stratego.control;

import project.stratego.ui.StrategoFrame;

/**
 *
 */
public class ViewComManager implements ViewReceiver {

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

    public void setStrategoClient(StrategoClient client) {
        this.client = client;
    }

    @Override
    public void setStrategoFrame(StrategoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void sendResetGame() {
        client.sendCommandToServer("rs");
    }

    @Override
    public void sendAutoDeploy(int playerIndex) {
        client.sendCommandToServer("ad");
    }

    @Override
    public void sendPlayerReady(int playerIndex) {
        client.sendCommandToServer("pr");
    }

    @Override
    public void sendTrayPieceSelected(int playerIndex, int index) {
        client.sendCommandToServer("tps " + index);
    }

    @Override
    public void sendBoardTileSelected(int playerIndex, int row, int col) {
        client.sendCommandToServer("bts " + row + " " + col);
    }

    /* Model to view methods (sent by the client) */

    public void sendTrayActiveUpdate(int gameID, int pieceIndex) {

    }

    public void sendActivePieceUpdate(int gameID, int row, int col) {

    }

    public void sendAssignSide(int gameID, int playerIndex) {
        frame.getInGameView().processAssignSide(playerIndex);
    }

    public void sendResetDeployment(int gameID, int playerIndex) {
        frame.getInGameView().processResetDeployment(playerIndex);
    }

    public void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col) {
        frame.getInGameView().processPiecePlaced(playerIndex, pieceIndex, row, col);
        if (frame.getInGameView().getID() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
    }

    public void sendHidePiece(int gameID, int playerIndex, int row, int col) {
        frame.getInGameView().processHidePiece(row, col);
    }

    public void sendRevealPiece(int gameID, int playerIndex, int row, int col) {
        frame.getInGameView().processRevealPiece(row, col);
    }

    // NOTE: the attack methods still have to be fixed (is the processing done on the client or server side?)

    public void sendAttackLost(int gameID, int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackLost(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackTied(int gameID, int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackTied(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackWon(int gameID, int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        frame.getInGameView().processAttackWon(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

}
