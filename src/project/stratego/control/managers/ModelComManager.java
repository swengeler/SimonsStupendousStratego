package project.stratego.control.managers;

import project.stratego.control.server.StrategoServer;
import project.stratego.game.logic.DeploymentLogic;
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

    private boolean multiPlayer;

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

    public void configureMultiPlayer() {
        multiPlayer = true;
        AIComManager.getInstance().configureMultiPlayer();
    }

    public void configureSinglePlayer() {
        multiPlayer = false;
        activeGames.add(new StrategoGame(-1));
        AIComManager.getInstance().configureSinglePlayer();
    }

    /* View to model methods */

    public void requestResetGame(int gameID) {
        if (findGame(gameID) != null) {
            System.out.println("Request reset game");
            findGame(gameID).resetGame();
            sendResetGame(gameID);
            if (!multiPlayer) {
                AIComManager.getInstance().reset();
            }
        }
    }

    public void requestAutoDeploy(int gameID, int playerIndex) {
        // need proper method for this
        if (findGame(gameID) != null && findGame(gameID).getCurrentRequestProcessor() instanceof DeploymentLogic) {
            if (multiPlayer && !server.gameStarted(gameID)) {
                return;
            }
            ((DeploymentLogic) findGame(gameID).getCurrentRequestProcessor()).randomPlaceCurrentPlayer(playerIndex);
        }
    }

    public void requestResetDeployment(int gameID, int playerIndex) {
        if (findGame(gameID) != null && findGame(gameID).getCurrentRequestProcessor() instanceof DeploymentLogic) {
            if (multiPlayer && !server.gameStarted(gameID)) {
                return;
            }
            ((DeploymentLogic) findGame(gameID).getCurrentRequestProcessor()).resetDeployment(playerIndex);
        }
    }

    public void requestPlayerReady(int gameID, int playerIndex) {
        if (findGame(gameID) != null) {
            findGame(gameID).getCurrentRequestProcessor().processPlayerReady(playerIndex);
        }
    }

    public void requestTrayPieceSelected(int gameID, int playerIndex, int pieceIndex) {
        if (findGame(gameID) != null && findGame(gameID).getCurrentRequestProcessor() instanceof DeploymentLogic) {
            if (multiPlayer && !server.gameStarted(gameID)) {
                return;
            }
            System.out.println("Process tray select");
            findGame(gameID).getCurrentRequestProcessor().processTraySelect(playerIndex, pieceIndex);
        }
    }

    public void requestBoardTileSelected(int gameID, int playerIndex, int row, int col) {
        if (multiPlayer && !server.gameStarted(gameID)) {
            return;
        }
        if (findGame(gameID) != null) {
            findGame(gameID).getCurrentRequestProcessor().processBoardSelect(playerIndex, row, col);
        }
    }

    /* Model to view methods*/

    public void sendResetGame(int gameID) {
        if (multiPlayer) {
            server.sendCommandToClient(gameID, 0, "rg");
            server.sendCommandToClient(gameID, 1, "rg");
            server.remove(gameID);
        } else {
            ViewComManager.getInstance().sendResetGame();
        }
    }

    public void sendPlayerQuit(int gameID, int playerIndex) {
        if (multiPlayer) {
            server.sendCommandToClient(gameID, playerIndex == 0 ? 1 : 0, "oq");
        }
    }

    public void sendChangeTurn(int gameID, int playerIndex) {
        if (multiPlayer) {
            server.sendCommandToClient(gameID, 0, ("ct " + playerIndex));
            server.sendCommandToClient(gameID, 1, ("ct " + playerIndex));
        } else {
            ViewComManager.getInstance().sendChangeTurn(playerIndex);
        }
    }

    public void sendTrayActiveUpdate(int gameID, int playerIndex, int pieceIndex) {
        if (multiPlayer) {
            server.sendCommandToClient(gameID, playerIndex, ("ta " + pieceIndex));
        } else {
            ViewComManager.getInstance().sendTrayActiveUpdate(pieceIndex);
        }
    }

    public void sendActivePieceUpdate(int gameID, int playerIndex, int row, int col) {

    }

    public void sendResetDeployment(int gameID, int playerIndex) {
        if (multiPlayer) {
            // rd = reset deployment, s = self, o = opponent
            server.sendCommandToClient(gameID, 0, ("rd " + playerIndex));
            server.sendCommandToClient(gameID, 1, ("rd " + playerIndex));
        } else {
            ViewComManager.getInstance().sendResetDeployment(playerIndex);
        }
    }

    public void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col) {
        if (multiPlayer) {
            // pp = piece placed, s = self, o = opponent
            System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + ".");
            server.sendCommandToClient(gameID, 0, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
            server.sendCommandToClient(gameID, 1, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
        } else {
            ViewComManager.getInstance().sendPiecePlaced(playerIndex, pieceIndex, row, col);
        }
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
        if (multiPlayer) {
            // pm = piece moved
            server.sendCommandToClient(gameID, 0, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
        } else {
            ViewComManager.getInstance().sendPieceMoved(orRow, orCol, destRow, destCol);
        }
    }

    public void sendHidePiece(int gameID, int playerIndex, int row, int col) {
        if (multiPlayer) {
            // ph = piece hidden
            server.sendCommandToClient(gameID, playerIndex, ("ph " + playerIndex + " " + row + " " + col));
        } else {
            ViewComManager.getInstance().sendHidePiece(playerIndex, row, col);
        }
    }

    public void sendRevealPiece(int gameID, int playerIndex, int row, int col) {
        if (multiPlayer) {
            // ph = piece revealed
            server.sendCommandToClient(gameID, playerIndex, ("pr " + playerIndex + " " + row + " " + col));
        } else {
            ViewComManager.getInstance().sendRevealPiece(playerIndex, row, col);
        }
    }

    public void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // al = attack lost
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            if (multiPlayer) {
                server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackLost(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            if (multiPlayer) {
                // move was vertical
                server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackLost(orRow, orCol, destRow, (colDiff < 0 ? destCol + 1 : destCol - 1), destRow, destCol);
            }
        }
    }

    public void sendAttackTied(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // at = attack tied
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            if (multiPlayer) {
                server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackTied(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            // move was vertical
            if (multiPlayer) {
                server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackTied(orRow, orCol, destRow, (colDiff < 0 ? destCol + 1 : destCol - 1), destRow, destCol);
            }
        }
    }

    public void sendAttackWon(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // aw = attack won
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            if (multiPlayer) {
                server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackWon(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            // move was vertical
            if (multiPlayer) {
                server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else {
                ViewComManager.getInstance().sendAttackWon(orRow, orCol, destRow, (colDiff < 0 ? destCol + 1 : destCol - 1), destRow, destCol);
            }
        }
    }

    public void sendGameOver(int gameID, int winnerPlayerIndex) {
        if (multiPlayer) {
            server.sendCommandToClient(gameID, 0, ("go " + winnerPlayerIndex));
            server.sendCommandToClient(gameID, 1, ("go " + winnerPlayerIndex));
            server.remove(gameID);
            activeGames.remove(findGame(gameID));
        } else {
            ViewComManager.getInstance().sendGameOver(winnerPlayerIndex);
        }
    }

}
