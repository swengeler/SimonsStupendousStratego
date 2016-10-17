package project.stratego.game;

import project.stratego.control.*;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class DeploymentLogic extends GameLogic {

    private Player tempPlayer;

    private int firstPlayerReady = -1;

    public DeploymentLogic(StrategoGame parent, Player playerNorth, Player playerSouth) {
        super(parent, playerNorth, playerSouth);
    }

    public void randomPlaceCurrentPlayer(int playerIndex) {
        if (playerIndex == firstPlayerReady) {
            return;
        }
        PieceFactory.reset();
        tempPlayer = findPlayer(playerIndex);
        tempPlayer.getActivePieces().clear();
        // also needs to reset board
        ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendResetDeployment(parent.getGameID(), playerIndex);
        Piece temp;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                temp = PieceFactory.makeRandomPiece(tempPlayer.getType());
                tempPlayer.addPiece(temp);
                parent.getBoard()[tempPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(temp);
                ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendPiecePlaced(parent.getGameID(), playerIndex, temp.getType().ordinal(), temp.getRowPos(), temp.getColPos());
            }
        }
    }

    public void resetDeployment(int playerIndex) {
        PieceFactory.reset();
        tempPlayer = findPlayer(playerIndex);
        tempPlayer.getActivePieces().clear();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                parent.getBoard()[tempPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(null);
            }
        }
        ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendResetDeployment(parent.getGameID(), playerIndex);
    }

    @Override
    public void processTraySelect(int playerIndex, int pieceIndex) {
        if (playerIndex == firstPlayerReady) {
            return;
        }
        Piece temp;
        if ((temp = PieceFactory.makePiece(findPlayer(playerIndex).getType(), PieceType.values()[pieceIndex])) != null) {
            findPlayer(playerIndex).addPiece(temp);
            findPlayer(playerIndex).setCurrentPiece(temp);
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendTrayActiveUpdate(parent.getGameID(), pieceIndex);
        }
    }

    @Override
    public void processBoardSelect(int playerIndex, int row, int col) {
        if (playerIndex == firstPlayerReady || !isLegalSelection(playerIndex, row)) {
            // maybe call some method to notify user
            return;
        }
        tempPlayer = findPlayer(playerIndex);
        if (tempPlayer.getCurrentPiece() != null) {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null && tempPlayer.getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on occupied tile on board
                // -> no special functionality right now, needs to be placed somewhere else
                // -> could send cue to place elsewhere though
            } else if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                // piece from board to be placed on occupied tile on board
                // -> simply swaps the current active piece, not the positions of the pieces or similar
                tempPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendActivePieceUpdate(parent.getGameID(), playerIndex, tempPlayer.getCurrentPiece().getRowPos(), tempPlayer.getCurrentPiece().getColPos());
            } else if (tempPlayer.getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on unoccupied tile on board
                // -> simply place the piece
                parent.getBoard()[row][col].setOccupyingPiece(tempPlayer.getCurrentPiece());
                //System.out.println(tempPlayer.getCurrentPiece().getType() + " placed at " + row + ", " + col);
                ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendPiecePlaced(parent.getGameID(), playerIndex, tempPlayer.getCurrentPiece().getType().ordinal(), tempPlayer.getCurrentPiece().getRowPos(), tempPlayer.getCurrentPiece().getColPos());
                //tempPlayer.setCurrentPiece(null);
            } else {
                // piece from board to be placed on unoccupied tile on board
                //System.out.println("Piece occupying a tile should be moved");
                int orRow = tempPlayer.getCurrentPiece().getRowPos();
                int orCol = tempPlayer.getCurrentPiece().getColPos();
                parent.getBoard()[orRow][orCol].setOccupyingPiece(null);
                parent.getBoard()[row][col].setOccupyingPiece(tempPlayer.getCurrentPiece());
                ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendPieceMoved(parent.getGameID(), orRow, orCol, row, col);
            }
        } else {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                tempPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                //System.out.println(tempPlayer.getCurrentPiece() + " selected");
                // send notification to UI to highlight selected piece
            }
        }
    }

    @Override
    public void processPlayerReady(int playerIndex) {
        System.out.println(findPlayer(playerIndex).getActivePieces().size());
        if (firstPlayerReady == -1 && findPlayer(playerIndex).getActivePieces().size() == 40) {
            // player sending "ready" is the first to be ready, have to wait for other player
            // controller.sendWaitForOtherPlayer();
            firstPlayerReady = playerIndex;
            System.out.println("Player " + playerIndex + " is ready.");
        } else if (findPlayer(playerIndex).getActivePieces().size() == 40) {
            // both players are ready
            System.out.println("Both players are ready.");
            playerNorth.setCurrentPiece(null);
            playerSouth.setCurrentPiece(null);
            parent.switchStates();
        } else {
            // controller.notification("Not ready");
        }
    }

    private void hidePieces(int playerIndex) {
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null && temp.getPlayerType() == findPlayer(playerIndex).getType() && !temp.isRevealed()) {
                    ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendHidePiece(parent.getGameID(), -1, temp.getRowPos(), temp.getColPos());
                }
            }
        }
    }

    private boolean isLegalSelection(int playerIndex, int row) {
        if (( row < 4 && findPlayer(playerIndex).getType() == PlayerType.NORTH) || (row > 5 && findPlayer(playerIndex).getType() == PlayerType.SOUTH)) {
            return true;
        }
        return false;
    }

    private Player findPlayer(int playerIndex) {
        if (playerNorth.getType().ordinal() == playerIndex) {
            return playerNorth;
        }
        return playerSouth;
    }

}
