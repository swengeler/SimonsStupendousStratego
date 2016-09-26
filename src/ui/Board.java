package ui;

import game.ModelBoard;
import javafx.scene.Group;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
public class Board extends Group {

    private StrategoFrame parent;

    private Group northDeploymentArea;
    private Group southDeploymentArea;

    private Effect hoverEffect;

    private Rectangle activeTile;

    private double tileSize;

    public Board(StrategoFrame parent, double tileSize) {
        this.tileSize = tileSize;
        this.parent = parent;
        makeGrid();
    }

    private void makeGrid() {
        hoverEffect = new InnerShadow();

        northDeploymentArea = new Group();
        southDeploymentArea = new Group();

        getChildren().addAll(northDeploymentArea, southDeploymentArea);

        Image background = new Image(getClass().getResourceAsStream("/icons/board_background.png"), 10 * BoardTile.TILE_SIZE, 10 * BoardTile.TILE_SIZE, true, true);

        for (int row = 0; row < ModelBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < ModelBoard.BOARD_SIZE; col++) {
                BoardTile temp = new BoardTile(row, col, background);
                temp.setLayoutX(col * (BoardTile.TILE_SIZE + 4) + 2);
                temp.setLayoutY(row * (BoardTile.TILE_SIZE + 4) + 2);
                temp.setOnMouseClicked((MouseEvent e) -> {
                    temp.setEffect(new InnerShadow(10, Color.WHITE));
                });
                temp.setOnMouseEntered((MouseEvent e) -> temp.setEffect(hoverEffect));

                getChildren().add(temp);

                /*if (row < 4) {
                    northDeploymentArea.getChildren().add(temp);
                } else if (row > 5) {
                    southDeploymentArea.getChildren().add(temp);
                } else {
                    getChildren().add(temp);
                }*/
            }

        }

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

    public void setHoverEffect(Effect hoverEffect) {
        this.hoverEffect = hoverEffect;
    }

}