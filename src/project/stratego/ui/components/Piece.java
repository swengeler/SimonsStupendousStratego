package project.stratego.ui.components;

import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Piece extends ImageView {

    public static final double PIECE_SIZE = 0.8 * BoardTile.TILE_SIZE;

    private Image revealedImage;
    private Image hiddenImage;

    private Rectangle2D revealedViewPort;
    private Rectangle2D hiddenViewPort;

    public Piece(int playerIndex, int pieceIndex, Image pieceIcons, Image backsidePieceIcons) {
        super();
        //revealedViewPort = new Rectangle2D((playerIndex * 12 + pieceIndex) * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        revealedViewPort = new Rectangle2D(pieceIndex * PIECE_SIZE, playerIndex * PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
        // maybe have a look at the line below, the F piece appears when using this
        //revealedViewPort = new Rectangle2D((playerIndex + pieceIndex) * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        hiddenViewPort = new Rectangle2D(playerIndex * PIECE_SIZE, 0, PIECE_SIZE, PIECE_SIZE);
        revealedImage = pieceIcons;
        hiddenImage = backsidePieceIcons;
        setToRevealedState();
    }

    public void setToRevealedState() {
        setImage(revealedImage);
        setViewport(revealedViewPort);
    }

    public void setToHiddenState() {
        setImage(hiddenImage);
        setViewport(hiddenViewPort);
    }

    public void moveTo(BoardTile destTile) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), this);
        tt.setToX(destTile.getLayoutX());
        tt.setToY(destTile.getLayoutY());
    }

}
