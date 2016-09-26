package game;

import java.util.ArrayList;

/**
 *
 */
public class Player {

    private PlayerType type;

    private ArrayList<Piece> activePieces;
    private Piece currentPiece;

    public Player(PlayerType type) {
        this.type = type;
    }

    /* Getter methods */

    public PlayerType getType() {
        return type;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public ArrayList<Piece> getActivePieces() {
        return activePieces;
    }

    /* Setter methods */

    public void setCurrentPiece(Piece activePiece) {
        this.currentPiece = activePiece;
    }

    /* Other methods */

    public void addPiece(Piece piece) {
        activePieces.add(piece);
    }

    public void removePiece(Piece piece) {
        activePieces.remove(piece);
    }

}
