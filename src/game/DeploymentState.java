package game;

/**
 *
 */
public class DeploymentState extends GameState {

    public DeploymentState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
    }

    public void processTraySelect(PieceType type) {
        Piece temp;
        if ((temp = PieceFactory.makePiece(currentPlayer.getType(), type)) != null) {
            currentPlayer.addPiece(temp);
            currentPlayer.setCurrentPiece(temp);
            parent.getComManager().sendTrayActiveUpdate(type);
        }
    }

    public void processBoardSelect(int row, int col) {
        if (!isLegalSelection(row)) {
            // maybe call some method to notify user
            return;
        }
        if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            parent.getComManager().sendActivePieceUpdate(currentPlayer.getCurrentPiece());
        } else if (currentPlayer.getCurrentPiece() != null) {
            parent.getBoard()[row][col].setOccupyingPiece(currentPlayer.getCurrentPiece());
            currentPlayer.setCurrentPiece(null);
            // controller.updateActivePiece(null);
        }
    }

    public void processEndTurn() {
        if (currentPlayer == firstPlayer && currentPlayer.getActivePieces().size() == 40) {
            processSwitch();
        } else if (currentPlayer.getActivePieces().size() == 40) {
            // parent.switchStates();
        } else {
            // controller.notification("Not ready");
        }
    }

    public void processSwitch() {
        currentPlayer = secondPlayer;
        // controller.playersSwitched();
    }

    private boolean isLegalSelection(int row) {
        if (( row < 4 && currentPlayer.getType() == PlayerType.NORTH) || (row > 5 && currentPlayer.getType() == PlayerType.SOUTH)) {
            return true;
        }
        return false;
    }

}
