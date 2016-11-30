package project.stratego.ai.searchGenerics;

import project.stratego.ai.EnhancedGameState;
import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.Move;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class GenericAI {

    protected int playerIndex;
    protected EnhancedGameState gameState;

    public GenericAI(int playerIndex) {
        this.playerIndex = playerIndex;
        gameState = new EnhancedGameState(playerIndex);
    }

    public Move getNextMove(Move lastOpponentMove) {
        return null;
    }

    public int getPlayerIndex() {
        return -1;
    }

    public void makeBoardSetup(GameState state) {}

    public void copyOpponentSetup(GameState state) {}

    protected ArrayList<AIMove> generateLegalMoves(EnhancedGameState state, int playerIndex) {
        ArrayList<AIMove> legalMoves = new ArrayList<>();
        boolean chanceEvent = false;
        int destRow, destCol;
        for (Piece p : state.getPlayer(playerIndex).getActivePieces()) {
            // check for unmovable pieces
            if ((p.getPlayerType().ordinal() == state.getPlayerIndex() && p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG) ||
                    (Math.abs(state.getProbability(p, PieceType.BOMB) - 1.0) <= EnhancedGameState.PROB_EPSILON && Math.abs(state.getProbability(p, PieceType.FLAG) - 1.0) <= EnhancedGameState.PROB_EPSILON)) {
                int moveRadius = 1;
                // scouts can move more squares than the other pieces
                if ((p.getType() == PieceType.SCOUT && p.getPlayerType().ordinal() != state.getPlayerIndex()) || Math.abs(state.getProbability(p, PieceType.SCOUT) - 1.0) <= EnhancedGameState.PROB_EPSILON) {
                    moveRadius = 9;
                }
                // loop through the positions around the piece and check whether they can be moved there
                for (int row = -moveRadius; row <= moveRadius; row++) {
                    destRow = p.getRowPos() + row;
                    for (int col = -moveRadius; col <= moveRadius; col++) {
                        destCol = p.getColPos() + col;
                        // check whether the given piece can move to the target position
                        if (!(row == 0 && col == 0) && checkMovePossible(state, p, destRow, destCol)) {
                            // add legal move to list and also specify whether it will induce a chance event
                            if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null) {
                                // either different playerIndex from root (initPlayerIndex) AND piece to be moved is not revealed AND position to be moved to is taken by root player
                                chanceEvent = playerIndex != this.playerIndex && !p.isRevealed(); // last check not necessary because of the if-statement checking for null; move would not be possible anyway if position was occupied by own piece
                                // OR same playerIndex as root (initPlayerIndex) AND position to be moved to is taken by opponent's unrevealed piece
                                chanceEvent = chanceEvent || (playerIndex == this.playerIndex && !state.getBoardArray()[destRow][destCol].getOccupyingPiece().isRevealed());
                            }
                            legalMoves.add(new AIMove(p.getRowPos(), p.getColPos(), destRow, destCol, chanceEvent));
                            chanceEvent = false;
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    protected boolean checkMovePossible(EnhancedGameState state, Piece p, int destRow, int destCol) {
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
        if (((p.getType() == PieceType.SCOUT && p.getPlayerType().ordinal() != state.getPlayerIndex()) || Math.abs(state.getProbability(p, PieceType.SCOUT.ordinal()) - 1.0) <= EnhancedGameState.PROB_EPSILON) && !checkScoutPath(state, p, destRow, destCol)) {
            return false;
        }
        return true;
    }

    protected boolean checkScoutPath(EnhancedGameState state, Piece scout, int destRow, int destCol) {
        // target position is in the same column as current position
        if (scout.getColPos() - destCol == 0) {
            for (int row = scout.getRowPos(); row != destRow; row += (destRow - scout.getRowPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[row][destCol].getOccupyingPiece() != null || !state.getBoardArray()[row][destCol].isAccessible()) {
                    return false;
                }
            }
        } else if (scout.getRowPos() - destRow == 0) {
            for (int col = scout.getColPos(); col != destRow; col += (destRow - scout.getColPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[destRow][col].getOccupyingPiece() != null || !state.getBoardArray()[destRow][col].isAccessible()) {
                    return false;
                }
            }
        }
        return true;
    }


}
