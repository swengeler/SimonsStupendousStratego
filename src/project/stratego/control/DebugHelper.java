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

import javax.swing.*;

public class DebugHelper extends Stage {

    public DebugHelper() {
        setUp();
    }

    private void setUp() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        VBox buttons = new VBox();

        Button revealButton = new Button("Reveal all");
        revealButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().revealAll());
        buttons.getChildren().add(revealButton);

        Button hideButton = new Button("Dummy 0.1");
        buttons.getChildren().add(hideButton);

        Button dummyButton2 = new Button("Dummy 1 with more text because reasons");
        buttons.getChildren().add(dummyButton2);

        Button dummyButton3 = new Button("Dummy 2");
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
