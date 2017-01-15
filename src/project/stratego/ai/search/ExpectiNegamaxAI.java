package project.stratego.ai.search;

import project.stratego.ai.evaluation.AbstractEvaluationFunction;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.ai.setup.*;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.entities.*;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ExpectiNegamaxAI extends AbstractAI {

    private static String debugString = "";

    private static final boolean DEBUG = false;

    private int nodeCounter = 0;

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
        gameState.checkDebugCondition(0);
        gameState.checkDebugDepthThreeCondition(0);
        Move nextMove = expectiNegamaxSearch();
        gameState.checkDebugDepthThreeCondition(1);
        return nextMove;
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
        inGameState.copySetup(gameState, playerIndex);
    }

    public void applyMove(Move move) {
        super.applyMove(move);
        gameState.checkDebugCondition(1);
        gameState.checkDebugDepthThreeCondition(2);
        //System.out.println("In expectimax AI:");
        //gameState.printBoard();
    }

    private Move expectiNegamaxSearch() {
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

        //addToDebugString(0, -1, gameState);
        int currentNodeCounter = nodeCounter;

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            before = System.currentTimeMillis();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(1, gameState, m, currentNodeCounter);
            } else {
                gameState.applyMove(m);
                gameState.checkDebugDepthThreeCondition(3);
                currentValue = negamaxSearch(1, gameState, currentNodeCounter);
                gameState.checkDebugDepthThreeCondition(4);
                gameState.undoLastMove();
                gameState.checkDebugDepthThreeCondition(5);
            }
            if (DEBUG) {
                System.out.println("\n" + m);
                System.out.println("Value: " + currentValue + " in " + ((System.currentTimeMillis() - before) / 1000.0) + "s");
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
        nodeCounter++;
        //addToDebugString(currentDepth, parentID, gameState);
        int currentNodeCounter = nodeCounter;
        if (currentDepth == maxDepth || state.isGameOver()) {
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;
            return multiplier * evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        /*System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
        for (Move m : legalMoves) {
            System.out.println(m);
        }*/

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, state, m, currentNodeCounter);
            } else {
                state.applyMove(m);
                state.checkDebugDepthThreeCondition(6);
                currentValue = -negamaxSearch(currentDepth + 1, state, currentNodeCounter);
                state.checkDebugDepthThreeCondition(7);
                state.undoLastMove();
                state.checkDebugDepthThreeCondition(8);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, EnhancedGameState state, AIMove chanceMove, int parentID) {
        double sum = 0;
        double prevProbability;
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        //state.printProbabilitiesTable();
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            //System.out.println("Probability for piece at (" + unknownPiece.getRowPos() + "|" + unknownPiece.getColPos() + ") to be " + PieceType.values()[i] + ": " + state.getProbability(unknownPiece, i));
            if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                state.assignPieceType(unknownPiece, PieceType.values()[i]);
                state.applyMove(chanceMove);
                state.checkDebugDepthThreeCondition(9);
                sum += prevProbability * negamaxSearch(currentDepth, state, parentID);
                state.checkDebugDepthThreeCondition(10);
                state.undoLastMove();
                state.checkDebugDepthThreeCondition(11);
                state.undoLastAssignment();
            }
        }
        return sum;
    }

    /* stats */

    public int getNodesSearched() {
        return nodeCounter;
    }

    private void addToDebugString(int depth, int parentID, EnhancedGameState gameState) {
        debugString += "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\n";
        debugString += "NODE (ID: " + nodeCounter + ", parent ID: " + parentID + ", depth: " + depth + "):\r\n";
        debugString += "ACTUAL BOARD:\r\n" + gameState.boardToReadableString();
        debugString += "\r\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\nBOARD ACCORDING TO PROBABILITIES:\r\n" + gameState.boardAssignmentsToReadableString();
        debugString += "\r\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\nPROBABILITIES TABLE:\r\n" + gameState.probabilitiesTableToReadableString();
        debugString += "\r\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\r\n\r\n";
    }

    private void debugStringNextMove() {
        debugString += "--\r\n--\r\n--\r\n--\r\n--\r\n--\r\n--\r\n";
    }

    public static void printCurrentDebugString() {
        try (PrintWriter out = new PrintWriter("E:\\Simon\\Documents\\Google Drive\\University Material\\Project 2.1\\SimonsStupendousStratego\\res\\test_results\\debug2.txt")) {
            out.print(debugString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetDebugString() {
        debugString = "";
    }

}
