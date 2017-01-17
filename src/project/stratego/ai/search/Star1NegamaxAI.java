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

public class Star1NegamaxAI extends AbstractAI {

    private static final boolean DEBUG = true;

    private int nodeCounter = 0;

    private int maxDepth = 2;

    private double evalUpperBound = 1.5;
    private double evalLowerBound = 0.5;

    public Star1NegamaxAI(int playerIndex, int maxDepth) {
        super(playerIndex);
        this.maxDepth = maxDepth;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        return star1NegamaxSearch();
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

    private Move star1NegamaxSearch() {
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
                currentValue = star1Search(1, gameState, m, -evalUpperBound, -evalLowerBound);
            } else {
                gameState.applyMove(m);
                currentValue = alphaBetaNegamaxSearch(1, gameState, -evalUpperBound, -evalLowerBound);
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

    private double alphaBetaNegamaxSearch(int currentDepth, EnhancedGameState state, double alphaValue, double betaValue) {
        if (currentDepth == maxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;
            return multiplier * evaluationFunction.evaluate(state, playerIndex);
        }

        double currentValue;

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (m.isChanceMove()) {
                // do expectimax evaluation
                alphaValue = Math.max(alphaValue, (currentValue = star1Search(currentDepth + 1, state, m, -betaValue, -alphaValue)));
            } else {
                state.applyMove(m);
                alphaValue = Math.max(alphaValue, (currentValue = -alphaBetaNegamaxSearch(currentDepth + 1, state, -betaValue, -alphaValue)));
                state.undoLastMove();
            }
            if (alphaValue >= betaValue) {
                return alphaValue;
            }
        }

        return alphaValue;
    }

    private double star1Search(int currentDepth, EnhancedGameState state, AIMove chanceMove, double alphaValue, double betaValue) {
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        int[] relevantIndeces = new int[PieceType.values().length - 1];
        double[] relevantProbabilities = new double[PieceType.values().length - 1];
        double relevantProbabilitiesSum = 0.0;
        int nrChanceEvents = 0;
        double prevProbability;
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if ((prevProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantIndeces[nrChanceEvents] = i;
                relevantProbabilities[nrChanceEvents] = prevProbability;
                nrChanceEvents++;
            }
        }

        for (int i = 0; i < nrChanceEvents; i++) {
            relevantProbabilities[i] /= relevantProbabilitiesSum;
        }

        double probabilitiesDifference = 1.0; // Y
        double valueSum = 0.0; // X
        double lowerBound = (alphaValue - evalUpperBound * probabilitiesDifference - valueSum) / relevantProbabilities[0]; // A
        double upperBound = (betaValue - evalLowerBound * probabilitiesDifference - valueSum) / relevantProbabilities[0]; // B
        double nextLowerBound; // AX
        double nextUpperBound; // BX


        double sum = 0;
        double currentValue;

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove

        int counter = 0;
        for (int i = 0; i < nrChanceEvents; i++) {
            nextLowerBound = Math.max(lowerBound, evalLowerBound);
            nextUpperBound = Math.min(upperBound, evalUpperBound);

            relevantProbabilitiesSum += relevantProbabilities[i];

            state.assignPieceType(unknownPiece, PieceType.values()[relevantIndeces[i]]);
            state.applyMove(chanceMove);
            currentValue = -alphaBetaNegamaxSearch(currentDepth, state, -nextUpperBound, -nextLowerBound);
            sum += relevantProbabilities[i] * currentValue;
            state.undoLastMove();
            state.undoLastAssignment();

            if (currentValue <= lowerBound) {
                // sum += upperBound * probabilitiesDifference;
                return alphaValue; // not sure if this is enough
            }
            if (currentValue >= upperBound) {
                // sum += evalLowerBound * (nrChanceEvents - 1);
                return betaValue; // not sure if this is enough
            }

            if (i + 1 < relevantProbabilities.length) {
                probabilitiesDifference -= relevantProbabilities[i + 1];
                valueSum += relevantProbabilities[i + 1] * currentValue;
                lowerBound = (alphaValue - evalUpperBound * probabilitiesDifference - valueSum) / relevantProbabilities[i];
                upperBound = (betaValue - evalLowerBound * probabilitiesDifference - valueSum) / relevantProbabilities[i];
            }
        }
        return sum;
    }

    /* stats */

    public int getNodesSearched() {
        return nodeCounter;
    }

}

