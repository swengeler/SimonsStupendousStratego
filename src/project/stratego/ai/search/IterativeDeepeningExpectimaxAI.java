package project.stratego.ai.search;

import project.stratego.ai.utils.AIMove;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class IterativeDeepeningExpectimaxAI extends AbstractAI {

    private static final boolean DEBUG = true;

    private int nodeCounter = 0;

    private long timeLimitMillis;
    private long currentStartTimeMillis;

    private int currentMaxDepth;

    private boolean iterativeDeepening = false;
    private boolean timeLimitReached;

    public IterativeDeepeningExpectimaxAI(int playerIndex, long timeLimitMillis) {
        super(playerIndex);
        this.timeLimitMillis = timeLimitMillis;
    }

    public void setTimeLimit(long timeLimitMillis) {
        this.timeLimitMillis = timeLimitMillis;
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);
        currentStartTimeMillis = System.currentTimeMillis();
        currentMaxDepth = 2;
        timeLimitReached = false;
        return expectiNegamaxSearch();
    }

    private Move expectiNegamaxSearch() {
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        if (DEBUG) {
            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.println("EXPECTIMAX search for " + (playerIndex == PlayerType.NORTH.ordinal() ? "NORTH:" : "SOUTH:"));
            System.out.println("------------------------------------------------------------------------------------\n");

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
                        currentValue = expectimaxSearch(1, gameState, m);
                    } else {
                        gameState.applyMove(m);
                        currentValue = minSearch(1, gameState);
                        gameState.undoLastMove();
                    }
                    if (DEBUG) {
                        System.out.println("\n" + m);
                        System.out.println("Value: " + currentValue + " in " + ((System.currentTimeMillis() - before) / 1000.0) + "s (maxDepth: " + currentMaxDepth + ")");
                    }
                    if (currentValue > maxValue && !timeLimitReached) {
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
                    currentValue = expectimaxSearch(1, gameState, m);
                } else {
                    gameState.applyMove(m);
                    currentValue = minSearch(1, gameState);
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

        if (DEBUG) {
            System.out.println("------------------------------------------------------------------------------------\nBest move:");
            System.out.println(bestMove);
            System.out.println("Max value: " + maxValue);
            System.out.println("Searched " + nodeCounter + " nodes in " + ((System.currentTimeMillis() - total) / 1000.0) + "s.");
            System.out.println("------------------------------------------------------------------------------------\n");
        }

        return bestMove;
    }

    /*private double negamaxSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == currentMaxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            int multiplier = currentDepth % 2 == 0 ? -1 : 1;
            return multiplier * evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if ((System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
            }
            if (timeLimitReached) {
                return maxValue;
            }
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                currentValue = -negamaxSearch(currentDepth + 1, state);
                state.undoLastMove();
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, EnhancedGameState state, AIMove chanceMove) {
        double sum = 0;
        double prevProbability;
        Piece unknownPiece = state.getBoardArray()[chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestRow() : chanceMove.getOrRow()][chanceMove.getPlayerIndex() == playerIndex ? chanceMove.getDestCol() : chanceMove.getOrCol()].getOccupyingPiece();

        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        double relevantProbabilitiesSum = 0.0;
        for (int i = 0; i < PieceType.numberTypes; i++) {
            if ((System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
            }
            if (timeLimitReached) {
                return sum / relevantProbabilitiesSum;
            }
            if ((prevProbability = state.getProbability(unknownPiece, i)) > *//*0.2 * *//*EnhancedGameState.PROB_EPSILON) {
                relevantProbabilitiesSum += prevProbability;
                state.assignPieceType(unknownPiece, PieceType.values()[i]);
                state.applyMove(chanceMove);
                sum += prevProbability * negamaxSearch(currentDepth, state);
                state.undoLastMove();
                state.undoLastAssignment();
            }
        }
        sum /= relevantProbabilitiesSum;
        return sum;
    }*/

    private double maxSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == currentMaxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            return evaluationFunction.evaluate(state, playerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return maxValue;
            }
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                //gameState.checkBombDebugCondition(2);
                currentValue = minSearch(currentDepth + 1, state);
                state.undoLastMove();
            }
            if (currentValue > maxValue && (!iterativeDeepening || !timeLimitReached)) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double minSearch(int currentDepth, EnhancedGameState state) {
        if (currentDepth == currentMaxDepth || state.isGameOver()) {
            nodeCounter++;
            //System.out.println("Evaluation at depth: " + currentDepth + ", gameOver = " + state.isGameOver());
            return evaluationFunction.evaluate(state, playerIndex);
        }

        double minValue = Double.MAX_VALUE;
        double currentValue;
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? playerIndex : 1 - playerIndex);

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return minValue;
            }
            if (m.isChanceMove()) {
                // do expectimax evaluation
                currentValue = expectimaxSearch(currentDepth + 1, state, m);
            } else {
                state.applyMove(m);
                //gameState.checkBombDebugCondition(3);
                currentValue = maxSearch(currentDepth + 1, state);
                state.undoLastMove();
            }
            if (currentValue < minValue && (!iterativeDeepening || !timeLimitReached)) {
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
        for (int i = 0; i < PieceType.numberTypes; i++) {
            if (iterativeDeepening && (System.currentTimeMillis() - currentStartTimeMillis) >= timeLimitMillis) {
                timeLimitReached = true;
                return sum / relevantProbabilitiesSum;
            }
            if ((currentDepth % 2 == 1 || i > 1) && (prevProbability = state.getProbability(unknownPiece, i)) > /*0.2 * */EnhancedGameState.PROB_EPSILON) {
                relevantProbabilitiesSum += prevProbability;
                state.assignPieceType(unknownPiece, PieceType.values()[i]);
                state.applyMove(chanceMove);
                if (currentDepth % 2 == 1) {
                    sum += prevProbability * minSearch(currentDepth, state);
                } else {
                    sum += prevProbability * maxSearch(currentDepth, state);
                }
                state.undoLastMove();
                state.undoLastAssignment();
            }
        }
        sum /= relevantProbabilitiesSum;
        return sum;
    }

}
