package project.stratego.control.managers;

import project.stratego.control.server.StrategoServer;
import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.GameState;
import project.stratego.game.logic.DeploymentLogic;
import project.stratego.game.StrategoGame;
import project.stratego.game.utils.PlayerType;

import java.io.*;
import java.util.ArrayList;

public class ModelComManager {

    private static ModelComManager instance;

    public static ModelComManager getInstance() {
        if (instance == null) {
            instance = new ModelComManager();
        }
        return instance;
    }

    /* Fields, constructors and methods to do with managing the manager itself */

    private StrategoServer server;
    private ArrayList<StrategoGame> activeGames;

    private GameMode gameMode;

    private boolean programRunning;

    private ModelComManager() {
        activeGames = new ArrayList<>();
    }

    public void closeProgram() {
        programRunning = false;
    }

    public void setStrategoServer(StrategoServer server) {
        this.server = server;
    }

    public void addStrategoGame(int gameID) {
        // note: the activeGames.size() call returns the size before adding new game
        activeGames.add(new StrategoGame(gameID));
        //System.out.println("New game created (ID: " + gameID + ").");
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

    /* Methods to configure the game mode */

    public void configureMultiPlayer() {
        System.out.println("Switched to MULTIPLAYER in ViewComManager");
        gameMode = GameMode.MULTIPLAYER;
        AIComManager.getInstance().configureMultiPlayer();
    }

    public void configureSinglePlayer() {
        System.out.println("Switched to SINGLEPLAYER in ViewComManager");
        gameMode = GameMode.SINGLEPLAYER;
        activeGames.clear();
        activeGames.add(new StrategoGame(-1));
        AIComManager.getInstance().configureSinglePlayer();
        //AIComManager.getInstance().setPrimaryAI("mcts", PlayerType.SOUTH.ordinal());
        //AIComManager.getInstance().setPrimaryAI("expectinegamax 2", PlayerType.SOUTH.ordinal());
        //AIComManager.getInstance().setPrimaryAI("iterdeepexp", PlayerType.SOUTH.ordinal());
        AIComManager.getInstance().setPrimaryAI("star2 2", PlayerType.SOUTH.ordinal());
        AIComManager.getInstance().tryBoardSetup(findGame(-1).getGameState());
        sendDeploymentUpdate(-1, PlayerType.SOUTH.ordinal());
        requestPlayerReady(-1, PlayerType.SOUTH.ordinal());
    }

    public void configureAIMatch() {
        System.out.println("Switched to AIMATCH in ViewComManager");
        gameMode = GameMode.AIMATCH;
        activeGames.clear();
        activeGames.add(new StrategoGame(-1));
        AIComManager.getInstance().configureAIMatch();
        AIComManager.getInstance().setSecondaryAI("random", PlayerType.NORTH.ordinal());
        //AIComManager.getInstance().setPrimaryAI("random", PlayerType.SOUTH.ordinal());
        //AIComManager.getInstance().setSecondaryAI("mcts", PlayerType.NORTH.ordinal());
        //AIComManager.getInstance().setSecondaryAI("expectinegamax 1", PlayerType.NORTH.ordinal());
        AIComManager.getInstance().setPrimaryAI("expectinegamax 2", PlayerType.SOUTH.ordinal());
        //AIComManager.getInstance().setPrimaryAI("iterdeepexp 2000", PlayerType.SOUTH.ordinal());
        //AIComManager.getInstance().setPrimaryAIEval("marks");
        AIComManager.getInstance().tryBoardSetup(findGame(-1).getGameState());
        //findGame(-1).getGameState().printBoard();
        requestPlayerReady(-1, PlayerType.NORTH.ordinal());
        requestPlayerReady(-1, PlayerType.SOUTH.ordinal());
    }

    public void configureAIShowMatch() {
        System.out.println("Switched to AISHOWMATCH in ViewComManager");
        gameMode = GameMode.AISHOWMATCH;
        activeGames.clear();
        activeGames.add(new StrategoGame(-1));
        AIComManager.getInstance().configureAIShowMatch();
        AIComManager.getInstance().setSecondaryAI("random", PlayerType.NORTH.ordinal());
        //AIComManager.getInstance().setPrimaryAI("expectinegamax", PlayerType.SOUTH.ordinal());
        AIComManager.getInstance().setPrimaryAI("mcts", PlayerType.SOUTH.ordinal());
        AIComManager.getInstance().tryBoardSetup(findGame(-1).getGameState());
        sendDeploymentUpdate(-1, PlayerType.NORTH.ordinal());
        sendDeploymentUpdate(-1, PlayerType.SOUTH.ordinal());
        requestPlayerReady(-1, PlayerType.NORTH.ordinal());
        requestPlayerReady(-1, PlayerType.SOUTH.ordinal());
    }

    /* View to model methods */

    public void requestResetGame(int gameID) {
        if (gameMode == GameMode.AIMATCH) {
            return;
        }
        if (findGame(gameID) != null) {
            //System.out.println("Request reset game");
            findGame(gameID).resetGame();
            sendResetGame(gameID);
            if (gameMode != GameMode.MULTIPLAYER) {
                AIComManager.getInstance().reset();
            }
        }
    }

    public void requestAutoDeploy(int gameID, int playerIndex) {
        // need proper method for this
        if (findGame(gameID) != null && !findGame(gameID).gameRunning()) {
            if (gameMode == GameMode.MULTIPLAYER && !server.gameStarted(gameID)) {
                return;
            }
            ((DeploymentLogic) findGame(gameID).getCurrentRequestProcessor()).randomPlaceCurrentPlayer(playerIndex);
        }
    }

    public void requestResetDeployment(int gameID, int playerIndex) {
        if (findGame(gameID) != null && !findGame(gameID).gameRunning()) {
            if (gameMode == GameMode.MULTIPLAYER && !server.gameStarted(gameID)) {
                return;
            }
            ((DeploymentLogic) findGame(gameID).getCurrentRequestProcessor()).resetDeployment(playerIndex);
        }
    }

    public void requestPlayerReady(int gameID, int playerIndex) {
        //if (gameMode != GameMode.AIMATCH) {
            if (findGame(gameID) != null) {
                findGame(gameID).getCurrentRequestProcessor().processPlayerReady(playerIndex);
            }
        //}
    }

    public void requestTrayPieceSelected(int gameID, int playerIndex, int pieceIndex) {
        if (findGame(gameID) != null && !findGame(gameID).gameRunning()) {
            if (gameMode == GameMode.MULTIPLAYER && !server.gameStarted(gameID)) {
                return;
            }
            //System.out.println("Process tray select");
            findGame(gameID).getCurrentRequestProcessor().processTraySelect(playerIndex, pieceIndex);
        }
    }

    public void requestBoardTileSelected(int gameID, int playerIndex, int row, int col) {
        if (gameMode == GameMode.MULTIPLAYER && !server.gameStarted(gameID)) {
            return;
        }
        if (findGame(gameID) != null) {
            findGame(gameID).getCurrentRequestProcessor().processBoardSelect(playerIndex, row, col);
        }
    }

    public void requestNextMove() {
        if (findGame(-1) == null || !findGame(-1).gameRunning()) {
            return;
        }
        if (gameMode == GameMode.SINGLEPLAYER) {
            AIComManager.getInstance().tryNextMove(findGame(-1).getGameState().getMoveHistory().getLast());
        } else if (gameMode == GameMode.AISHOWMATCH) {
            AIComManager.getInstance().advanceAIMatch();
        }
    }

    public void requestLoadSetup(int gameID, int playerIndex, String setupEncoding) {
        if (findGame(gameID).gameRunning()) {
            return;
        }

        requestResetDeployment(gameID, playerIndex);
        String[] pieceIndexStrings = setupEncoding.split("-");
        if (pieceIndexStrings.length != 40) {
            System.out.println("Something wrong with the loaded setup.");
            System.exit(1);
        }
        int[] pieceIndexIntegers = new int[40];
        for (int i = 0; i < pieceIndexStrings.length; i++) {
            pieceIndexIntegers[i] = Integer.parseInt(pieceIndexStrings[i]);
            requestTrayPieceSelected(gameID, playerIndex, pieceIndexIntegers[i]);
            requestBoardTileSelected(gameID, playerIndex, playerIndex == PlayerType.NORTH.ordinal() ? 3 - i / 10 : 6 + i / 10, playerIndex == PlayerType.NORTH.ordinal() ? 9 - i % 10 : i % 10);
        }
    }

    public void requestLoadGame(int gameID, String gameEncoding) {
        if (gameMode == GameMode.MULTIPLAYER) {
            return;
        }

        activeGames.clear();
        activeGames.add(new StrategoGame(-1));
        String[] encodings = gameEncoding.split("\n");
        AIComManager.getInstance().setGameLoaded(true);
        findGame(-1).getGameState().interpretEncodedBoard(encodings[0]);
        findGame(-1).getGameState().interpretEncodedMoves(encodings[1]);
        ViewComManager.getInstance().sendResetGame();
        sendBoardUpdate(gameID);
        findGame(-1).switchStates();

        System.out.println("Gamemode: " + gameMode);
        if (gameMode == GameMode.SINGLEPLAYER) {
            AIComManager.getInstance().configureSinglePlayer();
            AIComManager.getInstance().setPrimaryAI("expectinegamax", PlayerType.SOUTH.ordinal());
        } else {
            if (gameMode == GameMode.AISHOWMATCH) {
                AIComManager.getInstance().configureAIShowMatch();
            } else if (gameMode == GameMode.AIMATCH) {
                AIComManager.getInstance().configureAIMatch();
            }
            AIComManager.getInstance().setSecondaryAI("random", PlayerType.NORTH.ordinal());
            AIComManager.getInstance().setPrimaryAI("expectinegamax", PlayerType.SOUTH.ordinal());
        }
        AIComManager.getInstance().tryLoadGame(gameEncoding);
        AIComManager.getInstance().setGameLoaded(false);
    }

    public void requestSaveSetup(int gameID, int playerIndex, String filePath) {
        if (findGame(gameID).getGameState().getPlayer(playerIndex).getActivePieces().size() != 40 || findGame(gameID).gameRunning()) {
            return;
        }
        String setupEncoding = findGame(gameID).getGameState().getSetupEncoding(playerIndex);
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, playerIndex, "ss " + filePath + " " + setupEncoding);
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendSaveSetup(filePath, setupEncoding);
        }
    }

    public void requestSaveGame(int gameID, int playerIndex, String filePath) {
        if (!findGame(gameID).gameRunning()) {
            return;
        }
        String setupEncoding = findGame(gameID).getGameState().getInitBoardEncoding() + "\n" + findGame(gameID).getGameState().getMoveEncoding();
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, playerIndex, "ss " + filePath + " " + setupEncoding);
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendSaveSetup(filePath, setupEncoding);
        }
    }

    public void requestConfigureAIType(int playerIndex) {

    }

    public void requestConfigureSetup(int playerIndex) {

    }

    /* Model to view methods*/

    public void sendResetGame(int gameID) {
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, 0, "rg");
            server.sendCommandToClient(gameID, 1, "rg");
            server.remove(gameID);
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendResetGame();
        }
    }

    public void sendDeploymentUpdate(int gameID, int playerIndex) {
        if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            BoardTile[][] board = findGame(gameID).getBoard();
            for (int row = 0; row < GameState.BOARD_SIZE; row++) {
                for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                    if (board[row][col].getOccupyingPiece() != null && board[row][col].getOccupyingPiece().getPlayerType().ordinal() == playerIndex) {
                        sendPiecePlaced(gameID, playerIndex, board[row][col].getOccupyingPiece().getType().ordinal(), row, col);
                    }
                }
            }
        }
    }

    public void sendBoardUpdate(int gameID) {
        if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            BoardTile[][] board = findGame(gameID).getBoard();
            for (int row = 0; row < GameState.BOARD_SIZE; row++) {
                for (int col = 0; col < GameState.BOARD_SIZE; col++) {
                    if (board[row][col].getOccupyingPiece() != null) {
                        sendPiecePlaced(gameID, board[row][col].getOccupyingPiece().getPlayerType().ordinal(), board[row][col].getOccupyingPiece().getType().ordinal(), row, col);
                        if (board[row][col].getOccupyingPiece().isRevealed()) {
                            sendRevealPiece(gameID, board[row][col].getOccupyingPiece().getPlayerType().ordinal(), row, col);
                        }
                    }
                }
            }
        }
    }

    public void sendPlayerQuit(int gameID, int playerIndex) {
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, playerIndex == 0 ? 1 : 0, "oq");
        }
    }

    public void sendChangeTurn(int gameID, int playerIndex) {
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, 0, ("ct " + playerIndex));
            server.sendCommandToClient(gameID, 1, ("ct " + playerIndex));
        } else if (gameMode == GameMode.SINGLEPLAYER) {
            ViewComManager.getInstance().sendChangeTurn(playerIndex);
            if (!findGame(-1).getGameState().getMoveHistory().isEmpty()) {
                //AIComManager.getInstance().tryNextMove(findGame(-1).getGameState().getMoveHistory().getLast());
            } else {
                //AIComManager.getInstance().tryNextMove(new Move(playerIndex, -1, -1, -1, -1));
            }
        } else if (gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendChangeTurn(playerIndex);
        }
    }

    public void sendTrayActiveUpdate(int gameID, int playerIndex, int pieceIndex) {
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, playerIndex, ("ta " + pieceIndex));
        } else if (gameMode == GameMode.SINGLEPLAYER) {
            ViewComManager.getInstance().sendTrayActiveUpdate(pieceIndex);
        }
    }

    public void sendActivePieceUpdate(int gameID, int playerIndex, int row, int col) {

    }

    public void sendResetDeployment(int gameID, int playerIndex) {
        if (gameMode == GameMode.MULTIPLAYER) {
            // rd = reset deployment, s = self, o = opponent
            server.sendCommandToClient(gameID, 0, ("rd " + playerIndex));
            server.sendCommandToClient(gameID, 1, ("rd " + playerIndex));
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendResetDeployment(playerIndex);
        }
    }

    public void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col) {
        if (gameMode == GameMode.MULTIPLAYER) {
            // pp = piece placed, s = self, o = opponent
            //System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + ".");
            server.sendCommandToClient(gameID, 0, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
            server.sendCommandToClient(gameID, 1, ("pp " + playerIndex + " " + pieceIndex + " " + row + " " + col));
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            ViewComManager.getInstance().sendPiecePlaced(playerIndex, pieceIndex, row, col);
        }
    }

    public void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol) {
        if (gameMode == GameMode.MULTIPLAYER) {
            // pm = piece moved
            server.sendCommandToClient(gameID, 0, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
            server.sendCommandToClient(gameID, 1, ("pm " + orRow + " " + orCol + " " + destRow + " " + destCol));
        } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
            ViewComManager.getInstance().sendPieceMoved(orRow, orCol, destRow, destCol);
        }
    }

    public void sendHidePiece(int gameID, int playerIndex, int row, int col) {
        if (gameMode == GameMode.MULTIPLAYER) {
            // ph = piece hidden
            server.sendCommandToClient(gameID, playerIndex, ("ph " + playerIndex + " " + row + " " + col));
        } else if (gameMode == GameMode.SINGLEPLAYER) {
            ViewComManager.getInstance().sendHidePiece(playerIndex, row, col);
        }
    }

    public void sendRevealPiece(int gameID, int playerIndex, int row, int col) {
        if (gameMode == GameMode.MULTIPLAYER) {
            // ph = piece revealed
            server.sendCommandToClient(gameID, playerIndex, ("pr " + playerIndex + " " + row + " " + col));
        } else if (gameMode == GameMode.SINGLEPLAYER) {
            ViewComManager.getInstance().sendRevealPiece(playerIndex, row, col);
        }
    }

    public void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol) {
        // al = attack lost
        int rowDiff = destRow - orRow;
        int colDiff = destCol - orCol;
        if (rowDiff != 0) {
            // move was horizontal
            if (gameMode == GameMode.MULTIPLAYER) {
                server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
                ViewComManager.getInstance().sendAttackLost(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            if (gameMode == GameMode.MULTIPLAYER) {
                // move was vertical
                server.sendCommandToClient(gameID, 0, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("al " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
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
            if (gameMode == GameMode.MULTIPLAYER) {
                server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
                ViewComManager.getInstance().sendAttackTied(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            // move was vertical
            if (gameMode == GameMode.MULTIPLAYER) {
                server.sendCommandToClient(gameID, 0, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("at " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
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
            if (gameMode == GameMode.MULTIPLAYER) {
                server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + (rowDiff < 0 ? destRow + 1 : destRow - 1) + " " + destCol + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
                ViewComManager.getInstance().sendAttackWon(orRow, orCol, (rowDiff < 0 ? destRow + 1 : destRow - 1), destCol, destRow, destCol);
            }
        } else {
            // move was vertical
            if (gameMode == GameMode.MULTIPLAYER) {
                server.sendCommandToClient(gameID, 0, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
                server.sendCommandToClient(gameID, 1, ("aw " + orRow + " " + orCol + " " + destRow + " " + (colDiff < 0 ? destCol + 1 : destCol - 1) + " " + destRow + " " + destCol));
            } else if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
                //System.out.println("In ModelComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
                ViewComManager.getInstance().sendAttackWon(orRow, orCol, destRow, (colDiff < 0 ? destCol + 1 : destCol - 1), destRow, destCol);
            }
        }
    }

    public void sendGameOver(int gameID, int winnerPlayerIndex) {
        if (gameMode == GameMode.MULTIPLAYER) {
            server.sendCommandToClient(gameID, 0, ("go " + winnerPlayerIndex));
            server.sendCommandToClient(gameID, 1, ("go " + winnerPlayerIndex));
            server.remove(gameID);
            activeGames.remove(findGame(gameID));
        } else if (gameMode != GameMode.AIMATCH) {
            activeGames.remove(findGame(-1));
            //configureMultiPlayer();
            //configureSinglePlayer();
            ViewComManager.getInstance().sendGameOver(winnerPlayerIndex);
        }
    }

}
