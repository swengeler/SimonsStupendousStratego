package project.stratego.game.entities;

public class BoardTile {

    private Piece occupyingPiece;

    private final int rowPos, colPos;

    private boolean isAccessible;

    public BoardTile(boolean isAccessible, int rowPos, int colPos) {
        this.isAccessible = isAccessible;
        this.rowPos = rowPos;
        this.colPos = colPos;
    }

    /* Getter methods */

    public boolean isAccessible() {
        return isAccessible;
    }

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    /* Setter methods */

    public void setOccupyingPiece(Piece newOccupyingPiece) {
        this.occupyingPiece = newOccupyingPiece;
        if (occupyingPiece != null) {
            occupyingPiece.setPos(rowPos, colPos);
        }
    }

    /* Clone method */

    public BoardTile clone() {
        BoardTile clone = new BoardTile(isAccessible, rowPos, colPos);
        clone.setOccupyingPiece(occupyingPiece.clone());
        return clone;
    }

}
