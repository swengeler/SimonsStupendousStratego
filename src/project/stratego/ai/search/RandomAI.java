package project.stratego.ai.search;

import javafx.application.Platform;
import project.stratego.ai.utils.AIMove;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.moves.Move;

import java.util.ArrayList;

public class RandomAI extends AbstractAI {

    public RandomAI(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        int randIndex = (int) (Math.random() * legalMoves.size());
        return legalMoves.get(randIndex);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        Platform.runLater(() -> ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex));
        gameState.copySetup(inGameState, playerIndex);
    }

}
