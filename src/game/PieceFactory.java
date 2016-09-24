package game;

import java.util.ArrayList;

/**
 *
 */
public class PieceFactory {

    public static ArrayList<Piece> generatePieces(PlayerType pType) {
        // maybe should link pieces to UI objects here?

        ArrayList<Piece> pieces = new ArrayList<>(40);
        for (int i = 0; i < PieceType.pieceQuantity.length; i++) {
            for (int j = 0; j < PieceType.pieceQuantity[i]; j++) {
                pieces.add(new Piece(PieceType.values()[i], pType));
                // link to UI component
            }
        }

        return pieces;
    }

}
