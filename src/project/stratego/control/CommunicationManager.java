package project.stratego.control;

import project.stratego.game.entities.Piece;
import project.stratego.game.*;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;
import project.stratego.ui.StrategoFrame;

public class CommunicationManager {

    private StrategoFrame strategoFrame;
    private StrategoGame strategoGame;

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
        ((DeploymentState) strategoGame.getCurrentState()).randomPlaceCurrentPlayer();
    }

    public void sendPlayerReady() {
        strategoGame.getCurrentState().processPlayerReady();
    }

    public void sendTrayPieceSelected(int playerIndex, int index) {
        strategoGame.getCurrentState().processTraySelect(PlayerType.values()[playerIndex], PieceType.values()[index]);
    }

    public void sendBoardTileSelected(int row, int col) {
        strategoGame.getCurrentState().processBoardSelect(row, col);
    }

    /* Model to view methods */

    public void sendTrayActiveUpdate(PieceType type) {
        // send command to highlight a certain tray icon
    }

    public void sendActivePieceUpdate(Piece activePiece) {
        // send command to board Piece with the same ID, eyoo, was geht da
    }

    public void sendPiecePlaced(Piece piecePlaced) {
        strategoFrame.getInGameView().processPiecePlaced(piecePlaced.getPlayerType().ordinal(), piecePlaced.getType().ordinal(), piecePlaced.getRowPos(), piecePlaced.getColPos());
    }

    public void sendResetDeployment(PlayerType playerType) {
        strategoFrame.getInGameView().processResetDeployment(playerType.ordinal());
    }

    public void sendPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        strategoFrame.getInGameView().processPieceMoved(orRow, orCol, destRow, destCol);
    }

    public void sendHidePiece(Piece pieceToHide) {
        strategoFrame.getInGameView().processHidePiece(pieceToHide.getRowPos(), pieceToHide.getColPos());
    }

    public void sendRevealPiece(Piece pieceToReveal) {
        strategoFrame.getInGameView().processRevealPiece(pieceToReveal.getRowPos(), pieceToReveal.getColPos());
    }

    public void sendAttackLost(int orRow, int orCol, int destRow, int destCol) {
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

    public void sendAttackTied(int orRow, int orCol, int destRow, int destCol) {
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

    public void sendAttackWon(int orRow, int orCol, int destRow, int destCol) {
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
