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
    public void applyMove(Move move) {
        gameState.applyMove(move);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
        gameState.copySetup(inGameState, playerIndex);
    }

    @Override
    public void copyOpponentSetup(GameState inGameState) {
        gameState.copySetup(inGameState, 1 - playerIndex);
    }

}
