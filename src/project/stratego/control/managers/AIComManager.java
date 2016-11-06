package project.stratego.control.managers;

import project.stratego.ai.*;
import project.stratego.game.StrategoGame;

public class AIComManager {

    private static AIComManager instance;

    public static AIComManager getInstance() {
        if (instance == null) {
            instance = new AIComManager();
        }
        return instance;
    }

    private AIComManager() {
        setAIMode("random");
    }

    private AIInterface currentAI;
    private boolean isActive;

    public void configureMultiPlayer() {
        isActive = false;
    }

    public void configureSinglePlayer() {
        isActive = true;
    }

    public void setAIMode(String aiType) {
        if (aiType.equals("random")) {
            currentAI = new RandomAI();
        }
    }

    public void tryBoardSetup(StrategoGame game, int playerIndex) {
        if (isActive && currentAI != null) {
            currentAI.makeBoardSetup(game, playerIndex);
        }
    }

    public void tryBoardSetup(StrategoGame game, int playerIndex, String aiType) {

    }

    public void tryNextMove(StrategoGame game, int playerIndex) {
        System.out.println("TEST: " + game.getBoard()[4][1].getOccupyingPiece());
        if (isActive && currentAI != null && playerIndex == 1) {
            AIMove nextMove = currentAI.getNextMove(game.getBoard(), playerIndex == 0 ? game.getPlayerNorth() : game.getPlayerSouth());
            game.getCurrentRequestProcessor().processBoardSelect(playerIndex, nextMove.orRow, nextMove.orCol);
            game.getCurrentRequestProcessor().processBoardSelect(playerIndex, nextMove.destRow, nextMove.destCol);
        }
    }

    public void tryNextMove(StrategoGame game, int playerIndex, String aiType) {

    }

    public void reset() {
        setAIMode("random");
    }

}
