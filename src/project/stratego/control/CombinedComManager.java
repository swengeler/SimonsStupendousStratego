package project.stratego.control;

import project.stratego.game.entities.Piece;
import project.stratego.game.*;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;
import project.stratego.ui.StrategoFrame;

public class CombinedComManager {

    private static CombinedComManager instance;

    private StrategoFrame strategoFrame;
    private StrategoGame strategoGame;

    public static CombinedComManager getInstance() {
        if (instance == null) {
            instance = new CombinedComManager();
        }
        return instance;
    }

    private CombinedComManager() {}

    public void setStrategoFrame(StrategoFrame strategoFrame) {
        this.strategoFrame = strategoFrame;
    }

    public void setStrategoGame(StrategoGame strategoGame) {
        this.strategoGame = strategoGame;
    }

    public void removeStrategoFrame() {
        strategoFrame = null;
    }

    public void removeStrategoGame() {
        strategoGame = null;
    }

    public boolean isReady() {
        return ((strategoFrame != null) && (strategoGame != null));
    }

    /* View to model methods */

    public void sendResetGame() {
        strategoGame.resetGame();
    }

    public void sendAutoDeploy() {
        if (strategoGame.getCurrentState() instanceof DeploymentLogic)
            ((DeploymentLogic) strategoGame.getCurrentState()).randomPlaceCurrentPlayer(-1);
    }

    public void sendPlayerReady() {
        strategoGame.getCurrentState().processPlayerReady(-1);
    }

    public void sendTrayPieceSelected(int playerIndex, int pieceIndex) {
        strategoGame.getCurrentState().processTraySelect(playerIndex, pieceIndex);
    }

    public void sendBoardTileSelected(int row, int col) {
        strategoGame.getCurrentState().processBoardSelect(-1, row, col);
    }

    /* Model to view methods */

    // gameID is not used in the ComManager that is not part of the server-configuration
    // it is still part of the methods below to have matching interfaces (will try to find a more elegant solution)

    public void sendTrayActiveUpdate(int gameID, int pieceIndex) {
        // send command to highlight a certain tray icon
    }

    public void sendActivePieceUpdate(int gameID, Piece activePiece) {

    }

    public void sendPiecePlaced(int gameID, Piece piecePlaced) {
        strategoFrame.getInGameView().processPiecePlaced(piecePlaced.getPlayerType().ordinal(), piecePlaced.getType().ordinal(), piecePlaced.getRowPos(), piecePlaced.getColPos());
    }

    public void sendResetDeployment(int gameID, PlayerType playerType) {
        strategoFrame.getInGameView().processResetDeployment(playerType.ordinal());
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
        strategoFrame.getInGameView().processPieceMoved(orRow, orCol, destRow, destCol);
    }

    public void sendHidePiece(int gameID, Piece pieceToHide) {
        strategoFrame.getInGameView().processHidePiece(pieceToHide.getRowPos(), pieceToHide.getColPos());
    }

    public void sendRevealPiece(int gameID, Piece pieceToReveal) {
        strategoFrame.getInGameView().processRevealPiece(pieceToReveal.getRowPos(), pieceToReveal.getColPos());
    }

    public void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol) {
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            strategoFrame.getInGameView().processAttackLost(orRow, orCol, rowDiff < 0 ? destRow + 1 : destRow - 1, destCol, destRow, destCol);
        } else {
            // move was vertical
            strategoFrame.getInGameView().processAttackLost(orRow, orCol, destRow, colDiff < 0 ? destCol + 1 : destCol - 1, destRow, destCol);
        }
    }

    public void sendAttackTied(int gameID, int orRow, int orCol, int destRow, int destCol) {
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            strategoFrame.getInGameView().processAttackTied(orRow, orCol, rowDiff < 0 ? destRow + 1 : destRow - 1, destCol, destRow, destCol);
        } else {
            // move was vertical
            strategoFrame.getInGameView().processAttackTied(orRow, orCol, destRow, colDiff < 0 ? destCol + 1 : destCol - 1, destRow, destCol);
        }
    }

    public void sendAttackWon(int gameID, int orRow, int orCol, int destRow, int destCol) {
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            strategoFrame.getInGameView().processAttackWon(orRow, orCol, rowDiff < 0 ? destRow + 1 : destRow - 1, destCol, destRow, destCol);
        } else {
            // move was vertical
            strategoFrame.getInGameView().processAttackWon(orRow, orCol, destRow, colDiff < 0 ? destCol + 1 : destCol - 1, destRow, destCol);
        }
    }

}
