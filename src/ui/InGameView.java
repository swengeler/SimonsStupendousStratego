package ui;

import game.ModelBoard;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

/**
 * Created by Simon on 17/09/2016.
 */
public class InGameView extends Pane {

    private StrategoFrame parent;

    private Board board;
    private Tray trayNorth, traySouth;
    private Button backButton, switchButton;

    double tileSize = StrategoFrame.FRAME_HEIGHT / (ModelBoard.BOARD_SIZE * 1.2);
    double yOffset = (StrategoFrame.FRAME_HEIGHT - (tileSize * ModelBoard.BOARD_SIZE)) / 2;
    double xOffset = (StrategoFrame.FRAME_WIDTH - (tileSize * ModelBoard.BOARD_SIZE)) / 2;

    public InGameView(StrategoFrame parent) {
        super();
        this.parent = parent;
        //drawHelpGrid(100);
        //prepareBoardTest2();
        makeComponents();
        placeComponents();
    }

    private void makeComponents() {

        Background b = new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/frame_background.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));
        setBackground(b);

        board = new Board(tileSize);
        trayNorth = new Tray("blue");
        traySouth = new Tray("red");

        backButton = new Button("Back");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> parent.setToSinglePlayerMenu());
        switchButton = new Button("Switch player");
        switchButton.getStyleClass().add("switchButton");
        switchButton.setOnAction((ActionEvent e) -> parent.getComManager().sendPlayerSwitch());

        getChildren().addAll(board, trayNorth, traySouth, backButton, switchButton);
    }

    private void placeComponents() {
        board.setLayoutX(0);
    }

    public void makeButtons() {
        Button backButton = new Button("Back", new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/back_icon.png"), 50, 50, true, true)));
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> parent.setToSinglePlayerMenu());
        backButton.setLayoutX(1000);
        backButton.setLayoutY(200);

        Button switchButton = new Button("Switch");
        switchButton.getStyleClass().add("backButton");
        switchButton.setLayoutX(1000);
        switchButton.setLayoutY(300);

        getChildren().addAll(backButton, switchButton);
    }

    public void drawHelpGrid(double size) {
        if (size > StrategoFrame.FRAME_WIDTH) {
            return;
        }
        if (size < 20)
            size = 20;

        int rows = (int) (StrategoFrame.FRAME_HEIGHT / size);
        int columns = (int) (StrategoFrame.FRAME_WIDTH / size);
        Line temp;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                temp = new Line(j * size, 0, j * size, StrategoFrame.FRAME_HEIGHT);
                getChildren().add(temp);
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                temp = new Line(0, i * size, StrategoFrame.FRAME_WIDTH, i * size);
                getChildren().add(temp);
            }
        }
    }

    /*private void prepareBoardTest() {
        StackPane temp1;
        Rectangle temp;

        setVgap(2);
        setHgap(2);

        add(new Button("Eyo"), 0, 0, 1, 1);

        for (int row = 0; row < ModelBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < ModelBoard.BOARD_SIZE; col++) {
                if (col % 2 == 0) {
                    temp1 = new StackPane();
                    temp = new Rectangle(tileSize, tileSize);
                    temp.getStyleClass().add(row % 2 == 0 ? "evenRectangle" : "oddRectangle");
                    temp1.getChildren().add(temp);
                    add(temp1, col + 1, row, 1, 1);
                } else {
                    temp1 = new StackPane();
                    temp = new Rectangle(tileSize, tileSize);
                    temp.getStyleClass().add(row % 2 == 0 ? "oddRectangle" : "evenRectangle");
                    temp1.getChildren().add(temp);
                    add(temp1, col + 1, row, 1, 1);
                }
            }
        }

        setGridLinesVisible(true);

    }*/

    private void prepareBoardTest2() {
        StackPane temp1;
        Rectangle temp;
        for (int row = 0; row < ModelBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < ModelBoard.BOARD_SIZE; col++) {
                if (col % 2 == 0) {
                    temp = new Rectangle(row * tileSize + xOffset + 2, col * tileSize + yOffset + 2, tileSize - 4, tileSize - 4);
                    temp.getStyleClass().add(row % 2 == 0 ? "evenRectangle" : "oddRectangle");
                    getChildren().add(temp);
                } else {
                    temp = new Rectangle(row * tileSize + xOffset + 2, col * tileSize + yOffset + 2, tileSize - 4, tileSize - 4);
                    temp.getStyleClass().add(row % 2 == 0 ? "oddRectangle" : "evenRectangle");
                    getChildren().add(temp);
                }
            }
        }
    }

    private void prepareBoard() {
        VBox buttons = new VBox();
        Button backButton = new Button("Back", new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/back_icon.png"), 50, 50, true, true)));
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> {
            parent.setToSinglePlayerMenu();
        });
        buttons.getChildren().add(backButton);
        //buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        //this.setLeft(buttons);

        System.out.println("drawBoard called");



        Canvas canvas = new Canvas(StrategoFrame.FRAME_WIDTH, StrategoFrame.FRAME_HEIGHT);
        //Canvas canvas = new Canvas(500, 500);
        System.out.println("Canvas width: " + canvas.getWidth() + ", canvas height: " + canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //getChildren().add(canvas);

        //Rectangle temp;
        /*temp = new Rectangle(500, 200, tileSize, tileSize);
        temp.setFill(Color.RED);
        getChildren().add(temp);*/

        InnerShadow shadow = new InnerShadow();

        for (int x = 0; x < ModelBoard.BOARD_SIZE; x++) {
            for (int y = 0; y < ModelBoard.BOARD_SIZE; y++) {

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
            if (e.getX() > xOffset && e.getX() < xOffset + ModelBoard.BOARD_SIZE * tileSize && e.getY() > yOffset && e.getY() < yOffset + ModelBoard.BOARD_SIZE * tileSize) {
                // check which tile was clicked
                int gridX = (int) ((e.getX() - xOffset) / tileSize);
                int gridY = (int) ((e.getY() - yOffset) / tileSize);

            }
        });
    }

    Circle circle;
    ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/spy_piece.png"), tileSize, tileSize, true, true));


}
