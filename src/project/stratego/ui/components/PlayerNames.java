package project.stratego.ui.components;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class PlayerNames extends Group {

    private static final String NORTH_DEFAULT_NAME = "Emperor of the North";
    private static final String SOUTH_DEFAULT_NAME = "Emperor of the South";

    private Text northPlayerName, southPlayerName;
    private TextFlow northContainer, southContainer;

    private DropShadow highlightEffect;

    public PlayerNames(double boardSize) {
        setUp(boardSize);
    }

    private void setUp(double boardSize) {
        northContainer = new TextFlow();
        northContainer.setTextAlignment(TextAlignment.CENTER);
        northContainer.setMinWidth(boardSize);
        northContainer.setPrefWidth(boardSize);
        //northContainer.setStyle("-fx-border-color: black");

        northPlayerName = new Text(NORTH_DEFAULT_NAME);
        northPlayerName.setFont(Font.font("Helvetica", FontWeight.BOLD, 40));
        northPlayerName.setFill(Color.web("#48a4f9"));
        northPlayerName.setStroke(Color.WHITE);
        northPlayerName.setStrokeWidth(1);
        northContainer.getChildren().add(northPlayerName);

        southContainer = new TextFlow();
        southContainer.setTextAlignment(TextAlignment.CENTER);
        southContainer.setMinWidth(boardSize);
        southContainer.setPrefWidth(boardSize);
        //southContainer.setStyle("-fx-border-color: black");

        southPlayerName = new Text(SOUTH_DEFAULT_NAME);
        southPlayerName.setFont(Font.font("Helvetica", FontWeight.BOLD, 40));
        southPlayerName.setFill(Color.web("#bf1c1c"));
        southPlayerName.setStroke(Color.WHITE);
        southPlayerName.setStrokeWidth(1);
        southContainer.getChildren().add(southPlayerName);
        southContainer.setLayoutY(boardSize + 55);

        getChildren().addAll(northContainer, southContainer);

        highlightEffect = new DropShadow(40, Color.WHITE);
    }

    public void highlightPlayerName(int playerIndex) {
        if (playerIndex == -1 || playerIndex == 0) {
            southPlayerName.setEffect(null);
            northPlayerName.setEffect(highlightEffect);
        } else if (playerIndex == 1) {
            northPlayerName.setEffect(null);
            southPlayerName.setEffect(highlightEffect);
        }
    }

    public void resetHighlight() {
        northPlayerName.setEffect(null);
        southPlayerName.setEffect(null);
    }

    public void resetNames() {
        northPlayerName.setText(NORTH_DEFAULT_NAME);
        southPlayerName.setText(SOUTH_DEFAULT_NAME);
    }

    public void setPlayerName(int playerIndex, String newName) {
        if (playerIndex == -1 || playerIndex == 0) {
            northPlayerName.setText(newName);
        } else if (playerIndex == 1) {
            southPlayerName.setText(newName);
        }
    }

}
