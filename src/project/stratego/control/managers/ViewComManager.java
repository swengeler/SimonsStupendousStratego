package project.stratego.control.managers;

import project.stratego.control.client.StrategoClient;
import project.stratego.ui.utils.Messages;
import project.stratego.ui.sections.StrategoFrame;
import project.stratego.ui.sections.InGameView;

import java.io.File;

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

    private GameMode gameMode;

    /* Methods for managing the connection */

    public void closeProgram() {
        if (isConnected()) {
            closeStrategoClient();
        } else if (gameMode != GameMode.MULTIPLAYER) {
            ModelComManager.getInstance().closeProgram();
        }
    }

    public boolean isConnected() {
        return gameMode == GameMode.MULTIPLAYER && client != null;
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
        gameMode = GameMode.MULTIPLAYER;
        frame.getInGameView().processAssignSide(-1);
    }

    public void configureSinglePlayer() {
        gameMode = GameMode.SINGLEPLAYER;
        if (client != null) {
            client.stopThread();
            client = null;
        }
        requestResetGame();
        ModelComManager.getInstance().configureSinglePlayer();
        sendAssignSide(InGameView.DEFAULT_PLAYER_ID);
    }

    public void configureAIShowMatch() {
        gameMode = GameMode.AISHOWMATCH;
        if (client != null) {
            client.stopThread();
            client = null;
        }
        requestResetGame();
        ModelComManager.getInstance().configureAIShowMatch();
        sendAssignSide(-1);
    }

    /* Requests from view to model */

    public void requestStartGame() {
        if (gameMode == GameMode.MULTIPLAYER && client == null) {
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

    public void requestNextMove() {
        if (gameMode == GameMode.AISHOWMATCH) {
            ModelComManager.getInstance().requestNextMove();
        }
    }

    public void requestLoadSetup(int playerIndex, String setupEncoding) {
        if (isConnected()) {
            client.sendCommandToServer("ls " + setupEncoding);
        } else {
            ModelComManager.getInstance().requestLoadSetup(-1, playerIndex, setupEncoding);
        }
    }

    public void requestSaveSetup(int playerIndex, String filePath) {
        if (gameMode != GameMode.MULTIPLAYER) {
            ModelComManager.getInstance().requestSaveSetup(-1, playerIndex, filePath);
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
        if (gameMode != GameMode.AISHOWMATCH && frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        System.out.println("In ViewComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
        frame.getInGameView().processPieceMoved(orRow, orCol, destRow, destCol);
    }

    public void sendHidePiece(int playerIndex, int row, int col) {
        if (gameMode != GameMode.AISHOWMATCH && frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processHidePiece(row, col);
        }
    }

    public void sendRevealPiece(int playerIndex, int row, int col) {
        if (gameMode != GameMode.AISHOWMATCH && frame.getInGameView().getPlayerIndex() != playerIndex) {
            frame.getInGameView().processRevealPiece(row, col);
        }
    }

    // NOTE: the attack methods still have to be fixed (is the processing done on the client or server side?)

    public void sendAttackLost(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("In ViewComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
        frame.getInGameView().processAttackLost(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackTied(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("In ViewComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
        frame.getInGameView().processAttackTied(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendAttackWon(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("In ViewComManager: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
        frame.getInGameView().processAttackWon(orRow, orCol, stopRow, stopCol, destRow, destCol);
    }

    public void sendGameOver(int winnerPlayerIndex) {
        if (gameMode == GameMode.SINGLEPLAYER || gameMode == GameMode.AISHOWMATCH) {
            frame.getInGameView().processGameOver(winnerPlayerIndex);
        }
        frame.getSideMenu().reset();
        if (gameMode == GameMode.MULTIPLAYER) {
            closeStrategoClient();
        }
    }

    public void revealAll() {
        frame.getInGameView().revealAll();
    }

}
