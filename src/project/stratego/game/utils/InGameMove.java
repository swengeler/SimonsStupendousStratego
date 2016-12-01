package project.stratego.game.utils;

import project.stratego.game.entities.Piece;

public class InGameMove implements Move {

    private int playerIndex, orRow, orCol, destRow, destCol;

    private Piece movedPiece, encounteredPiece;

    public InGameMove(int playerIndex, int orRow, int orCol, int destRow, int destCol, Piece movedPiece, Piece encounteredPiece) {
        this.playerIndex = playerIndex;
        this.orRow = orRow;
        this.orCol = orCol;
        this.destRow = destRow;
        this.destCol = destCol;
        this.movedPiece = movedPiece;
        this.encounteredPiece = encounteredPiece;
    }

    /* Getter methods */

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public int getOrRow() {
        return orRow;
    }

    @Override
    public int getOrCol() {
        return orCol;
    }

    @Override
    public int getDestRow() {
        return destRow;
    }

    @Override
    public int getDestCol() {
        return destCol;
    }

    @Override
    public int length() {
        return Math.abs(destRow - orRow + destCol - orCol);
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getEncounteredPiece() {
        return encounteredPiece;
    }

    /* Clone method */

    public InGameMove clone() {
        return new InGameMove(playerIndex, orRow, orCol, destRow, destCol, movedPiece.clone(), encounteredPiece.clone());
    }

}
