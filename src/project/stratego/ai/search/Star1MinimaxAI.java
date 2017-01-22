package project.stratego.ai.search;

import project.stratego.ai.tests.AITestsMain;
import project.stratego.ai.utils.AIMove;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class Star1MinimaxAI extends AbstractAI {

    private static final boolean DEBUG = true;

    private int currentMaxDepth = 3;

    private double evalUpperBound = 2.0;
    private double evalLowerBound = 0.0;

    private long timeLimitMillis = 1000;
    private long currentStartTimeMillis;

    private boolean iterativeDeepening;
    private boolean timeLimitReached;

    private boolean moveOrdering;

    public Star1MinimaxAI(int playerIndex) {
        super(playerIndex);
    }

    public Star1MinimaxAI(int playerIndex, int maxDepth) {
        super(playerIndex);
        currentMaxDepth = maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.currentMaxDepth = maxDepth;
    }

    public void setTimeLimit(long timeLimitMillis) {
        this.timeLimitMillis = timeLimitMillis;
    }

    public void setIterativeDeepening(boolean iterativeDeepening) {
        this.iterativeDeepening = iterativeDeepening;
    }

    public void setOpponentModelling(boolean opponentModelling) {
        if (opponentModelling) {
            gameState.setOpponentModellingEnabled(true);
        } else {
            gameState.setOpponentModellingEnabled(false);
        }
    }

    public void setMoveOrdering(boolean moveOrdering) {
        this.moveOrdering = moveOrdering;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        if (iterativeDeepening) {
            currentStartTimeMillis = System.currentTimeMillis();
            currentMaxDepth = 1;
            timeLimitReached = false;
            bestMoveDepth = 1;
        }
        return star1MinimaxSearch();
    }

    private Move star1MinimaxSearch() {
        if (DEBUG) {
            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.println("STAR1 search for " + (playerIndex == PlayerType.NORTH.ordinal() ? "NORTH:" : "SOUTH:"));
            System.out.println("------------------------------------------------------------------------------------\n");
        }
        System.out.println("iterativeDeepening: " + iterativeDeepening);
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        if (moveOrdering) {
            legalMoves = orderMoves(gameState, legalMoves);
        }
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

        long testTotal = System.nanoTime();

        if (iterativeDeepening) {
            while (!timeLimitReached) {
                // loop through all moves and find the one with the highest expecti-negamax value
                for (AIMove m : legalMoves) {
                    if ((System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                        timeLimitReached = true;
                        break;
                    }
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
                        System.out.println("Value: " + currentValue + " in " + ((System.currentTimeMillis() - before) / 1000.0) + "s (currentMaxDepth: " + currentMaxDepth + ")");
                    }
                    if (currentValue > maxValue && !timeLimitReached) {
                        bestMoveDepth = currentMaxDepth;
                        maxValue = currentValue;
                        bestMove = m;
                    }
                }
                currentMaxDepth++;
            }
        } else {
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
        }

        AITestsMain.addMoveStatistics(playerIndex, leafNodeCounter, minMaxNodeCounter, chanceNodeCounter, (System.nanoTime() - testTotal), currentMaxDepth, bestMoveDepth);

        if (DEBUG) {
            System.out.println("------------------------------------------------------------------------------------\nBest move:");
            System.out.println(bestMove);
            System.out.println("Max value: " + maxValue);
            System.out.println("Searched " + leafNodeCounter + " leaf nodes in " + ((System.currentTimeMillis() - total) / 1000.0) + "s.");
            System.out.println("------------------------------------------------------------------------------------\n");
        }

        return bestMove;
    }

    private double alphaBetaMax(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == currentMaxDepth || state.isGameOver()) {
            leafNodeCounter++;
            return evaluationFunction.evaluate(state, playerIndex);
        }
        minMaxNodeCounter++;

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        if (moveOrdering) {
            legalMoves = orderMoves(state, legalMoves);
        }

        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return 0.0;
            }
            if (m.isChanceMove()) {
                alphaValue = Math.max(alphaValue, (currentValue = star1Minimax(currentDepth, state, m, alphaValue, betaValue)));
            } else {
                state.applyMove(m);
                alphaValue = Math.max(alphaValue, (currentValue = alphaBetaMin(currentDepth + 1, state, alphaValue, betaValue)));
                state.undoLastMove();
            }
            if (alphaValue >= betaValue && (!iterativeDeepening || !timeLimitReached)) {
                return alphaValue;
            }
        }
        return alphaValue;
    }

    private double alphaBetaMin(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == currentMaxDepth || state.isGameOver()) {
            leafNodeCounter++;
            return evaluationFunction.evaluate(state, playerIndex);
        }
        minMaxNodeCounter++;

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);
        if (moveOrdering) {
            legalMoves = orderMoves(state, legalMoves);
        }

        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return 0.0;
            }
            if (m.isChanceMove()) {
                betaValue = Math.min(betaValue, (currentValue = star1Minimax(currentDepth, state, m, alphaValue, betaValue)));
            } else {
                state.applyMove(m);
                betaValue = Math.min(betaValue, (currentValue = alphaBetaMax(currentDepth + 1, state, alphaValue, betaValue)));
                state.undoLastMove();
            }
            if (betaValue <= alphaValue && (!iterativeDeepening || !timeLimitReached)) {
                return betaValue;
            }
        }
        return betaValue;
    }

    private double star1Minimax(int currentDepth, EnhancedGameState state, AIMove chanceMove, double alphaValue, double betaValue) {
        chanceNodeCounter++;

        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        int[] relevantIndeces = new int[PieceType.numberTypes];
        int nrChanceEvents = 0;
        double[] relevantProbabilities = new double[relevantIndeces.length];
        double relevantProbabilitiesSum = 0.0;
        double tempProbability;
        for (int i = 0; i < PieceType.numberTypes; i++) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return 0.0;
            }

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
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return 0.0;
            }

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

    private int leafNodeCounter = 0;
    private int chanceNodeCounter = 0;
    private int minMaxNodeCounter = 0;

    private int bestMoveDepth;

    public int getLeafNodeCounter() {
        return leafNodeCounter;
    }

    public int getChanceNodeCounter() {
        return chanceNodeCounter;
    }

    public int getMinMaxNodeCounter() {
        return minMaxNodeCounter;
    }

}

