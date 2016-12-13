package project.stratego.game.logic;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.moves.*;
import project.stratego.game.utils.*;

import java.util.LinkedList;

public class PlayingLogic extends GameLogic {

    private Player currentPlayer, currentOpponent;

    private LinkedList<Move> moveHistory;

    public PlayingLogic(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
        currentPlayer = firstPlayer;
        currentOpponent = secondPlayer;
        moveHistory = new LinkedList<>();
        //revealPieces();
    }

    @Override
    public void processTraySelect(int playerIndex, int pieceIndex) {
        // maybe make sound?
    }

    @Override
    public void processBoardSelect(int playerIndex, int row, int col) {
        if (currentPlayer.getType().ordinal() != playerIndex) {
            // not the player's turn
            //System.out.println("processBoardSelect: return with code 0");
            return;
        }

        Piece temp = parent.getBoard()[row][col].getOccupyingPiece();
        //System.out.println(temp);
        if (currentPlayer.getCurrentPiece() == null && (temp == null || temp.getPlayerType() != currentPlayer.getType() || temp.getType() == PieceType.BOMB || temp.getType() == PieceType.FLAG)) {
            // no piece selected but either no piece on selected board tile or opponent's piece or unmovable piece
            //System.out.println("processBoardSelect: return with code 1");
            return;
        }
        if (temp != null && currentPlayer.getCurrentPiece() != null && temp.getPlayerType() == currentPlayer.getType() && (temp.getType() == PieceType.BOMB || temp.getType() == PieceType.FLAG)) {
            // don't switch selected piece to flag or bomb
            //System.out.println("processBoardSelect: return with code 2");
            return;
        }
        if (currentPlayer.getCurrentPiece() == null) {
            // select piece at board position
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            //System.out.println("processBoardSelect: return with code 3");
            return;
        }

        int orRow = currentPlayer.getCurrentPiece().getRowPos();
        int orCol = currentPlayer.getCurrentPiece().getColPos();
        parent.getMoveManager().processMove(currentPlayer, currentOpponent, (currentPiece = currentPlayer.getCurrentPiece()), row, col);
        MoveResult result;
        if ((result = parent.getMoveManager().lastMoveResult()) == MoveResult.MOVE) {
            //System.out.println("PIECE MOVED FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            //System.out.println(parent.getBoard()[row][col].getOccupyingPiece());
            ModelComManager.getInstance().sendPieceMoved(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKLOST) {
            //System.out.println("PIECE LOST ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ModelComManager.getInstance().sendAttackLost(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKTIE) {
            //System.out.println("PIECE TIED ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ModelComManager.getInstance().sendAttackTied(parent.getGameID(), orRow, orCol, row, col);
        } else if (result == MoveResult.ATTACKWON) {
            //System.out.println("PIECE WON ATTACK FROM (" + orRow + "|" + orCol + ") TO (" + row + "|" + col + ")");
            ModelComManager.getInstance().sendAttackWon(parent.getGameID(), orRow, orCol, row, col);
        } else if (parent.getBoard()[row][col].getOccupyingPiece() != null && parent.getBoard()[row][col].getOccupyingPiece().getPlayerType() == currentPlayer.getType()) {
            // board tile already has piece from current player -> switch selected pieces
            //System.out.println("Switch selected pieces from " + currentPlayer.getCurrentPiece().getType() + " to " + parent.getBoard()[row][col].getOccupyingPiece());
            currentPlayer.setCurrentPiece(parent.getBoard()[row][col].getOccupyingPiece());
            return;
        }
        if (result != MoveResult.NOMOVE) {
            parent.getGameState().getMoveHistory().add(new Move(playerIndex, orRow, orCol, row, col));
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
            currentPlayer = currentPlayer == playerNorth ? playerSouth : playerNorth;
            currentOpponent = currentOpponent == playerNorth ? playerSouth : playerNorth;
            ModelComManager.getInstance().sendChangeTurn(parent.getGameID(), currentPlayer.getType().ordinal());
        } else {
            System.out.println("Game over:");
            System.out.println("stuff: " + currentOpponent.getActivePieces().size());
            ModelComManager.getInstance().sendGameOver(parent.getGameID(), currentPlayer.getType().ordinal());
            AIComManager.getInstance().gameOver(parent);
        }
    }

    private boolean checkGameOver() {
        boolean playerLost;
        boolean opponentLost = !checkPlayerCanMove(currentOpponent);
        //System.out.println(opponentLost);
        if (!checkPlayerHasFlag(currentOpponent)) {
            // player won
            //System.out.println(0);
            return true;
        }
        if ((playerLost = !checkPlayerCanMove(currentPlayer)) && opponentLost) {
            // tie
            //System.out.println(1);
            return true;
        }
        if (playerLost) {
            // player lost
            //System.out.println(2);
            return true;
        }
        if (opponentLost){
            // player won
            //System.out.println(3);
            return true;
        }
        return false;
    }

    private boolean checkPlayerCanMove(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.FLAG && p.getType() != PieceType.BOMB) {
                //System.out.println("Player can move");
                return true;
            }
        }
        return false;
    }

    private boolean checkPlayerHasFlag(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() == PieceType.FLAG) {
                //System.out.println("Player has flag");
                return true;
            }
        }
        return false;
    }

}
