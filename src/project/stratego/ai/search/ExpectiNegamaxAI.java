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

    private GenericEvaluationFunction evaluationFunction;
    private int maxDepth = 3;

    public ExpectiNegamaxAI(int playerIndex) {
        super(playerIndex);
        // perhaps select with one more constructor parameter or make generic AIInterface method setEvaluationFunction();
        evaluationFunction = new TestEvaluationFunction(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        //System.out.println("CHECK 3");
        gameState.applyMove(lastOpponentMove);
        //System.out.println("CHECK 1");
        return expectiNegamaxSearch();
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
        EnhancedGameState clone;
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            //clone = gameState.clone();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(1, gameState, m);
            } else {
                //clone.applyMove(m);
                //System.out.println("BEFORE MOVE:");
                //gameState.printBoard();
                gameState.applyMove(m);
                //System.out.println("AFTER MOVE:");
                //gameState.printBoard();
                currentValue = negamaxSearch(1, gameState);
                gameState.undoLastMove();
                //System.out.println("AFTER REVERSE:");
            }
            //System.out.println("Current value: " + currentValue);
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        return bestMove;
    }

    private double negamaxSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == maxDepth) {
            //System.out.println("Negamax evaluation at depth: " + currentDepth);
            return evaluationFunction.evaluate(state); // return evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        // generate all moves
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        //System.out.println("\nNumber of legal moves for this node at depth " + currentDepth + ": " + legalMoves.size());
        EnhancedGameState clone;

        //System.out.println("PRINTOUTS ON LEVEL: " + currentDepth);
        //state.printBoard();

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            //clone = state.clone();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                //System.out.println("\n\n");
                //System.out.println("----------------------------------------------------------------------------");
                currentValue = expectimaxSearch(currentDepth + 1, gameState, m);
                //System.out.println("----------------------------------------------------------------------------");
                //System.out.println("\n\n");
            } else {
                //clone.applyMove(m);
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
            //System.out.println("\nEXPECTIMAX EVALUATION");

            double evalSum = 0;
            double prevProbability;
            Piece unknownPiece = state.getBoardArray()[currentDepth % 2 != 0 ? chanceMove.getDestRow() : chanceMove.getOrRow()][currentDepth % 2 != 0 ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

            for (int i = 0; i < PieceType.values().length - 1; i++) {
                if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                    //System.out.println();
                    //System.out.println("BEFORE ASSIGNMENT:");
                    //gameState.printProbabilitiesTable();
                    gameState.assignPieceType(unknownPiece, PieceType.values()[i]);
                    //System.out.println("BEFORE MOVE:");
                    //gameState.printBoard();
                    gameState.applyMove(chanceMove);
                    evalSum += prevProbability * evaluationFunction.evaluate(state);
                    gameState.undoLastMove();
                    //System.out.println("BEFORE MOVE UNDO:");
                    //gameState.printBoard();
                    gameState.undoLastAssignment();
                    //System.out.println("AFTER ASSIGNMENT UNDO:");
                    //gameState.printProbabilitiesTable();
                    //System.out.println();
                }
            }
            return evalSum; // return evaluate(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex); ?
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
            if ((prevProbability = state.getProbability(unknownPiece, i)) > EnhancedGameState.PROB_EPSILON) {
                //clone = state.clone();
                //clone.assignPieceType(unknownPiece, PieceType.values()[i]);
                //clone.applyMove(chanceMove);
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
