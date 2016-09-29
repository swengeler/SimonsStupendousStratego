package project.stratego.ui.components;

import javafx.scene.Group;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardArea extends Pane {

    private Group northDeploymentArea;
    private Group southDeploymentArea;

    private BoardTile[][] board;

    private Image tileBackground;
    private Image pieceIcons;
    private Image backsidePieceIcons;

    public BoardArea(Image tileBackground, Image pieceIcons, Image backsidePieceIcons) {
        this.tileBackground = tileBackground;
        this.pieceIcons = pieceIcons;
        this.backsidePieceIcons = backsidePieceIcons;
        makeGrid();
    }

    private void makeGrid() {
        board = new BoardTile[10][10];

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                BoardTile temp = new BoardTile(row, col, tileBackground);
                temp.setLayoutX(col * (BoardTile.TILE_SIZE + 2) + 1);
                temp.setLayoutY(row * (BoardTile.TILE_SIZE + 2) + 1);
                getChildren().add(temp);

                board[row][col] = temp;

                /*if (row < 4) {
                    northDeploymentArea.getChildren().add(temp);
                } else if (row > 5) {
                    southDeploymentArea.getChildren().add(temp);
                } else {
                    getChildren().add(temp);
                }*/
            }
        }

        northDeploymentArea = new Group();
        southDeploymentArea = new Group();

        getChildren().addAll(northDeploymentArea, southDeploymentArea);
        //setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        DropShadow northHighlight = new DropShadow(100, Color.BLUE);
        northDeploymentArea.setEffect(northHighlight);
        DropShadow southHighlight = new DropShadow(100, Color.RED);
        southDeploymentArea.setEffect(southHighlight);
        /*setOnMouseClicked((MouseEvent e) -> {
            TranslateTransition t = new TranslateTransition (new Duration(200), this);
            t.setByX(-200);
            t.play();
        });*/
    }

    public void makePiece(int playerIndex, int pieceIndex, int row, int col) {
        //System.out.println("makePiece in BoardArea called");
        board[row][col].setOccupyingPiece(new Piece(playerIndex, pieceIndex, pieceIcons, backsidePieceIcons));
    }

    public void movePiece(int orRow, int orCol, int destRow, int destCol) {
        if (orRow != destRow || orCol != destCol) {
            Piece movingPiece = board[orRow][orCol].getOccupyingPiece();
            board[destRow][destCol].setOccupyingPiece(movingPiece);
            board[orRow][orCol].setOccupyingPiece(null);
        }
    }

    public void resetDeployment(int playerIndex) {
        if (playerIndex == 0) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 10; col++) {
                    board[row][col].setOccupyingPiece(null);
                }
            }
        } else if (playerIndex == 1) {
            for (int row = 6; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    board[row][col].setOccupyingPiece(null);
                }
            }
        }
    }

    public void hidePiece(int row, int col) {
        board[row][col].getOccupyingPiece().setToHiddenState();
    }

    public void revealPiece(int row, int col) {
        board[row][col].getOccupyingPiece().setToRevealedState();
    }

    public void removePiece(int row, int col) {
        board[row][col].setOccupyingPiece(null);
    }

    public void attackAnimation(int rowAttacking, int colAttacking, int rowDefending, int colDefending) {

    }

}