package project.stratego.ui.menus;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import project.stratego.control.*;
import project.stratego.ui.StrategoFrame;

public class SideMenu extends Pane {

    private StrategoFrame parent;

    private Accordion menu;

    private TitledPane singlePlayerMenu;
    private TitledPane multiPlayerMenu;
    private TitledPane helpMenu;

    public SideMenu(StrategoFrame parent) {
        this.parent = parent;
        menu = new Accordion();
        makeSinglePlayerMenu();
        makeMultiPlayerMenu();
        makeHelpMenu();
        configureChangeListener();
        getChildren().add(menu);

        //menu.setStyle("-fx-border-color: black;");
        //setStyle("-fx-background-color: #48a4f9;");
        setStyle("-fx-background-color: transparent;");
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
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(b1, b2, startButton);

        singlePlayerMenu.setContent(pane);

        menu.getPanes().add(singlePlayerMenu);
    }

    private void makeMultiPlayerMenu() {
        multiPlayerMenu = new TitledPane();
        multiPlayerMenu.setText("Multiplayer");
        multiPlayerMenu.setStyle("-fx-font: 22 arial;");

        Button readyButton = new Button("Ready");
        readyButton.setOnAction((ActionEvent e) -> ((ViewComManager) ManagerManager.getViewReceiver()).getInstance().requestPlayerReady());
        Scene snapScene = new Scene(readyButton);
        snapScene.snapshot(null);

        Button startButton = new Button("Start game");
        startButton.setOnAction((ActionEvent e) -> {
            if (!((ViewComManager) ManagerManager.getViewReceiver()).isConnected()) {
                StrategoClient client = new StrategoClient();
                (new Thread(client)).start();
                ((ViewComManager) ManagerManager.getViewReceiver()).setStrategoClient(client);
            }
        });

        Button autoDeployButton = new Button("Auto deploy");
        autoDeployButton.setOnAction((ActionEvent e) -> {
            ((ViewComManager) ManagerManager.getViewReceiver()).requestAutoDeploy();
        });

        Button resetDeploymentButton = new Button("Reset deployment");
        resetDeploymentButton.setOnAction((ActionEvent e) -> {
            ((ViewComManager) ManagerManager.getViewReceiver()).requestResetDeployment();
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(readyButton, startButton, autoDeployButton, resetDeploymentButton);

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
        helpText.setStyle("-fx-font: 16 arial; -fx-background-color: transparent;");
        helpText.setPadding(new Insets(20));

        helpMenu.setContent(helpText);

        menu.getPanes().add(helpMenu);
    }

    private void configureChangeListener() {
        menu.expandedPaneProperty().addListener(((observable, oldValue, newValue) -> {
            // change between single and multiplayer mode
            if (newValue == singlePlayerMenu) {
                //((ViewComManager) ManagerManager.getViewReceiver()).configureSinglePlayer();
            } else if (newValue == multiPlayerMenu) {

            }
        }));
    }

}
