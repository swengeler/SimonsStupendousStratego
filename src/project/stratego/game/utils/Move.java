package project.stratego.game.utils;

import project.stratego.game.entities.Piece;

public class Move {

    private int orRow, orCol, destRow, destCol;

    private Piece movedPiece, encounteredPiece;

    public Move(int orRow, int orCol, int destRow, int destCol, Piece movedPiece, Piece encounteredPiece) {

    }

    /* Getter methods */

    public int getOrRow() {
        return orRow;
    }

    public int getOrCol() {
        return orCol;
    }

    public int getDestRow() {
        return destRow;
    }

    public int getDestCol() {
        return destCol;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getEncounteredPiece() {
        return encounteredPiece;
    }

    /* Clone method */

    public Move clone() {
        return new Move(orRow, orCol, destRow, destCol, movedPiece.clone(), encounteredPiece.clone());
    }

}
