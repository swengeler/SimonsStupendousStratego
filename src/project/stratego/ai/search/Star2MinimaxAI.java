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

public class Star2MinimaxAI extends AbstractAI {

    private static final boolean DEBUG = true;

    private int nodeCounter = 0;

    private int maxDepth = 2;

    private double evalUpperBound = 2.0;
    private double evalLowerBound = 0.0;

    public Star2MinimaxAI(int playerIndex, int maxDepth) {
        super(playerIndex);
        this.maxDepth = maxDepth;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        return star2MinimaxSearch();
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

    private Move star2MinimaxSearch() {
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

        ArrayList<AIMove> testList = new ArrayList<>();
        testList.add(legalMoves.get(0));

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            before = System.currentTimeMillis();
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = star2Min(0, gameState, m, -Double.MAX_VALUE, Double.MAX_VALUE);
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
            return evaluationFunction.evaluate(state, playerIndex);
        }

        // generate moves for MAX player
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, playerIndex);
        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                alphaValue = Math.max(alphaValue, (currentValue = star2Min(currentDepth, state, m, alphaValue, betaValue)));
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
            return evaluationFunction.evaluate(state, playerIndex);
        }

        // generate moves for MIN player
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, 1 - playerIndex);
        double currentValue;
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                betaValue = Math.min(betaValue, (currentValue = star2Max(currentDepth, state, m, alphaValue, betaValue)));
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

    private double star2Max(int currentDepth, EnhancedGameState state, AIMove chanceMove, double alphaValue, double betaValue) {
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // determine actual successors (i.e. probability for unknownPiece to be a rank i must be over the threshold
        int[] relevantIndeces = new int[PieceType.values().length - 1];
        int nrChanceEvents = 0;
        double[] relevantProbabilities = new double[relevantIndeces.length];
        double relevantProbabilitiesSum = 0.0;
        double tempProbability;
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if ((i > 1) && (tempProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantIndeces[nrChanceEvents] = i;
                relevantProbabilities[nrChanceEvents] = tempProbability;
                relevantProbabilitiesSum += tempProbability;
                nrChanceEvents++;
            }
        }

        // normalising the sum of probabilities since the threshold is large enough that the probabilities may not add up to 1
        for (int i = 0; i < nrChanceEvents; i++) {
            relevantProbabilities[i] /= relevantProbabilitiesSum;
        }

        // variables to be used for the computations (with their respective names in pseudo-code in the report/research papers
        double probValueSum = 0.0; // X
        double probDifference = 1.0; // Y
        double probEstimateSum = 0.0; // Z
        double lowerBound = (alphaValue - (probDifference - relevantProbabilities[0]) * evalUpperBound) / relevantProbabilities[0]; // A = (alpha - U) / P(i)
        double upperBound; // B
        double nextLowerBound = Math.max(lowerBound, evalLowerBound); // AX = max(A, L)
        double nextUpperBound; // BX

        // probe children of the chance nodes, the probing method only computes the value of one of the "grandchildren" but move ordering
        // can ensure that it will be a useful one tighten the bounds
        double[] probedValues = new double[nrChanceEvents]; // W
        for (int i = 0; i < nrChanceEvents; i++) {
            probDifference -= relevantProbabilities[i];
            // only the upper bound B is updated because the next "layer" of nodes are MAX nodes -> can only tighten the upper bound
            upperBound = (betaValue - probEstimateSum - evalUpperBound * probDifference) / relevantProbabilities[i]; // (beta - X(i) - U * Y(i)) / P(i)
            nextUpperBound = Math.min(upperBound, evalUpperBound); // BX = min(B, U)

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            probedValues[i] = probe(currentDepth, state, nextLowerBound, nextUpperBound); // W(i) = PROBE(S(i), AX, BX)
            state.undoLastMove();
            state.undoLastAssignment();

            if (probedValues[i] >= upperBound) {
                return betaValue;
            }

            probEstimateSum += relevantProbabilities[i] * probedValues[i];
        }

        // resetting the Y which is only used for the lower bound (A) in the second loop
        probDifference = 1.0;

        double weightedValueSum = 0.0;
        double currentValue;

        for (int i = 0; i < nrChanceEvents; i++) {
            // update Y used to update the bounds below
            probDifference -= relevantProbabilities[i]; // Y(i + 1) = Y(i) - P(i), here with special value Y(0) = 1.0
            // the following basically takes out the estimated values which are replaced at the end of the loop with the real ones
            probEstimateSum -= relevantProbabilities[i] * probedValues[i]; // Z(i) = Z(i - 1) + W(i) * P(i), here with special value Z(0) = W(1) * P(1) + W(2) * P(2) + ... + W(n) * P(n)

            // update lower bound and upper bound (A and B)
            lowerBound = (alphaValue - probValueSum - evalUpperBound * probDifference) / relevantProbabilities[i]; // A = (alpha - X(i) - U * Y(i)) / P(i)
            upperBound = (betaValue - probValueSum - probEstimateSum) / relevantProbabilities[i]; // B = (beta - X(i) - Z(i)) / P(i)

            // "trim" bounding values depending on whether the computed bounds are outside of the bonds of the evaluation function
            nextLowerBound = Math.max(lowerBound, evalLowerBound); // AX = max(A, L)
            nextUpperBound = Math.min(upperBound, evalUpperBound); // BX = min(B, U)

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            currentValue = alphaBetaMax(currentDepth + 1, state, nextLowerBound, nextUpperBound); // value = MAX(s(i), AX, BX)
            state.undoLastMove();
            state.undoLastAssignment();

            if (currentValue <= lowerBound) { // value <= A
                return alphaValue;
            }
            if (currentValue >= upperBound) { // value >= B
                return betaValue;
            }

            // update weighted sum which would only be used if all children are explored, then it's simply an expectimax evaluation
            weightedValueSum += relevantProbabilities[i] * currentValue;
            // update X used to update the bounds at the start of the loop (same thing, different use)
            probValueSum += relevantProbabilities[i] * currentValue; // X(i + 1) = X(i) + P(i) * V(i)
        }
        return weightedValueSum;
    }

    private double star2Min(int currentDepth, EnhancedGameState state, AIMove chanceMove, double alphaValue, double betaValue) {
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // determine actual successors (i.e. probability for unknownPiece to be a rank i must be over the threshold
        int[] relevantIndeces = new int[PieceType.values().length - 1];
        int nrChanceEvents = 0;
        double[] relevantProbabilities = new double[relevantIndeces.length];
        double relevantProbabilitiesSum = 0.0;
        double tempProbability;
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if ((tempProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantIndeces[nrChanceEvents] = i;
                relevantProbabilities[nrChanceEvents] = tempProbability;
                relevantProbabilitiesSum += tempProbability;
                nrChanceEvents++;
            }
        }

        // normalising the sum of probabilities since the threshold is large enough that the probabilities may not add up to 1
        for (int i = 0; i < nrChanceEvents; i++) {
            relevantProbabilities[i] /= relevantProbabilitiesSum;
        }

        // variables to be used for the computations (with their respective names in pseudo-code in the report/research papers
        double probValueSum = 0.0; // X
        double probDifference = 1.0; // Y
        double probEstimateSum = 0.0; // Z
        double lowerBound; // A
        double upperBound = (betaValue - probDifference * evalLowerBound) / relevantProbabilities[0]; // B
        double nextLowerBound; // AX
        double nextUpperBound = Math.min(upperBound, evalUpperBound); // BX

        // probe children of the chance nodes, the probing method only computes the value of one of the "grandchildren" but move ordering
        // can ensure that it will be a useful one tighten the bounds
        double[] probedValues = new double[nrChanceEvents]; // W
        for (int i = 0; i < nrChanceEvents; i++) {
            probDifference -= relevantProbabilities[i];
            // only the lower bound A is updated because the next "layer" of nodes are MIN nodes -> can only tighten the lower bound
            lowerBound = (alphaValue - probEstimateSum - evalUpperBound * probDifference) / relevantProbabilities[i];
            nextLowerBound = Math.max(lowerBound, evalLowerBound);

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            probedValues[i] = probe(currentDepth + 1, state, nextLowerBound, nextUpperBound);
            state.undoLastMove();
            state.undoLastAssignment();

            if (probedValues[i] <= lowerBound) {
                return alphaValue;
            }

            probEstimateSum += relevantProbabilities[i] * probedValues[i];
        }

        double weightedValueSum = 0.0;
        double currentValue;

        for (int i = 0; i < nrChanceEvents; i++) {
            // update Y used to update the bounds below
            probDifference -= relevantProbabilities[i];
            probEstimateSum -= relevantProbabilities[i] * probedValues[i];

            // update lower bound and upper bound (A and B)
            lowerBound = (alphaValue - probValueSum - probEstimateSum) / relevantProbabilities[i];
            upperBound = (betaValue - probValueSum - evalLowerBound * probDifference) / relevantProbabilities[i];

            // "trim" bounding values depending on whether the computed bounds are outside of the bonds of the evaluation function
            nextLowerBound = Math.max(lowerBound, evalLowerBound);
            nextUpperBound = Math.min(upperBound, evalUpperBound);

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            currentValue = alphaBetaMin(currentDepth + 1, state, nextLowerBound, nextUpperBound);
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
            // update X used to update the bounds at the start of the loop
            probValueSum += relevantProbabilities[i] * currentValue;
        }
        return weightedValueSum;
    }

    private double probe(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            return evaluationFunction.evaluate(state, playerIndex);
        }

        // generate moves (children) for MAX if currentDepth is even, for MIN if currentDepth is odd
        // order by how promising they are
        boolean maxPlayersTurn = currentDepth % 2 == 0;
        AIMove probedMove = orderMoves(generateLegalMoves(state, maxPlayersTurn ? playerIndex : 1 - playerIndex)).get(0);
        // get best one's value
        double probedValue;
        // now need to get the value of the opposite player's node which is the probed child
        if (probedMove.isChanceMove()) {
            probedValue = maxPlayersTurn ? star2Min(currentDepth, state, probedMove, alphaValue, betaValue) : star2Max(currentDepth, state, probedMove, alphaValue, betaValue);
        } else {
            state.applyMove(probedMove);
            probedValue = maxPlayersTurn ? alphaBetaMin(currentDepth + 1, state, alphaValue, betaValue) : alphaBetaMax(currentDepth + 1, state, alphaValue, betaValue);
            state.undoLastMove();
        }
        return probedValue;
    }

    /* stats */

    public int getNodesSearched() {
        return nodeCounter;
    }

}

