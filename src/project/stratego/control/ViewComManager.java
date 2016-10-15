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

    private StrategoFrame frame;

    @Override
    public void setStrategoFrame(StrategoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void sendResetGame() {

    }

    @Override
    public void sendAutoDeploy() {

    }

    @Override
    public void sendPlayerReady() {

    }

    @Override
    public void sendTrayPieceSelected(int playerIndex, int index) {

    }

    @Override
    public void sendBoardTileSelected(int row, int col) {

    }

    /* Model to view methods (sent by the client) */

    public void sendTrayActiveUpdate(int gameID, int pieceIndex) {

    }

    public void sendActivePieceUpdate(int gameID, int row, int col) {

    }

    public void sendResetDeployment(int gameID, int playerIndex) {
    }

    public void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col) {
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
    }

    public void sendHidePiece(int gameID, int playerIndex, int row, int col) {
    }

    public void sendRevealPiece(int gameID, int playerIndex, int row, int col) {
    }

    public void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol) {
    }

    public void sendAttackTied(int gameID, int orRow, int orCol, int destRow, int destCol) {
    }

    public void sendAttackWon(int gameID, int orRow, int orCol, int destRow, int destCol) {
    }

}
