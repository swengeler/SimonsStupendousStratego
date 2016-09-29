package project.stratego.game.utils;

import project.stratego.game.entities.*;

public class MoveManager {

    private BoardTile[][] board;

    private MoveResult lastMoveResult;
    private Piece lastRemovedPiece;

    public MoveManager(BoardTile[][] board) {
        this.board = board;
    }

    public void processMove(Player movingPlayer, Player staticPlayer, Piece movingPiece, int destRow, int destCol) {
        lastRemovedPiece = null;
        lastMoveResult = MoveResult.NOMOVE;
        BoardTile destTile = board[destRow][destCol];
        // doesn't account for flag/bomb because you cannot select those
        if (!destTile.isAccessible()) {
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
            System.out.println("Did not move because not accessible");
            return;
        }
        if (destTile.getOccupyingPiece() != null && destTile.getOccupyingPiece().getPlayerType() == movingPiece.getPlayerType()) {
            // also accounts for clicking the same tile again
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
            System.out.println("Did not move because occupied by own piece");
            return;
        }
        if (!checkIfReachable(movingPiece, destRow, destCol)) {
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
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
            destTile.setOccupyingPiece(movingPiece);
        }
        movingPlayer.setCurrentPiece(null);
    }

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
