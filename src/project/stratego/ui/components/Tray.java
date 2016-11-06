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
import project.stratego.control.managers.ViewComManager;

import java.util.ArrayList;

public class Tray extends Pane {

    private static TrayPiece lastActive;

    private ArrayList<TrayPiece> trayPieces;

    private double xOffset = 5;
    private double yOffset = 5;

    public Tray(Image pieceIcons) {
        makeIcons(pieceIcons);
    }

    private void makeIcons(Image pieceIcons) {
        trayPieces = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            TrayPiece temp = new TrayPiece(i, pieceIcons);
            getChildren().add(temp);
            trayPieces.add(temp);
        }
    }

    public void setPlayerIndex(int playerIndex) {
        for (Node n : getChildren()) {
            ((TrayPiece) n).setPlayer(playerIndex);
        }
    }

    public void setActive(int pieceIndex) {
        if (pieceIndex == -1) {
            lastActive.setActive(false);
            lastActive = null;
        } else {
            lastActive = trayPieces.get(pieceIndex);
            lastActive.setActive(true);
        }
    }

    class TrayPiece extends Group {

        private final int pieceIndex;

        private ImageView icon;
        private Rectangle background;

        private boolean active;

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
            //background = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, (playerIndex == 0 ? Color.web("#48a4f9") : Color.web("#bf1c1c")));
            background = new Rectangle(Piece.PIECE_SIZE, Piece.PIECE_SIZE, Color.web("#bcbcbc"));
            background.setStrokeWidth(2);
            background.setStroke(Color.BLACK);
            getChildren().add(background);

            setOnMouseEntered((MouseEvent e) -> background.setStroke(Color.WHITE));
            setOnMouseExited((MouseEvent e) -> {
                if (!active) {
                    background.setStroke(Color.BLACK);
                }
            });
        }

        public void setPlayer(int playerIndex) {
            if (playerIndex == 0) {
                background.setFill(Color.web("#48a4f9"));
                setOnMouseClicked((MouseEvent e) -> {
                    ViewComManager.getInstance().requestTrayPieceSelected(pieceIndex);
                });
            } else if (playerIndex == 1) {
                background.setFill(Color.web("#bf1c1c"));
                setOnMouseClicked((MouseEvent e) -> {
                    ViewComManager.getInstance().requestTrayPieceSelected(pieceIndex);
                });
            } else {
                background.setFill(Color.web("#bcbcbc"));
                setOnMouseClicked(null);
            }
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

}
