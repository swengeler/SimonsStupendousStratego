package project.stratego.ui.menus;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import project.stratego.control.CommunicationManager;

/**
 *
 */
public class SideMenu extends Pane {

    private Accordion menu;

    private TitledPane singlePlayerMenu;
    private TitledPane multiPlayerMenu;
    private TitledPane helpMenu;

    public SideMenu() {
        menu = new Accordion();
        makeSinglePlayerMenu();
        makeMultiPlayerMenu();
        makeHelpMenu();
        getChildren().add(menu);

        //menu.setStyle("-fx-border-color: black;");
        setStyle("-fx-background-color: #48a4f9;");
    }

    private void makeSinglePlayerMenu() {
        singlePlayerMenu = new TitledPane();
        singlePlayerMenu.setText("Singleplayer");
        singlePlayerMenu.setStyle("-fx-font: 22 arial;");

        Button b1 = new Button("Placeholder button 1");
        b1.setLayoutX(5);
        b1.setLayoutY(5);
        Scene snapScene = new Scene(b1);
        snapScene.snapshot(null);

        Button b2 = new Button("Placeholder button 2");
        b2.setLayoutX(5);
        b2.setLayoutY(b1.getHeight() + 10);
        snapScene = new Scene(b2);
        snapScene.snapshot(null);

        Button startButton = new Button("Start game");
        startButton.setLayoutX(5);
        startButton.setLayoutY(b1.getHeight() + b2.getHeight() + 15);

        // Pane pane = new Pane();
        // pane.getChildren().addAll(b1, b2, startButton);

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        pane.getChildren().addAll(b1, b2, startButton);

        singlePlayerMenu.setContent(pane);

        menu.getPanes().add(singlePlayerMenu);
    }

    private void makeMultiPlayerMenu() {
        multiPlayerMenu = new TitledPane();
        multiPlayerMenu.setText("Multiplayer");
        multiPlayerMenu.setStyle("-fx-font: 22 arial;");

        Button readyButton = new Button("Ready");
        readyButton.setLayoutX(5);
        readyButton.setLayoutY(5);
        readyButton.setOnAction((ActionEvent e) -> CommunicationManager.getInstance().sendPlayerReady());
        Scene snapScene = new Scene(readyButton);
        snapScene.snapshot(null);

        Button startButton = new Button("Start game");
        startButton.setLayoutX(5);
        startButton.setLayoutY(readyButton.getHeight() + 10);

        // Pane pane = new Pane();
        // pane.getChildren().addAll(b1, b2, startButton);

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        pane.getChildren().addAll(readyButton, startButton);

        multiPlayerMenu.setContent(pane);

        menu.getPanes().add(multiPlayerMenu);
    }

    private void makeHelpMenu() {
        helpMenu = new TitledPane();
        helpMenu.setText("Help");
        helpMenu.setStyle("-fx-font: 22 arial;");

        Label helpText = new Label(
            "The game Stratego is a complex strategy game created by our proud ancestors in ancient times. " +
            "Two players play until they capture the opponent's flag piece or until one of them cannot make any moves. " +
            "Now go out there and make your family proud, my child."
        );
        helpText.setWrapText(true);
        helpText.setPrefWidth(220);
        helpText.setStyle("-fx-font: 16 arial;");
        helpText.setPadding(new Insets(20));

        helpMenu.setContent(helpText);

        menu.getPanes().add(helpMenu);
    }

}
