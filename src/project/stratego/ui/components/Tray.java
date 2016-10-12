package project.stratego.ui.components;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import project.stratego.control.CommunicationManager;
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
        class TrayPiece extends Group {

            private ImageView icon;
            private Rectangle border;

            private TrayPiece(int playerIndex, int pieceIndex, Image pieceIcons) {
                super();
                makeBackGroundAndBorder(playerIndex);
                cutAndSetImage(pieceIndex, pieceIcons);
                setLayoutX(xOffset);
                setLayoutY(pieceIndex * (Piece.PIECE_SIZE + yOffset) + yOffset);
                setOnMouseClicked((MouseEvent e) -> {
                    CommunicationManager.getInstance().sendTrayPieceSelected(playerIndex, pieceIndex);
                });
            }

            private void cutAndSetImage(int pieceIndex, Image pieceIcons) {
                Rectangle2D viewPort = new Rectangle2D(pieceIndex * Piece.PIECE_SIZE, 0, Piece.PIECE_SIZE, Piece.PIECE_SIZE);
                icon = new ImageView();
                icon.setImage(pieceIcons);
                icon.setViewport(viewPort);
                getChildren().add(icon);
            }

            public void makeBackGroundAndBorder(int playerIndex) {
                border = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, (playerIndex == 0 ? Color.web("#48a4f9") : Color.web("#bf1c1c")));
                border.setStrokeWidth(2);
                getChildren().add(border);

                setOnMouseEntered((MouseEvent e) -> border.setStroke(Color.WHITE));
                setOnMouseExited((MouseEvent e) -> border.setStroke(Color.TRANSPARENT));
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
