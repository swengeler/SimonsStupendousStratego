package project.stratego.game;

import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class DeploymentState extends GameState {

    public DeploymentState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
        currentOpponent = secondPlayer;
    }

    public void randomPlaceCurrentPlayer() {
        PieceFactory.reset();
        currentPlayer.getActivePieces().clear();
        StrategoGame.getInstance().getComManager().sendResetDeployment(currentPlayer.getType());
        Piece temp;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                temp = PieceFactory.makeRandomPiece(currentPlayer.getType());
                currentPlayer.addPiece(temp);
                parent.getBoard()[currentPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(temp);
                parent.getComManager().sendPiecePlaced(temp);
            }
        }
    }

    public void processTraySelect(PlayerType playerType, PieceType pieceType) {
        //System.out.println("Piece = " + pieceType + " from player = " + playerType);
        if (playerType != currentPlayer.getType()) {
            return;
        }
        Piece temp;
        if ((temp = PieceFactory.makePiece(currentPlayer.getType(), pieceType)) != null) {
            currentPlayer.addPiece(temp);
            currentPlayer.setCurrentPiece(temp);
            parent.getComManager().sendTrayActiveUpdate(pieceType);
        }
    }

    public void processBoardSelect(int row, int col) {
        if (!isLegalSelection(row)) {
            // maybe call some method to notify user
            return;
        }
        if (currentPlayer.getCurrentPiece() != null) {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null && currentPlayer.getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on occupied tile on board
                // -> no special functionality right now, needs to be placed somewhere else
                // -> could send cue to place elsewhere though
            } else if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                // piece from board to be placed on occupied tile on board
                // -> simply swaps the current active piece, not the positions of the pieces or similar
                currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                parent.getComManager().sendActivePieceUpdate(currentPlayer.getCurrentPiece());
            } else if (currentPlayer.getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on unoccupied tile on board
                // -> simply place the piece
                parent.getBoard()[row][col].setOccupyingPiece(currentPlayer.getCurrentPiece());
                //System.out.println(currentPlayer.getCurrentPiece().getType() + " placed at " + row + ", " + col);
                StrategoGame.getInstance().getComManager().sendPiecePlaced(currentPlayer.getCurrentPiece());
                currentPlayer.setCurrentPiece(null);
            } else {
                // piece from board to be placed on unoccupied tile on board
                //System.out.println("Piece occupying a tile should be moved");
                int orRow = currentPlayer.getCurrentPiece().getRowPos();
                int orCol = currentPlayer.getCurrentPiece().getColPos();
                parent.getBoard()[orRow][orCol].setOccupyingPiece(null);
                parent.getBoard()[row][col].setOccupyingPiece(currentPlayer.getCurrentPiece());
                StrategoGame.getInstance().getComManager().sendPieceMoved(orRow, orCol, row, col);
            }
        } else {
            if (parent.getBoard()[row][col].getOccupyingPiece() != null) {
                currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
                //System.out.println(currentPlayer.getCurrentPiece() + " selected");
                // send notification to UI to highlight selected piece
            }
        }
    }

    public void processPlayerReady() {
        System.out.println(currentPlayer.getActivePieces().size());
        if (currentPlayer == firstPlayer && currentPlayer.getActivePieces().size() == 40) {
            hidePieces();
            currentPlayer = currentOpponent;
        } else if (currentPlayer.getActivePieces().size() == 40) {
            firstPlayer.setCurrentPiece(null);
            secondPlayer.setCurrentPiece(null);
            hidePieces();
            parent.switchStates();
        } else {
            // controller.notification("Not ready");
        }
    }

    private void hidePieces() {
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null && temp.getPlayerType() == currentPlayer.getType() && !temp.isRevealed()) {
                    parent.getComManager().sendHidePiece(temp);
                }
            }
        }
    }

    private boolean isLegalSelection(int row) {
        if (( row < 4 && currentPlayer.getType() == PlayerType.NORTH) || (row > 5 && currentPlayer.getType() == PlayerType.SOUTH)) {
            return true;
        }
        return false;
    }

}
