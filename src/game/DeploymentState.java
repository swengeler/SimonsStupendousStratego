package game;

/**
 *
 */
public class DeploymentState extends GameState {

    public DeploymentState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
    }

    public void processTraySelect() {
        // check whether enough pieces left?
        // change active piece
    }

    public void processBoardSelect(int row, int col) {
        if (!isLegalSelection(row)) {
            return;
        }
        if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            // controller will be the CommunicationsHandler instance that will bridge the gap between model and view (probably initialised in the main class)
            // controller.updateActivePiece(currentPiece);
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
