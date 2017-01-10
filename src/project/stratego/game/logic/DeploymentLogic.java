package project.stratego.game.logic;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class DeploymentLogic extends GameLogic {

    private Player tempPlayer;

    private PieceFactory pieceFactory;

    private int firstPlayerReady = -1;

    public DeploymentLogic(StrategoGame parent, Player playerNorth, Player playerSouth) {
        super(parent, playerNorth, playerSouth);
        pieceFactory = new PieceFactory();
    }

    public void randomPlaceCurrentPlayer(int playerIndex) {
        if (playerIndex == firstPlayerReady) {
            return;
        }
        pieceFactory.reset();
        tempPlayer = findPlayer(playerIndex);
        tempPlayer.getActivePieces().clear();
        tempPlayer.getDeadPieces().clear();
        // also needs to reset board
        ModelComManager.getInstance().sendResetDeployment(parent.getGameID(), playerIndex);
        Piece temp;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                temp = pieceFactory.makeRandomPiece(tempPlayer.getType());
                tempPlayer.getActivePieces().add(temp);
                parent.getBoard()[tempPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(temp);
                ModelComManager.getInstance().sendPiecePlaced(parent.getGameID(), playerIndex, temp.getType().ordinal(), temp.getRowPos(), temp.getColPos());
            }
        }
    }

    public void resetDeployment(int playerIndex) {
        pieceFactory.reset();
        tempPlayer = findPlayer(playerIndex);
        tempPlayer.getActivePieces().clear();
        tempPlayer.getDeadPieces().clear();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                parent.getBoard()[tempPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(null);
            }
        }
        ModelComManager.getInstance().sendResetDeployment(parent.getGameID(), playerIndex);
    }

    @Override
    public void processTraySelect(int playerIndex, int pieceIndex) {
        if (playerIndex == firstPlayerReady) {
            return;
        }
        Piece tempPiece;
        Player tempPlayer = findPlayer(playerIndex);
        if (tempPlayer.getCurrentPiece() == null && pieceFactory.pieceInStock(tempPlayer.getType(), PieceType.values()[pieceIndex])) {
            //System.out.println("Can place piece of type " + temp.getType());
            tempPiece = pieceFactory.makePiece(tempPlayer.getType(), PieceType.values()[pieceIndex]);
            tempPlayer.getActivePieces().add(tempPiece);
            tempPlayer.setCurrentPiece(tempPiece);
            ModelComManager.getInstance().sendTrayActiveUpdate(parent.getGameID(), playerIndex, pieceIndex);
        } else if (tempPlayer.getCurrentPiece() != null) {
            pieceFactory.giveBackPiece(tempPlayer.getCurrentPiece());
            tempPlayer.getActivePieces().remove(tempPlayer.getActivePieces());
            tempPiece = pieceFactory.makePiece(tempPlayer.getType(), PieceType.values()[pieceIndex]);
            tempPlayer.getActivePieces().add(tempPiece);
            tempPlayer.setCurrentPiece(tempPiece);
            ModelComManager.getInstance().sendTrayActiveUpdate(parent.getGameID(), playerIndex, pieceIndex);
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
                ModelComManager.getInstance().sendActivePieceUpdate(parent.getGameID(), playerIndex, tempPlayer.getCurrentPiece().getRowPos(), tempPlayer.getCurrentPiece().getColPos());
            } else if (tempPlayer.getCurrentPiece().getRowPos() < 0) {
                // piece from tray to be placed on unoccupied tile on board
                // -> simply place the piece
                parent.getBoard()[row][col].setOccupyingPiece(tempPlayer.getCurrentPiece());
                //System.out.println(tempPlayer.getCurrentPiece().getType() + " placed at " + row + ", " + col);
                ModelComManager.getInstance().sendPiecePlaced(parent.getGameID(), playerIndex, tempPlayer.getCurrentPiece().getType().ordinal(), tempPlayer.getCurrentPiece().getRowPos(), tempPlayer.getCurrentPiece().getColPos());
                //tempPlayer.setCurrentPiece(null);
            } else {
                // piece from board to be placed on unoccupied tile on board
                //System.out.println("Piece occupying a tile should be moved");
                int orRow = tempPlayer.getCurrentPiece().getRowPos();
                int orCol = tempPlayer.getCurrentPiece().getColPos();
                parent.getBoard()[orRow][orCol].setOccupyingPiece(null);
                parent.getBoard()[row][col].setOccupyingPiece(tempPlayer.getCurrentPiece());
                ModelComManager.getInstance().sendPieceMoved(parent.getGameID(), orRow, orCol, row, col);
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
        //System.out.println(findPlayer(playerIndex).getActivePieces().size());
        if (firstPlayerReady == -1 && findPlayer(playerIndex).getActivePieces().size() == 40) {
            // player sending "ready" is the first to be ready, have to wait for other player
            // controller.sendWaitForOtherPlayer();
            firstPlayerReady = playerIndex;
            System.out.println("Player " + playerIndex + " is ready.");
            //AIComManager.getInstance().tryBoardSetup(parent.getGameState());
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
                    ModelComManager.getInstance().sendHidePiece(parent.getGameID(), -1, temp.getRowPos(), temp.getColPos());
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
