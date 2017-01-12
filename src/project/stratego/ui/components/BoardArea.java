package project.stratego.ui.components;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import project.stratego.control.managers.ViewComManager;

public class BoardArea extends Pane {

    public static final double TILE_SPACING = 1;

    private Group northDeploymentArea;
    private Group southDeploymentArea;

    private BoardTile[][] board;
    private Piece[][] pieces;

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
        pieces = new Piece[10][10];

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
        if (pieces[row][col] == null) {
            //System.out.println("Piece at (" + row + "|" + col + "): " + board[row][col].getOccupyingPiece());
            pieces[row][col] = new Piece(playerIndex, pieceIndex, pieceIcons, backsidePieceIcons);
            pieces[row][col].setPosition(row, col);
            getChildren().add(pieces[row][col]);
            //System.out.println("After at (" + row + "|" + col + "): " + board[row][col].getOccupyingPiece() + " (" + playerIndex + ", " + pieceIndex + ")");
        }
    }

    public void move(int orRow, int orCol, int destRow, int destCol) {
        if (orRow != destRow || orCol != destCol) {
            System.out.println("(" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
            Piece movingPiece = pieces[orRow][orCol];
            pieces[orRow][orCol] = null;
            pieces[destRow][destCol] = movingPiece;
            movingPiece.setBoardPosition(destRow, destCol);

            int rowDiff = destRow - orRow;
            int colDiff = destCol - orCol;
            TranslateTransition move = new TranslateTransition(Duration.millis(300), movingPiece);
            move.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
            move.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
            move.setOnFinished(e -> {
                // call method for AI to make move
                ViewComManager.getInstance().requestNextMove();
            });
            move.play();

            //System.out.println("In BoardArea: (" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ")");
            /*
            System.out.println();
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if (pieces[row][col] != null) {
                        if (pieces[row][col].pieceIndex > 9) {
                            System.out.print(" " + pieces[row][col].pieceIndex);
                        } else {
                            System.out.print(" " + pieces[row][col].pieceIndex + " ");
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

    public void attackAndLose(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("(" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ") loses");
        int rowDiff = stopRow - orRow;
        int colDiff = stopCol - orCol;
        TranslateTransition move = new TranslateTransition(Duration.millis(rowDiff + colDiff == 0 ? 0 : 300), pieces[orRow][orCol]);
        move.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setOnFinished(e -> {
            revealPiece(orRow, orCol);
            revealPiece(destRow, destCol);
        });

        RotateTransition attack = new RotateTransition(Duration.millis(150), pieces[orRow][orCol]);
        attack.setByAngle(360);

        SequentialTransition transitions = new SequentialTransition(move, attack);
        transitions.setOnFinished(e -> {
            // call method to advance game/let AI make next move
            removePiece(orRow, orCol);
            ViewComManager.getInstance().requestNextMove();
        });
        transitions.play();
    }

    public void attackAndTie(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("(" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ") ties");
        int rowDiff = stopRow - orRow;
        int colDiff = stopCol - orCol;
        TranslateTransition move = new TranslateTransition(Duration.millis(rowDiff + colDiff == 0 ? 0 : 300), pieces[orRow][orCol]);
        move.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setOnFinished(e -> {
            revealPiece(orRow, orCol);
            revealPiece(destRow, destCol);
        });

        RotateTransition attack = new RotateTransition(Duration.millis(150), pieces[orRow][orCol]);
        attack.setByAngle(360);

        SequentialTransition transitions = new SequentialTransition(move, attack);
        transitions.setOnFinished(e -> {
            // call method to advance game/let AI make next move
            removePiece(orRow, orCol);
            removePiece(destRow, destCol);
            ViewComManager.getInstance().requestNextMove();
        });
        transitions.play();
    }

    public void attackAndWin(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        System.out.println("(" + orRow + "|" + orCol + ") to (" + destRow + "|" + destCol + ") wins");
        int rowDiff = stopRow - orRow;
        int colDiff = stopCol - orCol;
        TranslateTransition move = new TranslateTransition(Duration.millis(rowDiff + colDiff == 0 ? 0 : 300), pieces[orRow][orCol]);
        move.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        move.setOnFinished(e -> {
            revealPiece(orRow, orCol);
            revealPiece(destRow, destCol);
        });

        RotateTransition attack = new RotateTransition(Duration.millis(150), pieces[orRow][orCol]);
        attack.setByAngle(360);
        attack.setOnFinished(e -> removePiece(destRow, destCol));

        rowDiff = destRow - stopRow;
        colDiff = destCol - stopCol;
        TranslateTransition finish = new TranslateTransition(Duration.millis(150), pieces[orRow][orCol]);
        finish.setByX(colDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));
        finish.setByY(rowDiff * (BoardTile.TILE_SIZE + 2 * TILE_SPACING));

        SequentialTransition transitions = new SequentialTransition(move, attack, finish);
        transitions.setOnFinished(e -> {
            // call method to advance game/let AI make next move
            pieces[destRow][destCol] = pieces[orRow][orCol];
            pieces[destRow][destCol].setBoardPosition(destRow, destCol);
            pieces[orRow][orCol] = null;
            ViewComManager.getInstance().requestNextMove();
        });
        transitions.play();
    }

    public void resetDeployment(int playerIndex) {
        if (playerIndex == 0) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 10; col++) {
                    if (pieces[row][col] != null) {
                        getChildren().remove(pieces[row][col]);
                        pieces[row][col] = null;
                    }
                }
            }
        } else if (playerIndex == 1) {
            for (int row = 6; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if (pieces[row][col] != null) {
                        getChildren().remove(pieces[row][col]);
                        pieces[row][col] = null;
                    }
                }
            }
        }
    }

    public void reset() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (pieces[row][col] != null) {
                    getChildren().remove(pieces[row][col]);
                    pieces[row][col] = null;
                }
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
        if (pieces[row][col] != null) {
            pieces[row][col].setToHiddenState();
        }
    }

    public void revealPiece(int row, int col) {
        if (pieces[row][col] != null) {
            pieces[row][col].setToRevealedState();
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
        if (pieces[row][col] != null) {
            getChildren().remove(pieces[row][col]);
            pieces[row][col] = null;
        }
    }

    public double getSize() {
        return 10 * (BoardTile.TILE_SIZE + 2 * TILE_SPACING);
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void print() {
        System.out.println();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (pieces[row][col] != null) {
                    if (pieces[row][col].pieceIndex > 9) {
                        System.out.print(" " + pieces[row][col].pieceIndex);
                    } else {
                        System.out.print(" " + pieces[row][col].pieceIndex + " ");
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}