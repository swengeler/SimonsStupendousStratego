package project.stratego.control;

import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import project.stratego.control.managers.ViewComManager;

public class DebugHelper extends Stage {

    public DebugHelper() {
        setUp();
    }

    private void setUp() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        VBox buttons = new VBox();

        Button revealButton = new Button("Reveal all");
        revealButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().sendRevealAll());
        buttons.getChildren().add(revealButton);

        Button dummyButton1 = new Button("Dummy 1 with more text because reasons");
        buttons.getChildren().add(dummyButton1);

        Button dummyButton2 = new Button("Dummy 2");
        buttons.getChildren().add(dummyButton2);

        Button dummyButton3 = new Button("Dummy 3");
        buttons.getChildren().add(dummyButton3);

        Scene scene = new Scene(buttons);
        scene.getStylesheets().add("/menustyle.css");
        getIcons().add(new Image(getClass().getResourceAsStream("/icons/program_icon.png")));
        setScene(scene);
        setTitle("Debug Helper Window");
        setX(primaryScreenBounds.getMinX());
        setY(primaryScreenBounds.getMinY());
        show();
    }

}
