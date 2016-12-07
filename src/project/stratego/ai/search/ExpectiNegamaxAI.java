package project.stratego.ai.search;

import project.stratego.ai.evaluation.AbstractEvaluationFunction;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.ai.setup.*;
import project.stratego.ai.utils.AIMove;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.*;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ExpectiNegamaxAI extends AbstractAI {

    private AbstractEvaluationFunction evaluationFunction;
    private int maxDepth = 4;

    public ExpectiNegamaxAI(int playerIndex) {
        super(playerIndex);
        // perhaps select with one more constructor parameter or make generic AIInterface method setEvaluationFunction();
        evaluationFunction = new TestEvaluationFunction(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        return expectiNegamaxSearch();
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        SetupMaker setupMaker = new SetupMaker() {
            @Override
            public void makeBoardSetup(GameState state) {
                // something something
            }
        };
        ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
        gameState.copySetup(inGameState, playerIndex);
    }

    @Override
    public void copyOpponentSetup(GameState inGameState) {
        gameState.copySetup(inGameState, 1 - playerIndex);
    }

    private Move expectiNegamaxSearch() {
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        System.out.println("Number of legal moves: " + legalMoves.size());
        int sum = 0;
        for (AIMove m : legalMoves) {
            if (m.isChanceMove())
                sum++;
        }
        System.out.println("Number of chance moves: " + sum);

        System.out.println("PRINTOUT ON LEVEL: 0");
        gameState.printBoard();

        AIMove bestMove = legalMoves.get(0);
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(1, gameState, m);
            } else {
                gameState.applyMove(m);
                currentValue = negamaxSearch(1, gameState);
                gameState.undoLastMove();
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
            return evaluationFunction.evaluate(state);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        //System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, gameState, m);
            } else {
                gameState.applyMove(m);
                currentValue = -negamaxSearch(currentDepth + 1, gameState);
                gameState.undoLastMove();
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, EnhancedGameState state, AIMove chanceMove) {
        if (currentDepth == maxDepth) {
            double evalSum = 0;
            double prevProbability;
            Piece unknownPiece = state.getBoardArray()[currentDepth % 2 != 0 ? chanceMove.getDestRow() : chanceMove.getOrRow()][currentDepth % 2 != 0 ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

            for (int i = 0; i < PieceType.values().length - 1; i++) {
                if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                    gameState.assignPieceType(unknownPiece, PieceType.values()[i]);
                    gameState.applyMove(chanceMove);
                    evalSum += prevProbability * evaluationFunction.evaluate(state);
                    gameState.undoLastMove();
                    gameState.undoLastAssignment();
                }
            }
            return evalSum;
        }

        double sum = 0;
        double prevProbability;
        Piece unknownPiece = state.getBoardArray()[currentDepth % 2 != 0 ? chanceMove.getDestRow() : chanceMove.getOrRow()][currentDepth % 2 != 0 ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            //System.out.println("probability for " + PieceType.values()[i] + ": " + state.getProbability(unknownPiece, i) + " (" + unknownPiece.getRowPos() + "|" + unknownPiece.getColPos() + ")");
            if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                gameState.assignPieceType(unknownPiece, PieceType.values()[i]);
                gameState.applyMove(chanceMove);
                sum += prevProbability * -negamaxSearch(currentDepth + 1, gameState);
                gameState.undoLastMove();
                gameState.undoLastAssignment();
            }
        }
        return sum;
    }

}
