package project.stratego.ai.search;

import project.stratego.ai.evaluation.AbstractEvaluationFunction;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;

import java.util.ArrayList;

public class AlphaBetaMinimax extends PerfectInformationAbstractAI {

    private AbstractEvaluationFunction evaluationFunction;

    private int maxDepth = 6;

    public AlphaBetaMinimax(int playerIndex) {
        super(playerIndex);
        evaluationFunction = new TestEvaluationFunction(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        return search();
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        //gameState.interpretAndCopySetup(example1);
        inGameState.copySetup(gameState, playerIndex);
    }

    private Move search() {
        ArrayList<Move> legalMoves = generateLegalMoves(gameState, playerIndex);

        Move bestMove = legalMoves.get(0);
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        long before;
        long total = System.currentTimeMillis();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (Move m : legalMoves) {
            before = System.currentTimeMillis();
            gameState.applyMove(m);
            currentValue = alphaBetaNegamaxSearch(1, gameState, -Double.MAX_VALUE, Double.MAX_VALUE);
            gameState.undoLastMove();
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        return bestMove;
    }

    private double alphaBetaNegamaxSearch(int currentDepth, GameState state, double alphaValue, double betaValue) {
        if (currentDepth == maxDepth) {
            // return evaluationFunction.evaluate();
        }

        ArrayList<Move> legalMoves = generateLegalMoves(state, playerIndex);

        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        long before;
        long total = System.currentTimeMillis();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (Move m : legalMoves) {
            before = System.currentTimeMillis();
            state.applyMove(m);
            alphaValue = Math.max(alphaValue, -alphaBetaNegamaxSearch(currentDepth + 1, state, -betaValue, -alphaValue));
            state.undoLastMove();
            if (alphaValue >= betaValue) {
                return alphaValue;
            }
        }
        return alphaValue;
    }
}
