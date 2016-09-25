package ui;

import game.ModelBoard;
import javafx.scene.Group;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
public class Board extends Group {

    private Effect hoverEffect;

    private Rectangle activeTile;

    private double tileSize;

    public Board(double tileSize) {
        this.tileSize = tileSize;
        makeGrid();
    }

    private void makeGrid() {
        hoverEffect = new InnerShadow();

        for (int row = 0; row < ModelBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < ModelBoard.BOARD_SIZE; col++) {
                if (col % 2 == 0) {
                    Rectangle temp = new Rectangle(row * tileSize + 2, col * tileSize + 2, tileSize - 4, tileSize - 4);
                    temp.getStyleClass().add("evenRectangle");
                    temp.setOnMouseClicked((MouseEvent e) -> {
                        activeTile = temp;
                        temp.setEffect(new InnerShadow(10, Color.WHITE));
                    });
                    temp.setOnMouseEntered((MouseEvent e) -> temp.setEffect(hoverEffect));
                    temp.setOnMouseExited((MouseEvent e) -> {
                        if (temp != activeTile)
                            temp.setEffect(null);
                    });
                    getChildren().add(temp);
                } else {
                    Rectangle temp = new Rectangle(row * tileSize + 2, col * tileSize + 2, tileSize - 4, tileSize - 4);
                    temp.getStyleClass().add("evenRectangle");
                    temp.setOnMouseClicked((MouseEvent e) -> {
                        activeTile = temp;
                        temp.setEffect(new InnerShadow(10, Color.WHITE));
                    });
                    temp.setOnMouseEntered((MouseEvent e) -> temp.setEffect(hoverEffect));
                    temp.setOnMouseExited((MouseEvent e) -> {
                        if (temp != activeTile)
                            temp.setEffect(null);
                    });
                    getChildren().add(temp);
                }
            }
        }

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