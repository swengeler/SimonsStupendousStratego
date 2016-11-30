package project.stratego.ui.components;

import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Piece extends Group {

    public static final double PIECE_SIZE = 0.8 * BoardTile.TILE_SIZE;

    private ImageView icon;
    private Rectangle background;
    private Image revealedImage;
    private Image hiddenImage;

    private Rectangle2D revealedViewPort;
    private Rectangle2D hiddenViewPort;

    public Piece(int playerIndex, int pieceIndex, Image pieceIcons, Image backsidePieceIcons) {
        super();
        icon = new ImageView();
        background = new Rectangle(PIECE_SIZE, PIECE_SIZE, (playerIndex == 0 ? Color.web("#48a4f9") : Color.web("#bf1c1c")));
        getChildren().addAll(background, icon);
        revealedViewPort = new Rectangle2D(pieceIndex * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        hiddenViewPort = new Rectangle2D(playerIndex * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        revealedImage = pieceIcons;
        hiddenImage = backsidePieceIcons;
        setToRevealedState();
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

    public void moveTo(BoardTile destTile) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), this);
        tt.setToX(destTile.getLayoutX());
        tt.setToY(destTile.getLayoutY());
    }

}
