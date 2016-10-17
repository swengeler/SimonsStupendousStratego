package project.stratego.game;

import project.stratego.control.*;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class PlayingLogic extends GameLogic {

    boolean testing = false;

    private Player currentPlayer, currentOpponent;

    public PlayingLogic(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
        currentOpponent = secondPlayer;
        //revealPieces();
    }

    @Override
    public void processTraySelect(int playerIndex, int pieceIndex) {
        // maybe make sound?
    }

    @Override
    public void processBoardSelect(int playerIndex, int row, int col) {
        if (currentPlayer.getType().ordinal() != playerIndex) {
            return;
        }

        Piece temp = parent.getBoard()[row][col].getOccupyingPiece();
        if (currentPlayer.getCurrentPiece() == null && (temp == null || temp.getPlayerType() != currentPlayer.getType() || temp.getType() == PieceType.BOMB || temp.getType() == PieceType.FLAG)) {
            return;
        }
        if (temp != null && currentPlayer.getCurrentPiece() != null && temp.getPlayerType() == currentPlayer.getType() && (temp.getType() == PieceType.BOMB || temp.getType() == PieceType.FLAG)) {
            return;
        }
        if (currentPlayer.getCurrentPiece() == null) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            return;
        }

        int orRow = currentPlayer.getCurrentPiece().getRowPos();
        int orCol = currentPlayer.getCurrentPiece().getColPos();
        parent.getMoveManager().processMove(currentPlayer, currentOpponent, (currentPiece = currentPlayer.getCurrentPiece()), row, col);
        MoveResult result;
        if ((result = parent.getMoveManager().lastMoveResult()) == MoveResult.MOVE) {
            System.out.println("PIECE MOVED FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendPieceMoved(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKLOST) {
            System.out.println("PIECE LOST ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendAttackLost(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKTIE) {
            System.out.println("PIECE TIED ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendAttackTied(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKWON) {
            System.out.println("PIECE WON ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendAttackWon(parent.getGameID(), orRow, orCol, row, col);
        } else if (parent.getBoard()[row][col].getOccupyingPiece() != null && parent.getBoard()[row][col].getOccupyingPiece().getPlayerType() == currentPlayer.getType()) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            return;
        }
        if (result != MoveResult.NOMOVE && !testing) {
            processPlayerReady(currentPlayer.getType().ordinal());
        }
    }

    @Override
    public void processPlayerReady(int playerIndex) {
        if (currentPlayer.getType().ordinal() != playerIndex) {
            return;
        }

        boolean gameOver = checkGameOver();
        if (!gameOver) {
            System.out.println("player: " + currentPlayer.getActivePieces().size());
            System.out.println("opponent: " + currentOpponent.getActivePieces().size());
            //hidePieces();
            currentPlayer = currentPlayer == playerNorth ? playerSouth : playerNorth;
            currentOpponent = currentOpponent == playerNorth ? playerSouth : playerNorth;
            //revealPieces();
        } else {
            System.out.println("Game over");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendGameOver(parent.getGameID());
        }
    }

    private void hidePieces() {
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null && temp.getPlayerType() == currentPlayer.getType() && !temp.isRevealed()) {
                    ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendHidePiece(parent.getGameID(), currentPlayer.getType().ordinal(), temp.getRowPos(), temp.getColPos());
                }
            }
        }
    }

    private void revealPieces() {
        /*for (Piece p : currentPlayer.getActivePieces()) {
            System.out.println("Piece revealed: " + currentOpponent.getType().ordinal() + " " + p.getRowPos() + " " + p.getColPos() + ".");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendRevealPiece(parent.getGameID(), currentOpponent.getType().ordinal(), p.getRowPos(), p.getColPos());
        }
        for (Piece p : currentOpponent.getActivePieces()) {
            System.out.println("Piece revealed: " + currentOpponent.getType().ordinal() + " " + p.getRowPos() + " " + p.getColPos() + ".");
            ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendRevealPiece(parent.getGameID(), currentPlayer.getType().ordinal(), p.getRowPos(), p.getColPos());
        }*/
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null) {
                    ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendRevealPiece(parent.getGameID(), 0, temp.getRowPos(), temp.getColPos());
                    ((ModelComManager) ManagerManager.getModelReceiver()).getInstance().sendRevealPiece(parent.getGameID(), 1, temp.getRowPos(), temp.getColPos());
                }
            }
        }
    }

    private boolean checkGameOver() {
        boolean playerLost;
        boolean opponentLost = !checkPlayerCanMove(currentOpponent);
        System.out.println(opponentLost);
        if (!checkPlayerHasFlag(currentOpponent)) {
            // player won
            System.out.println(0);
            return true;
        }
        if ((playerLost = !checkPlayerCanMove(currentPlayer)) && opponentLost) {
            // tie
            System.out.println(1);
            return true;
        }
        if (playerLost) {
            // player lost
            System.out.println(2);
            return true;
        }
        if (opponentLost){
            // player won
            System.out.println(3);
            return true;
        }
        return false;
    }

    private boolean checkPlayerCanMove(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.FLAG && p.getType() != PieceType.BOMB) {
                System.out.println("Player can move");
                return true;
            }
        }
        return false;
    }

    private boolean checkPlayerHasFlag(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() == PieceType.FLAG) {
                System.out.println("Player has flag");
                return true;
            }
        }
        return false;
    }

}
