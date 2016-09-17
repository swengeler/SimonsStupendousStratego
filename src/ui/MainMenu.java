package ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Created by Simon on 17/09/2016.
 */
public class MainMenu extends VBox {

    public MainMenu() {
        super();
        createButtons();
    }

    private void createButtons() {
        Button singleButton = new Button("Singleplayer", new ImageView(new Image(getClass().getResourceAsStream("/singleplayer_icon.png"), 50, 50, true, true)));
        singleButton.setMinSize(350, 100);
        singleButton.getStyleClass().add("singleButton");
        singleButton.setOnAction((ActionEvent e) -> {
            Main.getInstance().setView(1);
        });

        Button multiButton = new Button("Multiplayer", new ImageView(new Image(getClass().getResourceAsStream("/multiplayer_icon.png"), 50, 50, true, true)));
        multiButton.setMinSize(350, 100);
        multiButton.getStyleClass().add("multiButton");
        multiButton.setOnAction((ActionEvent e) -> {
            Main.getInstance().setView(2);
        });

        Button helpButton = new Button("Help", new ImageView(new Image(getClass().getResourceAsStream("/help_icon.png"), 50, 50, true, true)));
        helpButton.setMinSize(350, 100);
        helpButton.getStyleClass().add("helpButton");

        this.getChildren().add(singleButton);
        this.getChildren().add(multiButton);
        this.getChildren().add(helpButton);

        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
    }

}
