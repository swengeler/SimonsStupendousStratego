package project.stratego.ui.menus;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import project.stratego.ui.StrategoFrame;

/**
 * Created by Simon on 17/09/2016.
 */
public class MainMenu extends VBox {

    private StrategoFrame parent;

    public MainMenu(StrategoFrame parent) {
        super();
        this.parent = parent;
        createButtons();
    }

    private void createButtons() {
        Button singleButton = new Button("Singleplayer", new ImageView(new Image(getClass().getResourceAsStream("/singleplayer_icon.png"), 50, 50, true, true)));
        singleButton.setMinSize(350, 100);
        singleButton.getStyleClass().add("singleButton");
        singleButton.setOnAction((ActionEvent e) -> {
            parent.setToSinglePlayerMenu();
        });

        Button multiButton = new Button("Multiplayer", new ImageView(new Image(getClass().getResourceAsStream("/multiplayer_icon.png"), 50, 50, true, true)));
        multiButton.setMinSize(350, 100);
        multiButton.getStyleClass().add("multiButton");
        multiButton.setOnAction((ActionEvent e) -> {
            parent.setToMultiPlayerMenu();
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
