package ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 */
public class BoardTile extends ImageView {

    public static double TILE_SIZE = 58;

    private int rowPos, colPos;

    public BoardTile(int rowPos, int colPos, Image boardBackground) {
        super();
        this.rowPos = rowPos;
        this.colPos = colPos;
        cutAndSetImage(boardBackground);
    }

    private void cutAndSetImage(Image boardBackground) {
        Rectangle2D viewPort = new Rectangle2D(colPos * TILE_SIZE, rowPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        setImage(boardBackground);
        setViewport(viewPort);
    }

    public int getRowPos() {
        return rowPos;
    }

    public int getColPos() {
        return colPos;
    }
}
