package project.stratego.game;

import project.stratego.control.CombinedComManager;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class DeploymentLogic extends GameLogic {

    private boolean onePlayerReady;

    public DeploymentLogic(StrategoGame parent, Player playerNorth, Player playerSouth) {
        super(parent, playerNorth, playerSouth);
    }

    public void randomPlaceCurrentPlayer(int playerIndex) {
        PieceFactory.reset();
        findPlayer(playerIndex).getActivePieces().clear();
        CombinedComManager.getInstance().sendResetDeployment(parent.getGameID(), findPlayer(playerIndex).getType());
        Piece temp;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                temp = PieceFactory.makeRandomPiece(findPlayer(playerIndex).getType());
                findPlayer(playerIndex).addPiece(temp);
                parent.getBoard()[findPlayer(playerIndex).getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(temp);
                CombinedComManager.getInstance().sendPiecePlaced(parent.getGameID(), temp);
            }
        }
    }

    @Override
    public void processTraySelect(int playerIndex, int pieceIndex) {
        Piece temp;
        if ((temp = PieceFactory.makePiece(findPlayer(playerIndex).getType(), PieceType.values()[pieceIndex])) != null) {
            findPlayer(playerIndex).addPiece(temp);
            findPlayer(playerIndex).setCurrentPiece(temp);
            CombinedComManager.getInstance().sendTrayActiveUpdate(parent.getGameID(), pieceIndex);
        }
    }

    @Override
    public void processBoardSelect(int playerIndex, int row, int col) {
        if (!isLegalSelection(playerIndex, row)) {
            // maybe call some method to notify user
            return;
        }
        if (findPlayer(playerIndex).getCurrentPiece() != null) {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null && findPlayer(playerIndex).getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on occupied tile on board
                // -> no special functionality right now, needs to be placed somewhere else
                // -> could send cue to place elsewhere though
            } else if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                // piece from board to be placed on occupied tile on board
                // -> simply swaps the current active piece, not the positions of the pieces or similar
                findPlayer(playerIndex).setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                CombinedComManager.getInstance().sendActivePieceUpdate(parent.getGameID(), findPlayer(playerIndex).getCurrentPiece());
            } else if (findPlayer(playerIndex).getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on unoccupied tile on board
                // -> simply place the piece
                parent.getBoard()[row][col].setOccupyingPiece(findPlayer(playerIndex).getCurrentPiece());
                //System.out.println(findPlayer(playerIndex).getCurrentPiece().getType() + " placed at " + row + ", " + col);
                CombinedComManager.getInstance().sendPiecePlaced(parent.getGameID(), findPlayer(playerIndex).getCurrentPiece());
                //findPlayer(playerIndex).setCurrentPiece(null);
            } else {
                // piece from board to be placed on unoccupied tile on board
                //System.out.println("Piece occupying a tile should be moved");
                int orRow = findPlayer(playerIndex).getCurrentPiece().getRowPos();
                int orCol = findPlayer(playerIndex).getCurrentPiece().getColPos();
                parent.getBoard()[orRow][orCol].setOccupyingPiece(null);
                parent.getBoard()[row][col].setOccupyingPiece(findPlayer(playerIndex).getCurrentPiece());
                CombinedComManager.getInstance().sendPieceMoved(parent.getGameID(), orRow, orCol, row, col);
            }
        } else {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                findPlayer(playerIndex).setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                //System.out.println(findPlayer(playerIndex).getCurrentPiece() + " selected");
                // send notification to UI to highlight selected piece
            }
        }
    }

    @Override
    public void processPlayerReady(int playerIndex) {
        System.out.println(findPlayer(playerIndex).getActivePieces().size());
        if (!onePlayerReady && findPlayer(playerIndex).getActivePieces().size() == 40) {
            // player sending "ready" is the first to be ready, have to wait for other player
            // controller.sendWaitForOtherPlayer();
            onePlayerReady = true;
        } else if (findPlayer(playerIndex).getActivePieces().size() == 40) {
            // both players are ready
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
                    CombinedComManager.getInstance().sendHidePiece(parent.getGameID(), temp);
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
