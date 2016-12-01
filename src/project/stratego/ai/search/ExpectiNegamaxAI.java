package project.stratego.ai.search;

import project.stratego.ai.EnhancedGameState;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.ai.searchGenerics.*;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.utils.Move;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ExpectiNegamaxAI extends GenericAI {

    private EvaluationFunction evaluationFunction;
    private int maxDepth = 2;

    public ExpectiNegamaxAI(int playerIndex) {
        super(playerIndex);
        // perhaps select with one more constructor parameter or make generic AIInterface method setEvaluationFunction();
        evaluationFunction = new TestEvaluationFunction();
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        //System.out.println("CHECK 3");
        gameState.applyMove(lastOpponentMove);
        //System.out.println("CHECK 1");
        return expectiNegamaxSearch();
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState state) {
        SetupMaker setupMaker = new SetupMaker() {
            @Override
            public void makeBoardSetup(GameState state) {
                // something something
            }
        };
        ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
        gameState.copySetup(state, playerIndex);
    }

    @Override
    public void copyOpponentSetup(GameState state) {
        gameState.copySetup(state, 1 - playerIndex);
    }

    private Move expectiNegamaxSearch() {
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        System.out.println("Number of legal moves: " + legalMoves.size());/*
        for (AIMove m : legalMoves) {
            System.out.println("Move FROM: (" + m.getOrRow() + "|" + m.getOrCol() + ") TO (" + m.getDestRow() + "|" + m.getDestCol() + ")");
        }*/
        AIMove bestMove = legalMoves.get(0);
        EnhancedGameState clone;
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        System.out.println("PRINTOUT ON LEVEL: 0");
        gameState.printBoard();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            clone = gameState.clone();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(1, clone, m);
            } else {
                clone.applyMove(m);
                currentValue = -negamaxSearch(1, clone);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        return bestMove;
    }

    private double negamaxSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == maxDepth) {
            System.out.println("Negamax evaluation at depth: " + currentDepth);
            return evaluationFunction.evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex); // return evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        // generate all moves
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
        EnhancedGameState clone;

        System.out.println("PRINTOUTS ON LEVEL: " + currentDepth);
        state.printBoard();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            clone = state.clone();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, clone, m);
            } else {
                clone.applyMove(m);
                currentValue = -negamaxSearch(currentDepth + 1, clone);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, EnhancedGameState state, AIMove chanceMove) {
        if (currentDepth == maxDepth) {
            System.out.println("Expectimax evaluation at depth: " + currentDepth);
            return evaluationFunction.evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex); // return evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex); ?
        }

        double sum = 0;
        double prevProbability;
        EnhancedGameState clone;
        Piece unknownPiece = state.getBoardArray()[currentDepth % 2 != 0 ? chanceMove.getDestRow() : chanceMove.getOrRow()][currentDepth % 2 != 0 ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        //System.out.println("EXPECTIMAX PRINTOUTS ON LEVEL: " + currentDepth);

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            //System.out.println("probability for " + PieceType.values()[i] + ": " + state.getProbability(unknownPiece, i) + " (" + unknownPiece.getRowPos() + "|" + unknownPiece.getColPos() + ")");
            if ((prevProbability = state.getProbability(unknownPiece, i)) != 0) {
                clone = state.clone();
                clone.assignPieceType(unknownPiece, PieceType.values()[i]);
                clone.applyMove(chanceMove);
                sum += prevProbability * -negamaxSearch(currentDepth, clone);
            }
        }
        return sum;
    }

}
