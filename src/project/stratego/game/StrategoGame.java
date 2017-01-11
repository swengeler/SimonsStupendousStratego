package project.stratego.game;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.logic.*;
import project.stratego.game.moves.DiscreteMoveManager;
import project.stratego.game.moves.MoveManager;

public class StrategoGame {

    private final int gameID;

    private GameLogic currentRequestProcessor;
    private MoveManager moveManager;

    private GameState gameState;

    public StrategoGame(int gameID) {
        this.gameID = gameID;
        componentSetup();
    }

    private void componentSetup() {
        gameState = new GameState();
        currentRequestProcessor = new DeploymentLogic(this, gameState.getPlayerNorth(), gameState.getPlayerSouth());
    }

    /* Getter methods */

    public int getGameID() {
        return gameID;
    }

    public GameLogic getCurrentRequestProcessor() {
        return currentRequestProcessor;
    }

    public MoveManager getMoveManager() {
        return moveManager;
    }

    public BoardTile[][] getBoard() {
        return gameState.getBoardArray();
    }

    public void switchStates() {
        // save the initial setup
        gameState.saveInitBoard();
        moveManager = new DiscreteMoveManager(gameState.getBoardArray());
        currentRequestProcessor = new PlayingLogic(this, gameState.getPlayerNorth(), gameState.getPlayerSouth());
        AIComManager.getInstance().tryCopySetup(gameState);
        ModelComManager.getInstance().sendChangeTurn(gameID, 0);
    }

    public void resetGame() {
        componentSetup();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean gameRunning() {
        return currentRequestProcessor instanceof PlayingLogic;
    }

}
