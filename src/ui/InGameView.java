package ui;

import game.Board;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Created by Simon on 17/09/2016.
 */
public class InGameView extends BorderPane {

    int activeTileX = -1;
    int activeTileY = -1;

    boolean circleSelected = false;

    double tileSize = Main.getInstance().getScene().getHeight() / Board.BOARD_SIZE;
    double yOffset = 0;
    double xOffset = (Main.getInstance().getScene().getWidth() - (tileSize * Board.BOARD_SIZE)) / 2;

    public InGameView() {
        super();
        drawBoard();
    }

    Pane board = new Pane();

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

        getChildren().add(board);

        for (int x = 0; x < Board.BOARD_SIZE; x++) {
            for (int y = 0; y < Board.BOARD_SIZE; y++) {

                // delegate the addition of new tiles to a method so that they can be added to a grid/array, thereby making it
                // possible to set pieces as their children
                if (y % 2 == 0) {
                    Rectangle temp = new Rectangle(2 + x * tileSize + xOffset, 2 + y * tileSize + yOffset, tileSize - 4, tileSize - 4);
                    //gc.setFill(x % 2 == 0 ? Color.BEIGE : Color.BURLYWOOD);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    getChildren().add(temp);
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    temp.getStyleClass().add(x % 2 == 0 ? "evenRectangle" : "oddRectangle");
                } else {
                    Rectangle temp = new Rectangle(2 + x * tileSize + xOffset, 2 + y * tileSize + yOffset, tileSize - 4, tileSize - 4);
                    //gc.setFill(x % 2 == 0 ? Color.BURLYWOOD : Color.BEIGE);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    getChildren().add(temp);
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    temp.getStyleClass().add(x % 2 == 0 ? "oddRectangle" : "evenRectangle");
                }
                //gc.fillRect(x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize);
                //pane.getChildren().add(temp);
                if (y % 2 == 0) {
                    Rectangle temp = new Rectangle(2 + x * tileSize + xOffset, 2 + y * tileSize + yOffset, tileSize - 4, tileSize - 4);
                    //gc.setFill(x % 2 == 0 ? Color.BEIGE : Color.BURLYWOOD);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    getChildren().add(temp);
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    temp.getStyleClass().add(x % 2 == 0 ? "evenRectangle" : "oddRectangle");
                } else {
                    Rectangle temp = new Rectangle(2 + x * tileSize + xOffset, 2 + y * tileSize + yOffset, tileSize - 4, tileSize - 4);
                    //gc.setFill(x % 2 == 0 ? Color.BURLYWOOD : Color.BEIGE);
                    temp.setOnMouseEntered((MouseEvent e) -> {
                        System.out.println("Mouse entered rectangle at (" + ((int) ((e.getX() - xOffset) / tileSize) + 1) + "|" + ((int) ((e.getY() - yOffset) / tileSize) + 1) + ")");
                        temp.setEffect(shadow);
                    });
                    getChildren().add(temp);
                    temp.setOnMouseExited((MouseEvent e) -> temp.setEffect(null));
                    temp.getStyleClass().add(x % 2 == 0 ? "oddRectangle" : "evenRectangle");
                }
            }
        }
        /*gc.setStroke(Color.RED);
        gc.setLineWidth(10);
        gc.strokeRect(100, 0, 500, 500);*/
        boolean tileActive = false;
        setOnMouseClicked((MouseEvent e) -> {
            if (e.getX() > xOffset && e.getX() < xOffset + Board.BOARD_SIZE * tileSize && e.getY() > yOffset && e.getY() < yOffset + Board.BOARD_SIZE * tileSize) {
                // check which tile was clicked
                int gridX = (int) ((e.getX() - xOffset) / tileSize);
                int gridY = (int) ((e.getY() - yOffset) / tileSize);

                doShit(gridX, gridY);
            }
        });
    }

    Circle circle;
    ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/spy_piece.png"), tileSize, tileSize, true, true));

    private void doShit(int gridX, int gridY) {
        if (activeTileX < 0) {
            //circle = new Circle((gridX + 0.5) * tileSize + xOffset, (gridY + 0.5) * tileSize + yOffset, tileSize / 5, Color.RED);
            //getChildren().add(circle);
            image.setX(gridX * tileSize + xOffset);
            image.setY(gridY * tileSize + yOffset);
            getChildren().add(image);
            System.out.println("Circle should be drawn at (" + gridX + "|" + gridY + ")");
            System.out.println(circle);
            activeTileX = gridX;
            activeTileY = gridY;
        } else {
            //circle.setCenterX((gridX + 0.5) * tileSize + xOffset);
            //circle.setCenterY((gridY + 0.5) * tileSize + yOffset);

            image.setX(gridX * tileSize + xOffset);
            image.setY(gridY * tileSize + yOffset);
            //board.getChildren().add(circle);
            activeTileX = gridX;
            activeTileY = gridY;
        }
    }

}
