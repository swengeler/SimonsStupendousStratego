package ui;

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
        Button singleButton = new Button("Singleplayer", new ImageView(new Image(getClass().getResourceAsStream("/singleplayer_icon.png"), 30, 30, true, true)));
        Button multiButton = new Button("Multiplayer", new ImageView(new Image(getClass().getResourceAsStream("/multiplayer_icon.png"), 30, 30, true, true)));
        Button helpButton = new Button("Help", new ImageView(new Image(getClass().getResourceAsStream("/help_icon.png"), 30, 30, true, true)));

        this.getChildren().add(singleButton);
        this.getChildren().add(multiButton);
        this.getChildren().add(helpButton);

        this.setSpacing(8);
        this.setAlignment(Pos.CENTER);
    }

}
