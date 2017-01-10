package project.stratego.ui.components;

import javafx.scene.Group;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BoardArea extends Pane {

    private static final double TILE_SPACING = 1;

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

        northDeploymentArea = new Group();
        southDeploymentArea = new Group();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                BoardTile temp = new BoardTile(row, col, tileBackground);
                temp.setLayoutX(col * (BoardTile.TILE_SIZE + 2 * TILE_SPACING) + TILE_SPACING);
                temp.setLayoutY(row * (BoardTile.TILE_SIZE + 2 * TILE_SPACING) + TILE_SPACING);
                if ((row == 4 || row == 5) && (col == 2 || col == 3 || col == 6 || col == 7)) {
                    temp.setBorderColor(Color.TRANSPARENT);
                }
                //getChildren().add(temp);

                board[row][col] = temp;


                if (row < 4) {
                    northDeploymentArea.getChildren().add(temp);
                } else if (row > 5) {
                    southDeploymentArea.getChildren().add(temp);
                } else {
                    getChildren().add(temp);
                }
            }
        }

        getChildren().addAll(northDeploymentArea, southDeploymentArea);
    }

    public void makePiece(int playerIndex, int pieceIndex, int row, int col) {
        if (board[row][col].getOccupyingPiece() == null) {
            //System.out.println("Piece at (" + row + "|" + col + "): " + board[row][col].getOccupyingPiece());
            board[row][col].setOccupyingPiece(new Piece(playerIndex, pieceIndex, pieceIcons, backsidePieceIcons));
            //System.out.println("After at (" + row + "|" + col + "): " + board[row][col].getOccupyingPiece() + " (" + playerIndex + ", " + pieceIndex + ")");
        }
    }

    public void movePiece(int orRow, int orCol, int destRow, int destCol) {
        if (orRow != destRow || orCol != destCol) {
            Piece movingPiece = board[orRow][orCol].getOccupyingPiece();
            board[destRow][destCol].setOccupyingPiece(movingPiece);
            board[orRow][orCol].setOccupyingPiece(null);

            System.out.println("In BoardArea: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");

            /*System.out.println();
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if (board[row][col].getOccupyingPiece() != null) {
                        if (board[row][col].getOccupyingPiece().pieceIndex > 9) {
                            System.out.print(" " + board[row][col].getOccupyingPiece().pieceIndex);
                        } else {
                            System.out.print(" " + board[row][col].getOccupyingPiece().pieceIndex + " ");
                        }
                    } else {
                        System.out.print("   ");
                    }
                }
                System.out.println();
            }
            System.out.println();*/
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

    public void reset() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                board[row][col].setOccupyingPiece(null);
            }
        }
    }

    public void highlightDeploymentArea(int playerIndex, boolean highlight) {
        if (highlight) {
            if (playerIndex == 0) {
                northDeploymentArea.setEffect(new DropShadow(10, Color.WHITE));
            } else if (playerIndex == 1) {
                southDeploymentArea.setEffect(new DropShadow(10, Color.WHITE));
            }
        } else {
            if (playerIndex == 0) {
                northDeploymentArea.setEffect(null);
            } else if (playerIndex == 1) {
                southDeploymentArea.setEffect(null);
            }
        }
    }

    public void hidePiece(int row, int col) {
        board[row][col].getOccupyingPiece().setToHiddenState();
    }

    public void revealPiece(int row, int col) {
        if (board[row][col].getOccupyingPiece() != null) {
            board[row][col].getOccupyingPiece().setToRevealedState();
        }
    }

    public void revealAll() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                revealPiece(row, col);
            }
        }
    }

    public void removePiece(int row, int col) {
        board[row][col].setOccupyingPiece(null);
    }

    public void attackAnimation(int rowAttacking, int colAttacking, int rowDefending, int colDefending) {

    }

    public double getSize() {
        return 10 * (BoardTile.TILE_SIZE + 2 * TILE_SPACING);
    }

}