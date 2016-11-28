package project.stratego.ai;

import project.stratego.ai.searchGenerics.EvaluationFunction;
import project.stratego.ai.searchGenerics.TreeSearchNode;
import project.stratego.game.entities.*;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ExpectiNegamaxAI implements AIInterface {

    private EvaluationFunction evaluationFunction;

    private GameState initGameState;
    private int initPlayerIndex;
    private double[][][] probabilities; // every entry should be initialised to (#pieces of that type)/40

    private int maxDepth = 6;

    @Override
    public AIMove getNextMove(GameState state, int playerIndex) {
        return expectiNegamaxSearch(state, playerIndex);
    }

    @Override
    public void makeBoardSetup(GameState state, int playerIndex) {

    }

    private AIMove expectiNegamaxSearch(GameState state, int playerIndex) {
        initGameState = state;
        initPlayerIndex = playerIndex;
        updateProbabilities(state);

        ArrayList<AIMove> legalMoves = generateLegalMoves(state, initPlayerIndex);
        AIMove bestMove = legalMoves.get(0);
        GameState clone;
        double maxValue = -Double.MAX_VALUE;
        double currentValue;

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            clone = state.clone();
            if (m.isChanceMove) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(1, state.clone(), m);
            } else {
                clone.applyMove(m);
                currentValue = -negamaxSearch(1, clone);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
                bestMove = m;
            }
        }

        return bestMove;
    }

    private double negamaxSearch(int currentDepth, GameState state) {
        if (currentDepth == maxDepth) {
            return evaluationFunction.evaluate(state, currentDepth % 2 == 0 ? initPlayerIndex : 1 - initPlayerIndex); // return evaluate(state, currentDepth % 2 == 0 ? initPlayerIndex : 1 - initPlayerIndex);
        }

        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        // generate all moves
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, currentDepth % 2 == 0 ? initPlayerIndex : 1 - initPlayerIndex);
        GameState clone;

        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            clone = state.clone();
            if (m.isChanceMove) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, clone, m);
            } else {
                clone.applyMove(m);
                currentValue = -negamaxSearch(currentDepth + 1, clone);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }

        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, GameState state, AIMove chanceMove) {
        if (currentDepth == maxDepth) {
            return evaluationFunction.evaluate(state, currentDepth % 2 == 0 ? initPlayerIndex : 1 - initPlayerIndex); // return evaluate(state, currentDepth % 2 == 0 ? initPlayerIndex : 1 - initPlayerIndex); ?
        }

        double sum = 0;
        GameState clone;
        int unknownPieceRowPos = currentDepth % 2 == 0 ? chanceMove.destRow : chanceMove.orRow;
        int unknownPieceColPos = currentDepth % 2 == 0 ? chanceMove.destCol : chanceMove.orCol;

        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if (probabilities[unknownPieceRowPos][unknownPieceColPos][i] != 0) {
                clone = state.clone();
                clone.getBoardArray()[unknownPieceRowPos][unknownPieceColPos].getOccupyingPiece().setType(PieceType.values()[i]);
                clone.applyMove(chanceMove);
                sum += probabilities[unknownPieceRowPos][unknownPieceColPos][i] * -negamaxSearch(currentDepth, clone);
            }
        }
        // make clones of all possible assignments for either the piece that is moved or the piece that is attacked
        // take probability values from table/array that is stored and updated with each move made in the actual game (should probably adapt this later to be adjusted also for AI moves)
        // sum over all possible scenarios arising from chanceMove
        return sum;
    }

    private ArrayList<AIMove> generateLegalMoves(GameState state, int playerIndex) {
        ArrayList<AIMove> legalMoves = new ArrayList<>();
        boolean chanceEvent = false;
        int destRow, destCol;
        for (Piece p : state.getPlayer(playerIndex).getActivePieces()) {
            // check for unmovable pieces
            if (p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG) {
                int moveRadius = 1;
                // scouts can move more squares than the other pieces
                if (p.getType() == PieceType.SCOUT) {
                    moveRadius = 9;
                }
                // loop through the positions around the piece and check whether they can be moved there
                for (int row = -moveRadius; row <= moveRadius; row++) {
                    destRow = p.getRowPos() + row;
                    for (int col = -moveRadius; col <= moveRadius; col++) {
                        destCol = p.getColPos() + col;
                        // check whether the given piece can move to the target position
                        if (checkMovePossible(state, p, destRow, destCol)) {
                            // add legal move to list and also specify whether it will induce a chance event
                            if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null) {
                                // either different playerIndex from root (initPlayerIndex) AND piece to be moved is not revealed AND position to be moved to is taken by root player
                                chanceEvent = playerIndex != initPlayerIndex && !p.isRevealed(); // last check not necessary because of the if-statement checking for null; move would not be possible anyway if position was occupied by own piece
                                // OR same playerIndex as root (initPlayerIndex) AND position to be moved to is taken by opponent's unrevealed piece
                                chanceEvent = chanceEvent || (playerIndex == initPlayerIndex && !state.getBoardArray()[destRow][destCol].getOccupyingPiece().isRevealed());
                            }
                            legalMoves.add(new AIMove(p.getRowPos(), p.getColPos(), row, col, chanceEvent));
                            chanceEvent = false;
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    private boolean checkMovePossible(GameState state, Piece p, int destRow, int destCol) {
        // check if position is accessible
        if (!state.getBoardArray()[destRow][destCol].isAccessible()) {
            return false;
        }
        // check if position is on the board
        if (destRow < 0 || destRow >= 10 || destCol < 0 || destCol >= 10) {
            return false;
        }
        // check if position is already occupied by own piece
        if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null && state.getBoardArray()[destRow][destCol].getOccupyingPiece().getPlayerType() == p.getPlayerType()) {
            return false;
        }
        // check if path to position is blocked by other piece/inaccessible tile
        if (p.getType() == PieceType.SCOUT && checkScoutPath(state, p, destRow, destCol)) {
            return false;
        }
        return true;
    }

    private boolean checkScoutPath(GameState state, Piece scout, int destRow, int destCol) {
        // target position is in the same column as current position
        if (scout.getColPos() - destCol == 0) {
            for (int row = scout.getRowPos(); row != destRow; row += (destRow - scout.getRowPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[row][destCol].getOccupyingPiece() != null || !state.getBoardArray()[row][destCol].isAccessible()) {
                    return false;
                }
            }
        } else if (scout.getRowPos() - destRow == 0) {
            for (int col = scout.getColPos(); col != destRow; col += (destRow - scout.getColPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[destRow][col].getOccupyingPiece() != null || !state.getBoardArray()[destRow][col].isAccessible()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateProbabilities(GameState state) {
        if (probabilities == null) {
            probabilities = new double[GameState.BOARD_SIZE][GameState.BOARD_SIZE][PieceType.values().length - 1];
        }
    }

}
