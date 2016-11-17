package project.stratego.game.entities;

import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

public class Piece {

    private static int pieceCount;
    private final int ID;

    private PieceType type;
    private PlayerType playerType;

    private int rowPos = - 1;
    private int colPos = -1;

    private boolean isRevealed;
    private boolean isMoveRevealed;

    public Piece(PieceType type, PlayerType playerType) {
        ID = pieceCount++;
        this.type = type;
        this.playerType = playerType;
    }

    private Piece(PieceType type, PlayerType playerType, int ID) {
        this.ID = ID;
        this.type = type;
        this.playerType = playerType;
    }

    private Piece(PieceType type, PlayerType playerType, int ID, int rowPos, int colPos, boolean isRevealed, boolean isMoveRevealed) {
        this.ID = ID;
        this.type = type;
        this.playerType = playerType;
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.isRevealed = isRevealed;
        this.isMoveRevealed = isMoveRevealed;
    }

    /* Getter methods */

    public int getID() {
        return ID;
    }

    public PieceType getType() {
        return type;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getRowPos() {
        return rowPos;
    }

    public int getColPos() {
        return colPos;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isMoveRevealed() {
        return isMoveRevealed;
    }

    /* Setter methods */

    public void reveal() {
        System.out.println("Piece at (" + rowPos + "|" + colPos + ") revealed.");
        isRevealed = true;
    }

    public void revealMove() {
        isMoveRevealed = true;
    }

    public void markUnknown() {
        type = PieceType.UNKNOWN;
    }

    public void setPos(int rowPos, int colPos) {
        this.rowPos = rowPos;
        this.colPos = colPos;
    }

    /* Clone method */

    public Piece clone() {
        Piece clone = new Piece(type, playerType, ID, rowPos, colPos, isRevealed, isMoveRevealed);
        return clone;
    }

    /* toString method */

    public String toString() {
        return "Piece (model) belonging to player " + playerType + " , of type " + type + ", at position (" + rowPos + "|" + colPos + ")";
    }

}
