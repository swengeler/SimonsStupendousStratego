package project.stratego.ui.components;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import project.stratego.control.*;

public class BoardTile extends Group {

    public static double TILE_SIZE = 60;

    private int rowPos, colPos;

    private ImageView fill;
    private Rectangle border;
    private Piece occupyingPiece;

    public BoardTile(int rowPos, int colPos, Image boardBackground) {
        super();
        this.rowPos = rowPos;
        this.colPos = colPos;
        cutAndSetImage(boardBackground);
        makeBorder();
        makeActions();
        toBack();
    }

    private void cutAndSetImage(Image boardBackground) {
        Rectangle2D viewPort = new Rectangle2D(colPos * TILE_SIZE, rowPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        fill = new ImageView();
        fill.setImage(boardBackground);
        fill.setViewport(viewPort);
        getChildren().add(fill);
    }

    private void makeBorder() {
        border = new Rectangle(TILE_SIZE, TILE_SIZE, Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);
        border.setVisible(false);
        getChildren().add(border);
    }

    private void makeActions() {
        setOnMouseClicked((MouseEvent e) -> {
            ViewComManager.getInstance().requestBoardTileSelected(rowPos, colPos);
        });
        setOnMouseEntered((MouseEvent e) -> setBorderVisible(true));
        setOnMouseExited((MouseEvent e) -> setBorderVisible(false));
    }

    public void setBorderVisible(boolean visible) {
        border.setVisible(visible);
    }

    public void setBorderColor(Color color) {
        border.setStroke(color);
    }

    public void setOccupyingPiece(Piece newPiece) {
        //System.out.println("setOccupyingPiece in BoardTile at (" + rowPos + "|" + colPos + ") called");
        if (newPiece == null) {
            getChildren().remove(occupyingPiece);
            occupyingPiece = newPiece;
            return;
        }
        occupyingPiece = newPiece;
        getChildren().add(occupyingPiece);
        occupyingPiece.setLayoutX(/*getLayoutX() + */0.1 * TILE_SIZE);
        occupyingPiece.setLayoutY(/*getLayoutY() + */0.1 * TILE_SIZE);
    }

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    public int getRowPos() {
        return rowPos;
    }

    public int getColPos() {
        return colPos;
    }
}
