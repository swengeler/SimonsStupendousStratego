package ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;

/**
 * Created by Simon on 17/09/2016.
 */
public class SinglePlayerMenu extends VBox {

    public SinglePlayerMenu() {
        super();
        createButtons();
    }

    private void createButtons() {
        Button backButton = new Button("Back", new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/back_icon.png"), 50, 50, true, true)));
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> {
            Main.getInstance().setView(0);
        });

        Button startButton = new Button("Start", new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/start_icon.png"), 50, 50, true, true)));
        startButton.getStyleClass().add("startButton");
        startButton.setOnAction((ActionEvent e) -> {
            Main.getInstance().setView(3);
        });

        this.getChildren().add(backButton);
        this.getChildren().add(startButton);

        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
    }

}
