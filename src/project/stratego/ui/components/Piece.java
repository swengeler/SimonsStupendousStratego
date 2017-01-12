package project.stratego.ui.components;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import project.stratego.control.managers.ViewComManager;

import static project.stratego.ui.components.BoardArea.TILE_SPACING;

public class Piece extends Group {

    public static final double PIECE_SIZE = 0.8 * BoardTile.TILE_SIZE;

    private ImageView icon;
    private Rectangle background;
    private Image revealedImage;
    private Image hiddenImage;

    private Rectangle2D revealedViewPort;
    private Rectangle2D hiddenViewPort;
    private Rectangle border;

    public int pieceIndex;

    private int rowPos, colPos;

    public Piece(int playerIndex, int pieceIndex, Image pieceIcons, Image backsidePieceIcons) {
        super();
        this.pieceIndex = pieceIndex;
        icon = new ImageView();
        background = new Rectangle(PIECE_SIZE, PIECE_SIZE, (playerIndex == 0 ? Color.web("#48a4f9") : Color.web("#bf1c1c")));
        getChildren().addAll(background, icon);
        revealedViewPort = new Rectangle2D(pieceIndex * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        hiddenViewPort = new Rectangle2D(playerIndex * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        revealedImage = pieceIcons;
        hiddenImage = backsidePieceIcons;
        setToRevealedState();
        makeBorder();
        makeActions();
    }

    public void setToRevealedState() {
        //System.out.println("Piece revealed");
        icon.setImage(revealedImage);
        icon.setViewport(revealedViewPort);
    }

    public void setToHiddenState() {
        icon.setImage(hiddenImage);
        icon.setViewport(hiddenViewPort);
    }

    public void setPosition(int rowPos, int colPos) {
        this.rowPos = rowPos;
        this.colPos = colPos;
        setLayoutX(colPos * (BoardTile.TILE_SIZE + 2 * TILE_SPACING) + TILE_SPACING + 0.1 * BoardTile.TILE_SIZE);
        setLayoutY(rowPos * (BoardTile.TILE_SIZE + 2 * TILE_SPACING) + TILE_SPACING + 0.1 * BoardTile.TILE_SIZE);
    }

    public void setBoardPosition(int rowPos, int colPos) {
        this.rowPos = rowPos;
        this.colPos = colPos;
    }

    private void makeBorder() {
        border = new Rectangle(BoardTile.TILE_SIZE, BoardTile.TILE_SIZE, Color.TRANSPARENT);
        border.setLayoutX(-0.1 * BoardTile.TILE_SIZE);
        border.setLayoutY(-0.1 * BoardTile.TILE_SIZE);
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

    private void setBorderVisible(boolean visible) {
        border.setVisible(visible);
    }

    public void moveTo(int destRow, int destCol) {
        int rowDiff = destRow - rowPos;
        int colDiff = destCol - colPos;
        rowPos = destRow;
        colPos = destCol;
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), this);
        tt.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        tt.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        tt.play();
    }

}
