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

    private int nodeCounter = 0;

    private AbstractEvaluationFunction evaluationFunction;
    private int maxDepth = 2;

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
            public void makeBoardSetup(GameState state, int playerIndex) {
                // something something
            }
        };
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        gameState.interpretAndCopySetup(example1);
        System.out.println("board setup in AI");
        inGameState.copySetup(gameState, playerIndex);
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
            System.out.println(m);

            if (m.isChanceMove())
                sum++;
        }
        System.out.println("Number of chance moves: " + sum);

        //System.out.println("PRINTOUT ON LEVEL: 0");
        //gameState.printProbabilitiesTable();
        gameState.printBoard();

        AIMove bestMove = legalMoves.get(0);
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        long before;
        long total = System.currentTimeMillis();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            before = System.currentTimeMillis();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(1, gameState, m);
            } else {
                gameState.applyMove(m);
                currentValue = negamaxSearch(1, gameState);
                gameState.undoLastMove();
            }
            System.out.println(m);
            System.out.println("Value: " + currentValue + " in " + ((System.currentTimeMillis() - before) / 1000.0) + "s\n");
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        System.out.println("------------------------------------------------------------------------------------\nBest move:");
        System.out.println(bestMove);
        System.out.println("Max value: " + maxValue);
        System.out.println("Searched " + nodeCounter + " nodes in " + ((System.currentTimeMillis() - total) / 1000.0) + "s.");
        System.out.println("------------------------------------------------------------------------------------");

        return bestMove;
    }

    private double negamaxSearch(int currentDepth, EnhancedGameState state) {
        //System.out.println("PRINTOUT ON LEVEL: 1");
        //gameState.printProbabilitiesTable();
        //gameState.printBoard();
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;
            return multiplier * evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        //System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, gameState, m);
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
        /*
        if (currentDepth == maxDepth) {
            nodeCounter++;
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;

            double evalSum = 0;
            double prevProbability;
            Piece unknownPiece = state.getBoardArray()[currentDepth % 2 != 0 ? chanceMove.getDestRow() : chanceMove.getOrRow()][currentDepth % 2 != 0 ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

            for (int i = 0; i < PieceType.values().length - 1; i++) {
                if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                    gameState.assignPieceType(unknownPiece, PieceType.values()[i]);
                    gameState.applyMove(chanceMove);
                    evalSum += multiplier * prevProbability * evaluationFunction.evaluate(state, playerIndex);
                    gameState.undoLastMove();
                    gameState.undoLastAssignment();
                }
            }
            return evalSum;
        }*/

        double sum = 0;
        double prevProbability;
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                gameState.assignPieceType(unknownPiece, PieceType.values()[i]);
                gameState.applyMove(chanceMove);
                sum += prevProbability * negamaxSearch(currentDepth, gameState);
                gameState.undoLastMove();
                gameState.undoLastAssignment();
            }
        }
        return sum;
    }

}
