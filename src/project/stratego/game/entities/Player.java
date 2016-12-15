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
    private ArrayList<Piece> deadPieces;
    private Piece currentPiece;

    public Player(PlayerType type) {
        this.type = type;
        this.activePieces = new ArrayList<>();
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

    public ArrayList<Piece> getDeadPieces() {
        return deadPieces;
    }

    /* Setter methods */

    public void setCurrentPiece(Piece activePiece) {
        this.currentPiece = activePiece;
    }

    /* Other methods */

    public void addPiece(Piece piece) {
        //System.out.println("Piece added for " + type + ": " + piece);
        activePieces.add(piece);
    }

    public void removePiece(Piece piece) {
        activePieces.remove(piece);
    }

    /* Clone methods */

    public Player clone() {
        Player clone = new Player(type);
        for (Piece p : activePieces) {
            clone.getActivePieces().add(p.clone());
        }
        clone.setCurrentPiece(currentPiece == null ? null : currentPiece.clone());
        return clone;
    }

}
