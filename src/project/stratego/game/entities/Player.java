package project.stratego.game.entities;

import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class Player {

    private PlayerType type;
    
    /**
     * An arraylist that keeps track of all the pieces that the player has on the board (that have
     * been placed and not been eliminated yet).
     */
    private ArrayList<Piece> activePieces;
    private Piece currentPiece;

    public Player(PlayerType type) {
        this.type = type;
        this.activePieces = new ArrayList<>();
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
