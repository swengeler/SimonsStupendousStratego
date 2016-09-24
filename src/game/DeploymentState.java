package game;

/**
 *
 */
public class DeploymentState extends GameState {

    public DeploymentState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        firstPlayer.setActive(true);
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
            activePiece = parent.getBoard()[row][col].getOccupyingPiece();
            // controller will be the CommunicationsHandler instance that will bridge the gap between model and view (probably initialised in the Main class)
            // controller.updateActivePiece(activePiece);
        } else if (activePiece != null) {
            parent.getBoard()[row][col].setOccupyingPiece(activePiece);
            activePiece = null;
            // controller.updateActivePiece(null);
        }
    }

    public void processPlayerReady() {
        if (firstPlayer.isActive() && firstPlayer.getActivePieces().size() == 40) {
            processSwitch();
            // controller.playersSwitched();
        } else if (firstPlayer.isActive()) {
            // controller.notification("Not ready");
        } else if (secondPlayer.getActivePieces().size() == 40) {
            // parent.switchStates();
        } else {
            // controller.notification("Not ready");
        }
    }

    public void processSwitch() {
        firstPlayer.setActive(false);
        secondPlayer.setActive(true);
    }

    private boolean isLegalSelection(int row) {
        PlayerType activeType = firstPlayer.isActive() ? firstPlayer.getType() : secondPlayer.getType();
        if (( row < 4 && activeType == PlayerType.NORTH) || (row > 5 && activeType == PlayerType.SOUTH)) {
            return true;
        }
        return false;
    }

}
