package ui;

import javafx.scene.Group;

/**
 *
 */
public class Piece extends Group {

    private static int pieceCount;
    private final int ID;

    public Piece(int correspondingPieceID, String type) {
        ID = pieceCount++;
    }

}
