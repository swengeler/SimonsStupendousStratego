package project.stratego.ai.search;

import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public abstract class PerfectInformationAbstractAI {

    protected GameState gameState;

    protected int playerIndex;

    protected PerfectInformationAbstractAI(int playerIndex) {
        this.playerIndex = playerIndex;
        gameState = new GameState();
    }

    public abstract Move getNextMove(Move lastOpponentMove);

    public abstract void makeBoardSetup(GameState inGameState);

    public void copyOpponentSetup(GameState inGameState) {
        gameState.copySetup(inGameState, 1 - playerIndex);
    }

    protected ArrayList<Move> generateLegalMoves(GameState state, int playerIndex) {
        //System.out.println("Number of player pieces: " + state.getPlayer(playerIndex).getActivePieces().size());
        ArrayList<Move> legalMoves = new ArrayList<>();
        int destRow, destCol;
        for (Piece p : state.getPlayer(playerIndex).getActivePieces()) {
            // check for unmovable pieces
            if (p.getType() != PieceType.FLAG && p.getType() != PieceType.BOMB) {
                int moveRadius = 1;
                // scouts can move more squares than the other pieces
                if (p.getType() == PieceType.SCOUT) {
                    moveRadius = 9;
                }
                // loop through the positions around the piece and check whether they can be moved there
                for (int row = -moveRadius; row <= moveRadius; row++) {
                    destRow = p.getRowPos() + row;
                    for (int col = -moveRadius; col <= moveRadius; col++) {
                        destCol = p.getColPos() + col;
                        // check whether the given piece can move to the target position
                        if (!(row == 0 && col == 0) && checkMovePossible(state, p, destRow, destCol)) {
                            // add legal move to list
                            legalMoves.add(new Move(playerIndex, p.getRowPos(), p.getColPos(), destRow, destCol));
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    private boolean checkMovePossible(GameState state, Piece p, int destRow, int destCol) {
        // check if position is on the board
        if (destRow < 0 || destRow >= 10 || destCol < 0 || destCol >= 10) {
            return false;
        }
        // check if the move is in a row or column only
        if ((destRow - p.getRowPos()) != 0 && (destCol - p.getColPos()) != 0) {
            return false;
        }
        // check if position is accessible
        if (!state.getBoardArray()[destRow][destCol].isAccessible()) {
            return false;
        }
        // check if position is already occupied by own piece
        if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null && state.getBoardArray()[destRow][destCol].getOccupyingPiece().getPlayerType() == p.getPlayerType()) {
            return false;
        }
        // check if path to position is blocked by other piece/inaccessible tile
        if (!checkScoutPath(state, p, destRow, destCol)) {
            return false;
        }
        return true;
    }

    private boolean checkScoutPath(GameState state, Piece scout, int destRow, int destCol) {
        // target position is in the same column as current position
        if (scout.getColPos() - destCol == 0) {
            for (int row = scout.getRowPos() + (destRow - scout.getRowPos() < 0 ? -1 : 1); row != destRow; row += (destRow - scout.getRowPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[row][destCol].getOccupyingPiece() != null || !state.getBoardArray()[row][destCol].isAccessible()) {
                    return false;
                }
            }
        } else if (scout.getRowPos() - destRow == 0) {
            for (int col = scout.getColPos() + (destCol - scout.getColPos() < 0 ? -1 : 1); col != destCol; col += (destCol - scout.getColPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[destRow][col].getOccupyingPiece() != null || !state.getBoardArray()[destRow][col].isAccessible()) {
                    return false;
                }
            }
        }
        return true;
    }

}
