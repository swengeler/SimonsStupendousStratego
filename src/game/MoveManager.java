package game;

/**
 *
 */
public class MoveManager {

    private BoardTile[][] board;

    private MoveResult lastMoveResult;
    private Piece lastRemovedPiece;

    public MoveManager(BoardTile[][] board) {
        this.board = board;
    }

    public void processMove(Piece movingPiece, int destRow, int destCol) {
        lastRemovedPiece = null;
        lastMoveResult = MoveResult.NOMOVE;
        BoardTile destTile = board[destRow][destCol];
        // doesn't account for flag/bomb because you cannot select those
        if (!destTile.isAccessible()) {
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
            return;
        }
        if (destTile.getOccupyingPiece().getPlayerType() == movingPiece.getPlayerType()) {
            // also accounts for clicking the same tile again
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
            return;
        }
        if (!checkIfReachable(movingPiece, destRow, destCol)) {
            // controller.giveVisualOrAudioClueThatYouDoneGoofed();
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
                return;
            }
            if (checkIfAttackWins(movingPiece, destTile.getOccupyingPiece())) {
                lastMoveResult = MoveResult.ATTACKWON;
                lastRemovedPiece = destTile.getOccupyingPiece();
                destTile.getOccupyingPiece().reveal();
                destTile.setOccupyingPiece(movingPiece);
                return;
            }
            lastMoveResult = MoveResult.ATTACKLOST;
            lastRemovedPiece = movingPiece;
            destTile.getOccupyingPiece().reveal();
        }
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
