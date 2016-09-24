package game;

/**
 *
 */
public class BoardTile {

    private Piece occupyingPiece;

    private boolean isAccessible;

    public BoardTile(boolean isAccessible) {
        this.isAccessible = isAccessible;
    }

    /* Getter methods */

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    /* Setter methods */

    public void setOccupyingPiece(Piece newOccupyingPiece) {
        this.occupyingPiece = newOccupyingPiece;
    }

    /* Clone method */

    public BoardTile clone() {
        BoardTile clone = new BoardTile(isAccessible);
        clone.setOccupyingPiece(occupyingPiece.clone());
        return clone;
    }

}
