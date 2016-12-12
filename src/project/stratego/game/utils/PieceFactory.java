package project.stratego.game.utils;

import project.stratego.game.entities.Piece;

import java.util.ArrayList;

/**
 * A class that keeps track of the pieces that a player has not deployed on the board yet
 * and can be used to generate a piece of a certain type if the requesting player has not
 * placed all available pieces of that type yet.
 */
public class PieceFactory {

    private int[][] pieceCount = {
            PieceType.pieceQuantity.clone(),
            PieceType.pieceQuantity.clone()
    };

    private static int total = PieceType.pieceQuantity[0] + PieceType.pieceQuantity[1] + PieceType.pieceQuantity[2] +
            PieceType.pieceQuantity[3] + PieceType.pieceQuantity[4] + PieceType.pieceQuantity[5] +
            PieceType.pieceQuantity[6] + PieceType.pieceQuantity[7] + PieceType.pieceQuantity[8] +
            PieceType.pieceQuantity[9] + PieceType.pieceQuantity[10] + PieceType.pieceQuantity[11];

    /**
     * A method that checks whether the requesting player is allowed to generate and place another
     * piece of the specified type and returns a piece of that type if the player is allowed to do so.
     */
    public Piece makePiece(PlayerType playerType, PieceType pieceType) {
        if (pieceCount[playerType.ordinal()][pieceType.ordinal()] > 0) {
            pieceCount[playerType.ordinal()][pieceType.ordinal()]--;
            return new Piece(pieceType, playerType);
        }
        return null;
    }

    public boolean pieceInStock(PlayerType playerType, PieceType pieceType) {
        if (pieceCount[playerType.ordinal()][pieceType.ordinal()] > 0) {
            return true;
        }
        return false;
    }

    public Piece makeRandomPiece(PlayerType playerType) {
        for (int i : pieceCount[playerType.ordinal()]) {
            if (i > 0) {
                int pieceIndex = (int) (Math.random() * 12);
                while (pieceCount[playerType.ordinal()][pieceIndex] == 0) {
                    pieceIndex = (int) (Math.random() * 12);
                }
                pieceCount[playerType.ordinal()][pieceIndex]--;
                return new Piece(PieceType.values()[pieceIndex], playerType);
            }
        }
        return null;
    }

    public void reset() {
        pieceCount[0] = PieceType.pieceQuantity.clone();
        pieceCount[1] = PieceType.pieceQuantity.clone();
    }

    public ArrayList<Piece> generatePieces(PlayerType pType) {
        ArrayList<Piece> pieces = new ArrayList<>(40);
        for (int i = 0; i < PieceType.pieceQuantity.length; i++) {
            for (int j = 0; j < PieceType.pieceQuantity[i]; j++) {
                pieces.add(new Piece(PieceType.values()[i], pType));
            }
        }

        return pieces;
    }

}
