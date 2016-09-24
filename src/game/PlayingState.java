package game;

/**
 *
 */
public class PlayingState extends GameState {

    public PlayingState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
    }

    public void processTraySelect() {
        // maybe make sound?
    }

    public void processBoardSelect(int row, int col) {
        if (currentPlayer.getCurrentPiece() == null && (parent.getBoard()[row][col].getOccupyingPiece() == null || parent.getBoard()[row][col].getOccupyingPiece().getPlayerType() != currentPlayer.getType())) {
            return;
        }
        if (currentPlayer.getCurrentPiece() == null) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            return;
        }
        parent.getMoveManager().processMove((currentPiece = currentPlayer.getCurrentPiece()), row, col);
        MoveResult result;
        if ((result = parent.getMoveManager().lastMoveResult()) == MoveResult.MOVE) {

        } else if (result == MoveResult.ATTACKLOST) {
            currentPlayer.removePiece(currentPiece);
        } else if (result == MoveResult.ATTACKTIE) {
            currentPlayer.removePiece(currentPiece);
            currentOpponent.removePiece(parent.getMoveManager().lastRemovedPiece());
        } else if (result == MoveResult.ATTACKWON) {
            currentOpponent.removePiece(parent.getMoveManager().lastRemovedPiece());
        }
        processEndTurn();
    }

    public void processEndTurn() {
        boolean gameOver = checkGameOver();
        if (!gameOver) {
            processSwitch();
        }
    }

    public void processSwitch() {
        currentPlayer = currentPlayer == firstPlayer ? secondPlayer : firstPlayer;
        currentOpponent = currentOpponent == firstPlayer ? secondPlayer : firstPlayer;
        // controller.playersSwitched();
    }

    private boolean checkGameOver() {
        boolean playerLost;
        boolean opponentLost = checkPlayerCanMove(currentOpponent);
        if (checkPlayerHasFlag(currentOpponent)) {
            // player won
            return true;
        }
        if ((playerLost = checkPlayerCanMove(currentPlayer)) && opponentLost) {
            // tie
            return true;
        }
        if (playerLost) {
            // player lost
            return true;
        }
        if (opponentLost){
            // player won
            return true;
        }
        return false;
    }

    private boolean checkPlayerCanMove(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.FLAG && p.getType() != PieceType.BOMB) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPlayerHasFlag(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() == PieceType.FLAG) {
                return true;
            }
        }
        return false;
    }

}
