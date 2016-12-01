package project.stratego.control.managers;

import project.stratego.ai.search.ExpectiNegamaxAI;
import project.stratego.ai.search.RandomAI;
import project.stratego.ai.searchGenerics.GenericAI;
import project.stratego.game.entities.GameState;
import project.stratego.game.utils.Move;

public class AIComManager {

    private static AIComManager instance;

    public static AIComManager getInstance() {
        if (instance == null) {
            instance = new AIComManager();
        }
        return instance;
    }

    private AIComManager() {
        setAIMode("expectinegamax", 1);
    }

    private GenericAI currentAI;
    private boolean isActive;

    public void configureMultiPlayer() {
        isActive = false;
    }

    public void configureSinglePlayer() {
        isActive = true;
    }

    public void setAIMode(String aiType, int playerIndex) {
        if (aiType.equals("random")) {
            currentAI = new RandomAI(playerIndex);
        } else if (aiType.equals("expectinegamax")) {
            currentAI = new ExpectiNegamaxAI(playerIndex);
        }
    }

    public void tryBoardSetup(GameState state) {
        if (isActive && currentAI != null) {
            currentAI.copyOpponentSetup(state);
            currentAI.makeBoardSetup(state);
        }
    }

    public void tryNextMove(Move move) {
        if (isActive && currentAI != null && move.getPlayerIndex() != currentAI.getPlayerIndex()) {
            Move nextMove = currentAI.getNextMove(move);
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (isActive && currentAI != null) {
            currentAI.applyMove(move);
        }
    }

    public void reset() {
        setAIMode("expectinegamax", 1);
    }

}
