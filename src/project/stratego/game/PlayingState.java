package project.stratego.game;

import project.stratego.control.CommunicationManager;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.*;

public class PlayingState extends GameState {

    boolean testing = false;

    public PlayingState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
        currentOpponent = secondPlayer;
        revealPieces();
    }

    public void processTraySelect(PlayerType playerType, PieceType pieceType) {
        // maybe make sound?
    }

    public void processBoardSelect(int row, int col) {
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
            CommunicationManager.getInstance().sendPieceMoved(orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKLOST) {
            System.out.println("PIECE LOST ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            CommunicationManager.getInstance().sendAttackLost(orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKTIE) {
            System.out.println("PIECE TIED ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            CommunicationManager.getInstance().sendAttackTied(orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKWON) {
            System.out.println("PIECE WON ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            CommunicationManager.getInstance().sendAttackWon(orRow, orCol, row, col);
        } else if (parent.getBoard()[row][col].getOccupyingPiece() != null && parent.getBoard()[row][col].getOccupyingPiece().getPlayerType() == currentPlayer.getType()) {
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            return;
        }
        if (result != MoveResult.NOMOVE && !testing) {
            processPlayerReady();
        }
    }

    public void processPlayerReady() {
        boolean gameOver = checkGameOver();
        if (!gameOver) {
            System.out.println("player: " + currentPlayer.getActivePieces().size());
            System.out.println("opponent: " + currentOpponent.getActivePieces().size());
            hidePieces();
            currentPlayer = currentPlayer == firstPlayer ? secondPlayer : firstPlayer;
            currentOpponent = currentOpponent == firstPlayer ? secondPlayer : firstPlayer;
            revealPieces();
        } else {
            System.out.println("Game over");
            currentPlayer = currentOpponent;
            revealPieces();
        }
    }

    private void hidePieces() {
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null && temp.getPlayerType() == currentPlayer.getType() && !temp.isRevealed()) {
                    CommunicationManager.getInstance().sendHidePiece(temp);
                }
            }
        }
    }

    private void revealPieces() {
        Piece temp;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((temp = parent.getBoard()[row][col].getOccupyingPiece()) != null && temp.getPlayerType() == currentPlayer.getType()) {
                    CommunicationManager.getInstance().sendRevealPiece(temp);
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
