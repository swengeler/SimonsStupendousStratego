package project.stratego.game.entities;

import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class Player {

    private PlayerType type;
    
    /**
     * An ArrayList that keeps track of all the pieces that the player has on the board (that have
     * been placed and not been eliminated yet).
     */
    private ArrayList<Piece> activePieces;
    private ArrayList<Piece> hiddenPieces;
    private ArrayList<Piece> deadPieces;
    private Piece currentPiece;

    public Player(PlayerType type) {
        this.type = type;
        this.activePieces = new ArrayList<>();
        this.hiddenPieces = new ArrayList<>();
        this.deadPieces = new ArrayList<>();
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

    public ArrayList<Piece> getHiddenPieces() { return hiddenPieces; }

    public ArrayList<Piece> getDeadPieces() {
        return deadPieces;
    }

    /* Setter methods */

    public void setCurrentPiece(Piece activePiece) {
        this.currentPiece = activePiece;
    }

    /* Clone methods */

    public Player clone() {
        Player clone = new Player(type);
        for (Piece p : activePieces) {
            clone.getActivePieces().add(p.clone());
        }
        for (Piece p : hiddenPieces) {
            clone.getHiddenPieces().add(p.clone());
        }
        for (Piece p : deadPieces) {
            clone.getDeadPieces().add(p.clone());
        }
        clone.setCurrentPiece(currentPiece == null ? null : currentPiece.clone());
        return clone;
    }

}
