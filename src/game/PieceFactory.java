package game;

import java.util.ArrayList;

/**
 *
 */
public class PieceFactory {

    private static int[][] pieceCount = {
            PieceType.pieceQuantity.clone(),
            PieceType.pieceQuantity.clone()
    };

    public static Piece makePiece(PlayerType playerType, PieceType pieceType) {
        if (pieceCount[playerType.ordinal()][pieceType.ordinal()] > 0) {
            pieceCount[playerType.ordinal()][pieceType.ordinal()]--;
            return new Piece(pieceType, playerType);
        }
        return null;
    }

    public static ArrayList<Piece> generatePieces(PlayerType pType) {
        ArrayList<Piece> pieces = new ArrayList<>(40);
        for (int i = 0; i < PieceType.pieceQuantity.length; i++) {
            for (int j = 0; j < PieceType.pieceQuantity[i]; j++) {
                pieces.add(new Piece(PieceType.values()[i], pType));
            }
        }

        return pieces;
    }

}
