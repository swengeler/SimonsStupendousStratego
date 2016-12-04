package project.stratego.game;

import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.logic.*;
import project.stratego.game.utils.*;

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
        moveManager = new DiscreteMoveManager(gameState.getBoardArray());
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
        //System.out.println("States switched");
        //System.out.println("playerNorth has " + playerNorth.getActivePieces().size() + " pieces");
        currentRequestProcessor = currentRequestProcessor instanceof DeploymentLogic ? new PlayingLogic(this, gameState.getPlayerNorth(), gameState.getPlayerSouth()) : new DeploymentLogic(this, gameState.getPlayerNorth(), gameState.getPlayerSouth());
        ModelComManager.getInstance().sendChangeTurn(gameID, 0);
    }

    public void resetGame() {
        componentSetup();
    }

    public Player getPlayerNorth() {
        return gameState.getPlayerNorth();
    }

    public Player getPlayerSouth() {
        return gameState.getPlayerSouth();
    }

    public GameState getGameState() {
        return gameState;
    }

}
