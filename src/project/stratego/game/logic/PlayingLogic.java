package project.stratego.game.logic;

import project.stratego.ai.tests.AITestsMain;
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
            //System.out.println("processBoardSelect: return with code 1 (" + row + "|" + col + ")");
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
            //System.out.println("Game not over");
        } else {
            System.out.println("Game over by " + (checkPlayerHasFlag(currentOpponent) ? "piece capture" : "flag capture"));
            AITestsMain.addWin(currentPlayer.getType().ordinal(), checkPlayerHasFlag(currentOpponent) ? 1 : 0);
            ModelComManager.getInstance().sendGameOver(parent.getGameID(), currentPlayer.getType().ordinal());
            AIComManager.getInstance().gameOver(parent);
        }
    }

    private boolean checkGameOver() {
        return !checkPlayerHasFlag(currentOpponent) || !checkPlayerHasFlag(currentPlayer) || !checkPlayerCanMove(currentOpponent) || !checkPlayerCanMove(currentPlayer);
    }

    private boolean checkPlayerCanMove(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.FLAG && p.getType() != PieceType.BOMB) {
                for (int row = p.getRowPos() - 1; row <= p.getRowPos() + 1; row += 2) {
                    for (int col = p.getColPos() - 1; col <= p.getColPos() + 1; col += 2) {
                        if (row >= 0 && row < 10 && col >= 0 && col < 10 && (parent.getBoard()[row][col].getOccupyingPiece() == null || parent.getBoard()[row][col].getOccupyingPiece().getPlayerType() != player.getType())) {
                            //System.out.println(p + " can move");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkPlayerHasFlag(Player player) {
        for (Piece p : player.getActivePieces()) {
            if (p.getType() == PieceType.FLAG) {
                return true;
            }
        }
        return false;
    }

}
