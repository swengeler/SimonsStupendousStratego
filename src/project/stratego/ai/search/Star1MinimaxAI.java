package project.stratego.ai.search;

import project.stratego.ai.setup.SetupMaker;
import project.stratego.ai.utils.AIMove;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class Star1MinimaxAI extends AbstractAI {

    private static final boolean DEBUG = true;

    private int nodeCounter = 0;

    private int maxDepth = 2;

    private double evalUpperBound = 1.1;
    private double evalLowerBound = 0.9;

    public Star1MinimaxAI(int playerIndex, int maxDepth) {
        super(playerIndex);
        this.maxDepth = maxDepth;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        return star1MinimaxSearch();
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

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void applyMove(Move move) {
        super.applyMove(move);
    }

    private Move star1MinimaxSearch() {
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

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            before = System.currentTimeMillis();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = star1Minimax(0, gameState, m, -Double.MAX_VALUE, Double.MAX_VALUE);
            } else {
                gameState.applyMove(m);
                currentValue = alphaBetaMin(1, gameState, -Double.MAX_VALUE, Double.MAX_VALUE);
                gameState.undoLastMove();
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

    private double alphaBetaMax(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            return evaluationFunction.evaluate(state, playerIndex);
        }

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);

        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                alphaValue = Math.max(alphaValue, (currentValue = star1Minimax(currentDepth, state, m, alphaValue, betaValue)));
            } else {
                state.applyMove(m);
                alphaValue = Math.max(alphaValue, (currentValue = alphaBetaMin(currentDepth + 1, state, alphaValue, betaValue)));
                state.undoLastMove();
            }
            if (alphaValue >= betaValue) {
                return alphaValue;
            }
        }
        return alphaValue;
    }

    private double alphaBetaMin(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            return evaluationFunction.evaluate(state, playerIndex);
        }

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                betaValue = Math.min(betaValue, (currentValue = star1Minimax(currentDepth, state, m, alphaValue, betaValue)));
            } else {
                state.applyMove(m);
                betaValue = Math.min(betaValue, (currentValue = alphaBetaMax(currentDepth + 1, state, alphaValue, betaValue)));
                state.undoLastMove();
            }
            if (betaValue <= alphaValue) {
                return betaValue;
            }
        }
        return betaValue;
    }

    private double star1Minimax(int currentDepth, EnhancedGameState state, AIMove chanceMove, double alphaValue, double betaValue) {
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        int[] relevantIndeces = new int[PieceType.numberTypes];
        int nrChanceEvents = 0;
        double[] relevantProbabilities = new double[relevantIndeces.length];
        double relevantProbabilitiesSum = 0.0;
        double tempProbability;
        for (int i = 0; i < PieceType.numberTypes; i++) {
            if ((i > 1 || unknownPiece.getType().ordinal() == playerIndex) && (tempProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantIndeces[nrChanceEvents] = i;
                relevantProbabilities[nrChanceEvents] = tempProbability;
                relevantProbabilitiesSum += tempProbability;
                nrChanceEvents++;
            }
        }

        for (int i = 0; i < nrChanceEvents; i++) {
            relevantProbabilities[i] /= relevantProbabilitiesSum;
        }

        double probValueSum = 0.0; // X(1)
        double probDifference = 1.0; // Y(0)
        double lowerBound; // A
        double upperBound; // B
        double nextLowerBound; // AX
        double nextUpperBound; // BX

        double weightedValueSum = 0.0;
        double currentValue;

        for (int i = 0; i < nrChanceEvents; i++) {
            // update Y values used to update the bounds below
            probDifference -= relevantProbabilities[i];

            // update lower bound and upper bound (A and B)
            lowerBound = (alphaValue - probValueSum - evalUpperBound * probDifference) / relevantProbabilities[i];
            upperBound = (betaValue - probValueSum - evalLowerBound * probDifference) / relevantProbabilities[i];

            // "trim" bounding values depending on whether the computed bounds are outside of the bonds of the evaluation function
            nextLowerBound = Math.max(lowerBound, evalLowerBound);
            nextUpperBound = Math.min(upperBound, evalUpperBound);

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            // depending on the depth (or the active player) the next value will be computed as the value of the resulting MAX node or that of the resulting MIN node
            currentValue = currentDepth % 2 == 1 ? alphaBetaMax(currentDepth + 1, state, nextLowerBound, nextUpperBound) : alphaBetaMin(currentDepth + 1, state, nextLowerBound, nextUpperBound);
            state.undoLastMove();
            state.undoLastAssignment();

            if (currentValue <= lowerBound) {
                return alphaValue;
            }
            if (currentValue >= upperBound) {
                return betaValue;
            }

            // update weighted sum which would only be used if all children are explored, then it's simply an expectimax evaluation
            weightedValueSum += relevantProbabilities[i] * currentValue;
            // update X and Y values used to update the bounds at the start of the loop
            probValueSum += relevantProbabilities[i] * currentValue;
        }
        return weightedValueSum;
    }

    /* stats */

    public int getNodesSearched() {
        return nodeCounter;
    }

}

