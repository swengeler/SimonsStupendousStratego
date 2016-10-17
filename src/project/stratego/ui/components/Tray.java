package project.stratego.ui.components;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import project.stratego.control.*;

public class Tray extends Pane {

    private double xOffset = 5;
    private double yOffset = 5;

    public Tray(Image pieceIcons) {
        makeIcons(pieceIcons);
    }

    private void makeIcons(Image pieceIcons) {
        for (int i = 0; i < 12; i++) {
            TrayPiece temp = new TrayPiece(i, pieceIcons);
            getChildren().add(temp);
        }
    }

    public void setPlayerIndex(int playerIndex) {
        for (Node n : getChildren()) {
            ((TrayPiece) n).setPlayer(playerIndex);
        }
    }

    class TrayPiece extends Group {

        private final int pieceIndex;

        private ImageView icon;
        private Rectangle border;

        private TrayPiece(int pieceIndex, Image pieceIcons) {
            super();
            this.pieceIndex = pieceIndex;
            makeBackGroundAndBorder();
            cutAndSetImage(pieceIcons);
            setLayoutX(xOffset);
            setLayoutY(pieceIndex * (Piece.PIECE_SIZE + yOffset) + yOffset);
        }

        private void cutAndSetImage(Image pieceIcons) {
            Rectangle2D viewPort = new Rectangle2D(pieceIndex * Piece.PIECE_SIZE, 0, Piece.PIECE_SIZE, Piece.PIECE_SIZE);
            icon = new ImageView();
            icon.setImage(pieceIcons);
            icon.setViewport(viewPort);
            getChildren().add(icon);
        }

        public void makeBackGroundAndBorder() {
            //border = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, (playerIndex == 0 ? Color.web("#48a4f9") : Color.web("#bf1c1c")));
            border = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, Color.web("#bcbcbc"));
            border.setStrokeWidth(2);
            getChildren().add(border);

            setOnMouseEntered((MouseEvent e) -> border.setStroke(Color.WHITE));
            setOnMouseExited((MouseEvent e) -> border.setStroke(Color.TRANSPARENT));
        }

        public void setPlayer(int playerIndex) {
            if (playerIndex == 0) {
                border.setFill(Color.web("#48a4f9"));
            } else if (playerIndex == 1) {
                border.setFill(Color.web("#bf1c1c"));
            }
            setOnMouseClicked((MouseEvent e) -> {
                ((ViewComManager) ManagerManager.getViewReceiver()).getInstance().requestTrayPieceSelected(playerIndex, pieceIndex);
            });
        }

    }

}
