package project.stratego.ui.components;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import project.stratego.ui.StrategoFrame;

public class Tray extends Pane {

    private int playerIndex;

    private double xOffset = 5;
    private double yOffset = 5;

    public Tray(int playerIndex, Image pieceIcons) {
        this.playerIndex = playerIndex;
        makeIcons(pieceIcons);
    }

    private void makeIcons(Image pieceIcons) {
        class TrayPiece extends ImageView {

            private Rectangle border;

            private TrayPiece(int playerIndex, int pieceIndex, Image pieceIcons) {
                super();
                cutAndSetImage(pieceIndex, pieceIcons);
                setLayoutX(xOffset);
                setLayoutY(pieceIndex * (Piece.PIECE_SIZE + yOffset) + yOffset);
                setOnMouseClicked((MouseEvent e) -> {
                    StrategoFrame.getInstance().getComManager().sendTrayPieceSelected(playerIndex, pieceIndex);
                });
                //System.out.println("image with overall index = " + ((playerIndex * 12) + pieceIndex) + " placed at (" + getLayoutX() + "|" + getLayoutY() + ")");
                makeBorder();
            }

            private void cutAndSetImage(int pieceIndex, Image pieceIcons) {
                //Rectangle2D viewPort = new Rectangle2D(((playerIndex * 12) + pieceIndex) * Piece.PIECE_SIZE, 0, Piece.PIECE_SIZE, Piece.PIECE_SIZE);
                Rectangle2D viewPort = new Rectangle2D(pieceIndex * Piece.PIECE_SIZE, playerIndex * Piece.PIECE_SIZE, Piece.PIECE_SIZE, Piece.PIECE_SIZE);
                //System.out.println("New viewport rectangle: (" + viewPort.getMinX() + "|" + viewPort.getMinY() + "|" + viewPort.getWidth() + "|" + viewPort.getHeight() + ")");
                setImage(pieceIcons);
                setViewport(viewPort);
            }

            public void makeBorder() {
                border = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, Color.TRANSPARENT);
                border.setLayoutX(getLayoutX());
                border.setLayoutY(getLayoutY());
                border.setStroke(Color.WHITE);
                border.setStrokeWidth(3);
                border.setVisible(false);
                getChildren().add(border);

                setOnMouseEntered((MouseEvent e) -> border.setVisible(true));
                setOnMouseExited((MouseEvent e) -> border.setVisible(false));
            }
        }

        for (int i = 0; i < 12; i++) {
            //System.out.println("combined index = " + ((playerIndex * 12) + i));
            TrayPiece temp = new TrayPiece(playerIndex, i, pieceIcons);
            //Rectangle2D viewPort = new Rectangle2D(((playerIndex * 12) + index) * Piece.PIECE_SIZE, 0, Piece.PIECE_SIZE, Piece.PIECE_SIZE);
            //temp.setImage(pieceIcons);
            //temp.setViewport(viewPort);
            //temp.setFitWidth(0.75 * Piece.PIECE_SIZE);
            //temp.setFitHeight(0.75 * Piece.PIECE_SIZE);
            getChildren().add(temp);
        }
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

}
