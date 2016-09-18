/*
package ui;

import game.Piece;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

*/
/**
 * Created by Simon on 18/09/2016.
 *//*

public class BoardTile extends Rectangle {

    private int id;

    private int gridX;
    private int gridY;

    private Piece occupyingPiece;

    public BoardTile(int gridX, int gridY, int id) {
        super(gridX * InGameView.TILE_SIZE + InGameView.X_OFFSET, gridY * InGameView.TILE_SIZE + InGameView.Y_OFFSET, InGameView.TILE_SIZE, InGameView.TILE_SIZE);
        this.gridX = gridX;
        this.gridY = gridY;
        this.id = id;
        initEventListeners();
    }

    private void initEventListeners() {
        setOnMouseEntered((MouseEvent e) -> setEffect(new DropShadow()));
        setOnMouseExited((MouseEvent e) -> setEffect(null));
        setOnMouseClicked((MouseEvent e) -> {
            if (Main.getInstance().getInGameView().pieceSelected()) {
                setOccupyingPiece();
            }
        });
        getStyleClass().add(x % 2 == 0 ? "evenRectangle" : "oddRectangle");
    }

    public void setOccupyingPiece(Piece newPiece) {
        occupyingPiece = newPiece;
    }

    public boolean isOccupied() {
        return !(occupyingPiece == null);
    }

}
*/
