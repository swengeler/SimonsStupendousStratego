package project.stratego.ai.search;

import project.stratego.ai.EnhancedGameState;
import project.stratego.ai.searchGenerics.*;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.utils.*;

import java.util.ArrayList;

public class RandomAI extends GenericAI {

    public RandomAI(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        //System.out.println("CHECK 249z6");
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        int randIndex = (int) (Math.random() * legalMoves.size());
        return legalMoves.get(randIndex);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState state) {
        ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
        gameState.copySetup(state, playerIndex);
    }

    @Override
    public void copyOpponentSetup(GameState state) {
        gameState.copySetup(state, 1 - playerIndex);
    }

}
