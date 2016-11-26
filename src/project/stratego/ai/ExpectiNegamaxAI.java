package project.stratego.ai;

import project.stratego.ai.searchGenerics.TreeSearchNode;
import project.stratego.game.entities.*;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ExpectiNegamaxAI implements AIInterface {

    private GameState initGameState;
    private int initPlayerIndex;

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
        this.initPlayerIndex = playerIndex;

        return null;
    }

    private double negamaxSearch(int currentDepth, GameState state, int playerIndex) {
        if (currentDepth == maxDepth) {
            return 0; // return evaluate(state);
        }
        double maxValue = -Double.MAX_VALUE;
        double currentValue;
        // generate all moves
        ArrayList<AIMove> legalMoves = generateLegalMoves(state, playerIndex);
        // loop through all moves and find the one with the highest expecti-negamax value
        for (AIMove m : legalMoves) {
            // do move, more or less
            if (m.isChanceMove) {
                // do expectimax evaluation
                currentValue = -expectimaxSearch(currentDepth + 1, state.clone(), m);
            } else {
                currentValue = -negamaxSearch(currentDepth + 1, state.clone(), 1 - playerIndex);
            }
            if (currentValue > maxValue) {
                maxValue = currentValue;
            }
        }
        return maxValue;
    }

    private double expectimaxSearch(int currentDepth, GameState state, AIMove chanceMove) {
        if (currentDepth == maxDepth) {
            return 0; // return evaluate(state); ?
        }
        double sum = 0;

        // sum over all possible scenarios arising from chanceMove
        return sum;
    }

    private ArrayList<AIMove> generateLegalMoves(GameState state, int playerIndex) {
        ArrayList<AIMove> legalMoves = new ArrayList<>();
        boolean chanceEvent = false;
        // it is the AI player's turn -> moves for all its pieces should be generated
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
                    for (int col = -moveRadius; col <= moveRadius; col++) {
                        // check whether the given piece can move to the target position
                        if (checkMovePossible(state, p, p.getRowPos() + row, p.getColPos() + col)) {
                            // add legal move to list and also specify whether it will induce a chance event
                            if (state.getBoardArray()[p.getRowPos() + row][p.getColPos() + col].getOccupyingPiece() != null) {
                                chanceEvent = state.getBoardArray()[p.getRowPos() + row][p.getColPos() + col].getOccupyingPiece().getPlayerType() != p.getPlayerType(); // + one of them is not revealed
                                chanceEvent = chanceEvent && !(p.isRevealed() && state.getBoardArray()[p.getRowPos() + row][p.getColPos() + col].getOccupyingPiece().isRevealed());
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

    private ArrayList<TreeSearchNode> generateSuccessors(TreeSearchNode root) {
        if (root.getPlayerIndex() == initPlayerIndex) {
            // find all movable pieces
            // construct nodes with all possible resulting moves

        } else {
            // find movable "positions"/pieces of opponents
            // assign probabilities for each to be a certain piece
            // expand all possible moves for that piece
            // -> results in first a layer of chance nodes, then one of concrete states

        }
        return null;
    }

    private ArrayList<Piece> getMovablePieces(BoardTile[][] board, Player player) {
        ArrayList<Piece> movablePieces = new ArrayList<>();
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG &&
                ((p.getRowPos() - 1 >= 0 && (board[p.getRowPos() - 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() - 1][p.getColPos()].isAccessible()) ||
                (p.getRowPos() + 1 < 10 && (board[p.getRowPos() + 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() + 1][p.getColPos()].isAccessible()) ||
                (p.getColPos() - 1 >= 0 && (board[p.getRowPos()][p.getColPos() - 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() - 1].isAccessible()) ||
                (p.getColPos() + 1 < 10 && (board[p.getRowPos()][p.getColPos() + 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() + 1].isAccessible()))) {
                movablePieces.add(p);
            }
        }
        return movablePieces;
    }

    private ArrayList<TreeSearchNode> addSuccessorNodes(ArrayList<TreeSearchNode> successors, GameState state, Piece movablePiece) {
        // if an opponent's piece is not known probability node should be created
        return successors;
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

}
