package project.stratego.game.utils;

import project.stratego.game.entities.*;

/**
 * A class that operates on a specific instance of a board (array of board tiles). When a move
 * should be made on that board this class can process it and change the state of the board.
 */
public class MoveManager {

    private BoardTile[][] board;

    private MoveResult lastMoveResult;
    private Piece lastRemovedPiece;

    public MoveManager(BoardTile[][] board) {
        this.board = board;
        System.out.println("BOARD IN MOVEMANAGER CONSTRUCTOR: " + board);
    }

    public MoveResult testMove(Piece movingPiece, int destRow, int destCol) {
        if (destRow < 0 || destRow >= 10 || destCol < 0 || destCol >= 10) {
            return MoveResult.NOMOVE;
        }
        BoardTile destTile = board[destRow][destCol];
        if (movingPiece.getType() == PieceType.BOMB || movingPiece.getType() == PieceType.FLAG) {
            return MoveResult.NOMOVE;
        }
        if (!destTile.isAccessible()) {
            return MoveResult.NOMOVE;
        }
        if (destTile.getOccupyingPiece() != null && destTile.getOccupyingPiece().getPlayerType() == movingPiece.getPlayerType()) {
            return MoveResult.NOMOVE;
        }
        if (!checkIfReachable(movingPiece, destRow, destCol)) {
            return MoveResult.NOMOVE;
        }
        return MoveResult.MOVE;
    }

    /**
     * A method that resolves the request to move a certain selected piece to a certain board position.
     * It also takes as parameters the players of the game according to their respective roles, in order
     * to possibly remove pieces from their available pieces.
     */
    public void processMove(Player movingPlayer, Player staticPlayer, Piece movingPiece, int destRow, int destCol) {
        System.out.println("BOARD IN MOVEMANAGER: " + board);
        lastRemovedPiece = null;
        lastMoveResult = MoveResult.NOMOVE;

        BoardTile destTile = board[destRow][destCol];
        // doesn't account for flag/bomb because you cannot select those
        if (!destTile.isAccessible()) {
            System.out.println("Did not move because not accessible");
            return;
        }
        if (destTile.getOccupyingPiece() != null && destTile.getOccupyingPiece().getPlayerType() == movingPiece.getPlayerType()) {
            // also accounts for clicking the same tile again
            System.out.println("Did not move because occupied by own piece");
            return;
        }
        if (!checkIfReachable(movingPiece, destRow, destCol)) {
            System.out.println("Did not move because unreachable");
            return;
        }

        // at this point we know that the piece can move there
        lastMoveResult = MoveResult.MOVE;
        movingPiece.revealMove();
        board[movingPiece.getRowPos()][movingPiece.getColPos()].setOccupyingPiece(null);

        if (destTile.getOccupyingPiece() != null) {
            movingPiece.reveal();
            if (destTile.getOccupyingPiece().getType() == movingPiece.getType()) {
                lastMoveResult = MoveResult.ATTACKTIE;
                destTile.getOccupyingPiece().reveal();
                lastRemovedPiece = destTile.getOccupyingPiece();
                destTile.setOccupyingPiece(null);
                movingPlayer.removePiece(movingPiece);
                staticPlayer.removePiece(lastRemovedPiece);
            } else if (checkIfAttackWins(movingPiece, destTile.getOccupyingPiece())) {
                lastMoveResult = MoveResult.ATTACKWON;
                lastRemovedPiece = destTile.getOccupyingPiece();
                movingPiece.reveal();
                destTile.getOccupyingPiece().reveal();
                destTile.setOccupyingPiece(movingPiece);
                staticPlayer.removePiece(lastRemovedPiece);
            } else {
                lastMoveResult = MoveResult.ATTACKLOST;
                lastRemovedPiece = movingPiece;
                destTile.getOccupyingPiece().reveal();
                movingPlayer.removePiece(movingPiece);
            }
        } else {
            System.out.println("(" + destRow + "|" + destCol + ") is set to occupied: " + movingPiece);
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

    public Piece lastRemovedPiece() {
        return lastRemovedPiece;
    }

    public void changeBoard(BoardTile[][] newBoard) {
        this.board = newBoard;
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
        int c;
        if (rowDiff == 0) {
            c = colDiff > 0 ? 1 : -1;
            for (int i = 1; i < Math.abs(colDiff); i++) {
                if (board[destRow][movingPiece.getColPos() + i * c].getOccupyingPiece() != null || !board[destRow][movingPiece.getColPos() + i * c].isAccessible()) {
                    return false;
                }
            }
        } else if (colDiff == 0) {
            c = rowDiff > 0 ? 1 : -1;
            for (int i = 1; i < Math.abs(rowDiff); i++) {
                if (board[movingPiece.getRowPos() + i * c][destCol].getOccupyingPiece() != null || !board[movingPiece.getRowPos() + i * c][destCol].isAccessible()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIfAttackWins(Piece attackingPiece, Piece defendingPiece) {
        if (attackingPiece.getType() == PieceType.SPY && defendingPiece.getType() == PieceType.MARSHAL) {
            return true;
        }
        if (attackingPiece.getType() == PieceType.MINER && defendingPiece.getType() == PieceType.BOMB) {
            return true;
        }
        return (PieceType.pieceLvlMap.get(attackingPiece.getType())) > (PieceType.pieceLvlMap.get(defendingPiece.getType()));
    }

}
