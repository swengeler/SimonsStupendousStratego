package project.stratego.ai.search;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.entities.*;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class ExpectiMinimaxAI extends AbstractAI {

    private static String debugString = "";

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_MIN = false;
    private static final boolean DEBUG_MAX = false;
    private static final boolean DEBUG_EXP = false;

    private int nodeCounter = 0;

    private int maxDepth = 4;

    public ExpectiMinimaxAI(int playerIndex, int maxDepth) {
        super(playerIndex);
        this.maxDepth = maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        Move nextMove = expectiMinimaxSearch();
        return nextMove;
    }

    private Move expectiMinimaxSearch() {
        if (DEBUG) {
            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.println("EXPECTIMAX search for " + (playerIndex == PlayerType.NORTH.ordinal() ? "NORTH:" : "SOUTH:"));
            System.out.println("------------------------------------------------------------------------------------\n");
        }
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        if (DEBUG) {
            System.out.println("Number of legal moves: " + legalMoves.size());
            int sum = 0;
            for (AIMove m : legalMoves) {
                System.out.println(m);

                if (m.isChanceMove())
                    sum++;
            }
            System.out.println("\nNumber of chance moves: " + sum + "\n");

            gameState.printBoard();
        }

        AIMove bestMove = legalMoves.get(0);
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        long before;
        long total = System.currentTimeMillis();

        //ArrayList<AIMove> testList = new ArrayList<>(3);
        //testList.add(legalMoves.get(2));
        //testList.add(legalMoves.get(1));
        //testList.add(legalMoves.get(2));

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            before = System.currentTimeMillis();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(1, gameState, m);
            } else {
                gameState.applyMove(m);
                currentValue = minSearch(1, gameState);
                gameState.undoLastMove();
            }
            if (DEBUG) {
                System.out.println(m);
                System.out.println("Value: " + currentValue + " in " + ((System.currentTimeMillis() - before) / 1000.0) + "s\n");
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        if (DEBUG) {
            System.out.println("------------------------------------------------------------------------------------\nBest move:");
            System.out.println(bestMove);
            System.out.println("Max value: " + maxValue);
            System.out.println("Searched " + nodeCounter + " nodes in " + ((System.currentTimeMillis() - total) / 1000.0) + "s.");
            System.out.println("------------------------------------------------------------------------------------\n");
        }

        //debugStringNextMove();

        return bestMove;
    }

    private double negamaxSearch(int currentDepth, EnhancedGameState state, int parentID) {
        //System.out.println("PRINTOUT ON LEVEL: 1");
        //gameState.printProbabilitiesTable();
        //gameState.printBoard();
        //addToDebugString(currentDepth, parentID, gameState);
        int currentNodeCounter = nodeCounter;
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;
            //int multiplier = 1;
            return multiplier * evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        if (DEBUG_MIN && (currentDepth == 1 || currentDepth == 2)) {
            System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
            for (Move m : legalMoves) {
                System.out.println(m);
            }
            //state.printBoard();
            //state.printProbabilitiesTable();
        }

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                //state.checkDebugDepthThreeCondition(6);
                currentValue = -negamaxSearch(currentDepth + 1, state, currentNodeCounter);
                //state.checkDebugDepthThreeCondition(7);
                state.undoLastMove();
                //state.checkDebugDepthThreeCondition(8);
            }
            if (DEBUG_MIN && (currentDepth == 1 || currentDepth == 2)) {
                System.out.println("value for " + m + ": " + currentValue);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double maxSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            return evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        if (DEBUG_MAX && (currentDepth == 1 || currentDepth == 2)) {
            System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
            for (Move m : legalMoves) {
                System.out.println(m);
            }
            //state.printBoard();
            //state.printProbabilitiesTable();
        }

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                currentValue = minSearch(currentDepth + 1, state);
                state.undoLastMove();
            }
            if (DEBUG_MAX && (currentDepth == 1 || currentDepth == 2)) {
                System.out.println("value for " + m + ": " + currentValue);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double minSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            return evaluationFunction.evaluate(state, playerIndex);
        }

        double minValue = Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        if (DEBUG_MIN && (currentDepth == 1 || currentDepth == 2)) {
            System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
            for (Move m : legalMoves) {
                System.out.println(m);
            }
            //state.printBoard();
            //state.printProbabilitiesTable();
        }

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                currentValue = maxSearch(currentDepth + 1, state);
                state.undoLastMove();
            }
            if (DEBUG_MIN && currentDepth == 1) {
                System.out.println("value for " + m + ": " + currentValue);
            }
            if (currentValue < minValue) {
                minValue = currentValue;
            }
        }

        return minValue;
    }

    private double expectimaxSearch(int currentDepth, EnhancedGameState state, AIMove chanceMove) {
        double sum = 0;
        double prevProbability;
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        double relevantProbabilitiesSum = 0.0;
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if (DEBUG_EXP && currentDepth == 2) {
                System.out.println("Probability for piece at (" + unknownPiece.getRowPos() + "|" + unknownPiece.getColPos() + ") to be " + PieceType.values()[i] + ": " + state.getProbability(unknownPiece, i));
            }
            if ((currentDepth % 2 == 1 || i > 1) && (prevProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantProbabilitiesSum += prevProbability;
                state.assignPieceType(unknownPiece, PieceType.values()[i]);
                state.applyMove(chanceMove);
                if (DEBUG_EXP && currentDepth == 1) {
                    state.printBoard();
                }
                if (currentDepth % 2 == 1) {
                    sum += prevProbability * minSearch(currentDepth, state);
                } else {
                    sum += prevProbability * maxSearch(currentDepth, state);
                }
                state.undoLastMove();
                state.undoLastAssignment();
            }
        }
        if (DEBUG_EXP && currentDepth == 2) {
            System.out.println("sum = " + sum + ", relevantProbabilitiesSum = " + relevantProbabilitiesSum);
        }
        sum /= relevantProbabilitiesSum;
        if (DEBUG_EXP && currentDepth == 2) {
            System.out.println("sum after = " + sum);
        }
        return sum;
    }

    /* stats */

    public int getNodesSearched() {
        return nodeCounter;
    }

}
