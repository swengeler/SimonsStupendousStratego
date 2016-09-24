package game;

import java.util.ArrayList;

/**
 *
 */
public class Player {

    private PlayerType type;

    private ArrayList<Piece> activePieces;
    private Piece selectedPiece;

    private boolean isActive;

    public Player(PlayerType type) {
        this.type = type;
        this.activePieces = PieceFactory.generatePieces(type);
    }

    /* Getter methods */

    public PlayerType getType() {
        return type;
    }

    public Piece getActivePiece() {
        return selectedPiece;
    }

    public ArrayList<Piece> getActivePieces() {
        return activePieces;
    }

    public boolean isActive() {
        return isActive;
    }

    /* Setter methods */

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}
