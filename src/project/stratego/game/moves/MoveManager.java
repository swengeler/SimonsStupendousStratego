package project.stratego.game.moves;

import project.stratego.game.entities.*;
import project.stratego.game.utils.PieceType;

/**
 * A class that operates on a specific instance of a board (array of board tiles). When a move
 * should be made on that board this class can process it and change the state of the board.
 */
public abstract class MoveManager {

    protected BoardTile[][] board;

    private MoveResult lastMoveResult;
    private Piece lastRemovedPiece;

    protected MoveManager(BoardTile[][] board) {
        this.board = board;
    }

    /**
     * A method that resolves the request to move a certain selected piece to a certain board position.
     * It also takes as parameters the players of the game according to their respective roles, in order
     * to possibly remove pieces from their available pieces.
     */
    public void processMove(Player movingPlayer, Player staticPlayer, Piece movingPiece, int destRow, int destCol) {
        lastRemovedPiece = null;
        lastMoveResult = MoveResult.NOMOVE;

        BoardTile destTile = board[destRow][destCol];
        // doesn't account for flag/bomb because you cannot select those
        if (!destTile.isAccessible()) {
            //System.out.println("Did not move because not accessible");
            return;
        }
        if (destTile.getOccupyingPiece() != null && destTile.getOccupyingPiece().getPlayerType() == movingPiece.getPlayerType()) {
            // also accounts for clicking the same tile again
            //System.out.println("Did not move because occupied by own piece");
            return;
        }
        if (!checkIfReachable(movingPiece, destRow, destCol)) {
            //System.out.println("Did not move because unreachable");
            return;
        }

        // at this point we know that the piece can move there
        lastMoveResult = MoveResult.MOVE;
        movingPiece.revealMove();
        board[movingPiece.getRowPos()][movingPiece.getColPos()].setOccupyingPiece(null);

        if (destTile.getOccupyingPiece() != null) {
            movingPiece.reveal();
            if (checkIfDraw(destTile.getOccupyingPiece(), movingPiece)) {
                lastMoveResult = MoveResult.ATTACKTIE;
                destTile.getOccupyingPiece().reveal();
                lastRemovedPiece = destTile.getOccupyingPiece();
                destTile.setOccupyingPiece(null);
                movingPlayer.getActivePieces().remove(movingPiece);
                staticPlayer.getActivePieces().remove(lastRemovedPiece);
                movingPlayer.getDeadPieces().add(movingPiece);
                staticPlayer.getDeadPieces().add(lastRemovedPiece);
            } else if (checkIfAttackWins(movingPiece, destTile.getOccupyingPiece())) {
                lastMoveResult = MoveResult.ATTACKWON;
                lastRemovedPiece = destTile.getOccupyingPiece();
                movingPiece.reveal();
                destTile.getOccupyingPiece().reveal();
                destTile.setOccupyingPiece(movingPiece);
                staticPlayer.getActivePieces().remove(lastRemovedPiece);
                staticPlayer.getDeadPieces().add(lastRemovedPiece);
            } else {
                lastMoveResult = MoveResult.ATTACKLOST;
                lastRemovedPiece = movingPiece;
                destTile.getOccupyingPiece().reveal();
                movingPlayer.getActivePieces().remove(movingPiece);
                movingPlayer.getDeadPieces().add(movingPiece);
            }
        } else {
            //System.out.println("(" + destRow + "|" + destCol + ") is set to occupied: " + movingPiece);
            destTile.setOccupyingPiece(movingPiece);
        }
        movingPlayer.setCurrentPiece(null);
    }

    /**
     * A method that gives information about the outcome of the last move request by returning
     * a MoveResult object that can be interpreted.
     */
    public MoveResult lastMoveResult() {
        return lastMoveResult;
    }

    private boolean checkIfReachable(Piece movingPiece, int destRow, int destCol) {
        int rowDiff = destRow - movingPiece.getRowPos();
        int colDiff = destCol - movingPiece.getColPos();
        if (rowDiff != 0 && colDiff != 0) {
            return false;
        }
        if (((rowDiff == 0 && Math.abs(colDiff) > 1) || (colDiff == 0 && Math.abs(rowDiff) > 1)) && movingPiece.getType() != PieceType.SCOUT) {
            return false;
        }
        if (movingPiece.getType() == PieceType.SCOUT) {
            int c;
            if (rowDiff == 0) {
                c = colDiff > 0 ? 1 : -1;
                for (int i = 1; i < Math.abs(colDiff); i++) {
                    if (board[destRow][movingPiece.getColPos() + i * c].getOccupyingPiece() != null || !board[destRow][movingPiece.getColPos() + i * c].isAccessible()) {
                        return false;
                    }
                }
            } else {
                c = rowDiff > 0 ? 1 : -1;
                for (int i = 1; i < Math.abs(rowDiff); i++) {
                    if (board[movingPiece.getRowPos() + i * c][destCol].getOccupyingPiece() != null || !board[movingPiece.getRowPos() + i * c][destCol].isAccessible()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected abstract boolean checkIfAttackWins(Piece attackingPiece, Piece defendingPiece);

    protected abstract boolean checkIfDraw(Piece attackingPiece, Piece defendingPiece);

}
