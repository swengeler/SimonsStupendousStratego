package project.stratego.control.managers;

import project.stratego.ai.*;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.GameState;

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
        } else if (aiType.equals("expectimax")) {
            currentAI = new ExpectiNegamaxAI();
        }
    }

    public void tryBoardSetup(GameState state, int playerIndex) {
        if (isActive && currentAI != null) {
            currentAI.makeBoardSetup(state, playerIndex);
        }
    }

    public void tryBoardSetup(StrategoGame game, int playerIndex, String aiType) {

    }

    public void tryNextMove(GameState state, int playerIndex) {
        System.out.println("TEST: " + state.getBoardArray()[4][1].getOccupyingPiece());
        if (isActive && currentAI != null && playerIndex == 1) {
            AIMove nextMove = currentAI.getNextMove(state, playerIndex);
            ModelComManager.getInstance().requestBoardTileSelected(-1, playerIndex, nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, playerIndex, nextMove.getDestRow(), nextMove.getDestCol());
        }
    }

    public void tryNextMove(StrategoGame game, int playerIndex, String aiType) {

    }

    public void reset() {
        setAIMode("random");
    }

}
