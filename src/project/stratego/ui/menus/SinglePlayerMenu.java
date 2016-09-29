package project.stratego.ui.menus;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import project.stratego.ui.StrategoFrame;

public class SinglePlayerMenu extends VBox {

    private StrategoFrame parent;

    public SinglePlayerMenu(StrategoFrame parent) {
        super();
        this.parent = parent;
        createButtons();
    }

    private void createButtons() {
        Button backButton = new Button("Back", new ImageView(new Image(getClass().getResourceAsStream("/back_icon.png"), 50, 50, true, true)));
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction((ActionEvent e) -> {
            parent.setToMainMenu();
        });

        Button startButton = new Button("Start", new ImageView(new Image(getClass().getResourceAsStream("/start_icon.png"), 50, 50, true, true)));
        startButton.getStyleClass().add("startButton");
        startButton.setOnAction((ActionEvent e) -> {
            parent.setToInGameView();
        });

        this.getChildren().add(backButton);
        this.getChildren().add(startButton);

        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
    }

}
