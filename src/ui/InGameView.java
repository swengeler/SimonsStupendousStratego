package ui;

import game.Board;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Simon on 17/09/2016.
 */
public class InGameView extends BorderPane {

    public InGameView() {
        super();
        drawBoard();
    }

    private void drawBoard() {

        VBox buttons = new VBox();
        Button backButton = new Button("Back", new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/back_icon.png"), 50, 50, true, true)));
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> {
            Main.getInstance().setView(1);
        });
        buttons.getChildren().add(backButton);
        //buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        this.setLeft(buttons);

        System.out.println("drawBoard called");

        double tileSize = Main.getInstance().getScene().getHeight() / Board.BOARD_SIZE;
        double yOffset = 0;
        double xOffset = (Main.getInstance().getScene().getWidth() - (tileSize * Board.BOARD_SIZE)) / 2;

        Canvas canvas = new Canvas(Main.getInstance().getScene().getWidth(), Main.getInstance().getScene().getWidth());
        //Canvas canvas = new Canvas(500, 500);
        System.out.println("Canvas width: " + canvas.getWidth() + ", canvas height: " + canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //getChildren().add(canvas);

        //Rectangle temp;
        /*temp = new Rectangle(500, 200, tileSize, tileSize);
        temp.setFill(Color.RED);
        getChildren().add(temp);*/

        InnerShadow shadow = new InnerShadow();

        for (int x = 0; x < Board.BOARD_SIZE; x++) {
            for (int y = 0; y < Board.BOARD_SIZE; y++) {
                if (y % 2 == 0) {
                    Rectangle temp = new Rectangle(x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize);
                    //gc.setFill(x % 2 == 0 ? Color.BEIGE : Color.BURLYWOOD);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    getChildren().add(temp);
                    temp.getStyleClass().add(x % 2 == 0 ? "evenRectangle" : "oddRectangle");
                } else {
                    Rectangle temp = new Rectangle(x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize);
                    //gc.setFill(x % 2 == 0 ? Color.BURLYWOOD : Color.BEIGE);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    getChildren().add(temp);
                    temp.getStyleClass().add(x % 2 == 0 ? "oddRectangle" : "evenRectangle");
                }
                //gc.fillRect(x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize);
                //pane.getChildren().add(temp);
            }
        }
        /*gc.setStroke(Color.RED);
        gc.setLineWidth(10);
        gc.strokeRect(100, 0, 500, 500);*/
    }

}
