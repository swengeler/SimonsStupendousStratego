package project.stratego.control;

import project.stratego.game.DeploymentLogic;
import project.stratego.game.StrategoGame;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ModelComManager {

    private static ModelComManager instance;

    public static ModelComManager getInstance() {
        if (instance == null) {
            instance = new ModelComManager();
        }
        return instance;
    }

    private StrategoServer server;
    private ArrayList<StrategoGame> activeGames;

    private ModelComManager() {
        activeGames = new ArrayList<>();
    }

    public void setStrategoServer(StrategoServer server) {
        this.server = server;
    }

    public void addStrategoGame(int gameID) {
        // note: the activeGames.size() call returns the size before adding new game
        activeGames.add(new StrategoGame(gameID));
        System.out.println("New game created (ID: " + gameID + ").");
    }

    public void removeStrategoGame(int gameID) {
        for (int i = 0; i < activeGames.size(); i++) {
            if (activeGames.get(i).getGameID() == gameID) {
                activeGames.remove(i);
                return;
            }
        }
    }

    private StrategoGame findGame(int gameID) {
        for (StrategoGame g : activeGames) {
            if (g.getGameID() == gameID) {
                return g;
            }
        }
        return null;
    }

    /* View to model methods (sent by the server) */

    public void requestResetGame(int gameID) {
        findGame(gameID).resetGame();
    }

    public void requestAutoDeploy(int gameID, int playerIndex) {
        // need proper method for this
        if (findGame(gameID).getCurrentState() instanceof DeploymentLogic) {
            ((DeploymentLogic) findGame(gameID).getCurrentState()).randomPlaceCurrentPlayer(playerIndex);
        }
    }

    public void requestResetDeployment(int gameID, int playerIndex) {
        if (findGame(gameID).getCurrentState() instanceof DeploymentLogic) {
            ((DeploymentLogic) findGame(gameID).getCurrentState()).resetDeployment(playerIndex);
        }
    }

    public void requestPlayerReady(int gameID, int playerIndex) {
        findGame(gameID).getCurrentState().processPlayerReady(playerIndex);
    }

    public void requestTrayPieceSelected(int gameID, int playerIndex, int pieceIndex) {
        findGame(gameID).getCurrentState().processTraySelect(playerIndex, pieceIndex);
    }

    public void requestBoardTileSelected(int gameID, int playerIndex, int row, int col) {
        findGame(gameID).getCurrentState().processBoardSelect(playerIndex, row, col);
    }

    /* Model to view methods*/

    public void sendTrayActiveUpdate(int gameID, int pieceIndex) {

    }

    public void sendActivePieceUpdate(int gameID, int playerIndex, int row, int col) {

    }

    public void sendResetDeployment(int gameID, int playerIndex) {
        // rd = reset deployment, s = self, o = opponent
        server.sendCommandToClient(gameID, 0, ("rd " + playerIndex));
        server.sendCommandToClient(gameID, 1, ("rd " + playerIndex));
    }

    public void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col) {
        // pp = piece placed, s = self, o = opponent
        System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + ".");
        server.sendCommandToClient(gameID, 0, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
        server.sendCommandToClient(gameID, 1, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // pm = piece moved
        server.sendCommandToClient(gameID, 0, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
        server.sendCommandToClient(gameID, 1, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
    }

    public void sendHidePiece(int gameID, int playerIndex, int row, int col) {
        // ph = piece hidden
        server.sendCommandToClient(gameID, playerIndex, ("ph " + row + " " + col));
    }

    public void sendRevealPiece(int gameID, int playerIndex, int row, int col) {
        // ph = piece revealed
        server.sendCommandToClient(gameID, playerIndex, ("pr " + row + " " + col));
    }

    public void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // al = attack lost
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
        } else {
            // move was vertical
            server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
        }
    }

    public void sendAttackTied(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // at = attack tied
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
        } else {
            // move was vertical
            server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
        }
    }

    public void sendAttackWon(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // aw = attack won
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
        } else {
            // move was vertical
            server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
        }
    }

    public void sendGameOver(int gameID) {
        server.sendCommandToClient(gameID, 0, "go");
        server.sendCommandToClient(gameID, 1, "go");
        server.remove(gameID);
        activeGames.remove(findGame(gameID));
    }

}
