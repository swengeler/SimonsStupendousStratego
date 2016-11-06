package project.stratego.ui.sections;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import project.stratego.control.managers.ViewComManager;

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
        //singlePlayerMenu.setStyle("-fx-font: 22 helvetica;");

        Button autoDeployButton = new Button("Auto deploy");
        autoDeployButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestAutoDeploy());
        autoDeployButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        autoDeployButton.setTextFill(Color.BLACK);
        autoDeployButton.setOnMouseEntered(e -> {
            autoDeployButton.setTextFill(Color.WHITE);
        });
        autoDeployButton.setOnMouseExited(e -> {
            autoDeployButton.setTextFill(Color.BLACK);
        });

        Button resetDeploymentButton = new Button("Reset deployment");
        resetDeploymentButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        resetDeploymentButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        resetDeploymentButton.setTextFill(Color.BLACK);
        resetDeploymentButton.setOnMouseEntered(e -> {
            resetDeploymentButton.setTextFill(Color.WHITE);
        });
        resetDeploymentButton.setOnMouseExited(e -> {
            resetDeploymentButton.setTextFill(Color.BLACK);
        });

        Button startButton = new Button("Start game");
        startButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        startButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        startButton.setTextFill(Color.BLACK);
        startButton.setOnMouseEntered(e -> {
            startButton.setTextFill(Color.WHITE);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setTextFill(Color.BLACK);
        });

        // Pane pane = new Pane();
        // pane.getChildren().addAll(b1, b2, startButton);

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(autoDeployButton, resetDeploymentButton, startButton);

        singlePlayerMenu.setContent(pane);

        menu.getPanes().add(singlePlayerMenu);
    }

    private void makeMultiPlayerMenu() {
        multiPlayerMenu = new TitledPane();
        multiPlayerMenu.setText("Multiplayer");
        //multiPlayerMenu.setStyle("-fx-font: 22 helvetica;");

        Button readyButton = new Button("Ready");
        readyButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestPlayerReady());
        readyButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        readyButton.setTextFill(Color.BLACK);
        readyButton.setOnMouseEntered(e -> {
            readyButton.setTextFill(Color.WHITE);
        });
        readyButton.setOnMouseExited(e -> {
            readyButton.setTextFill(Color.BLACK);
        });

        Button startButton = new Button("Start game");
        startButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        startButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        startButton.setTextFill(Color.BLACK);
        startButton.setOnMouseEntered(e -> {
            startButton.setTextFill(Color.WHITE);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setTextFill(Color.BLACK);
        });

        Button autoDeployButton = new Button("Auto deploy");
        autoDeployButton.setOnAction((ActionEvent e) -> {
            if (ViewComManager.getInstance().isConnected()) {
                ViewComManager.getInstance().requestAutoDeploy();
            }
        });
        autoDeployButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        autoDeployButton.setTextFill(Color.BLACK);
        autoDeployButton.setOnMouseEntered(e -> {
            autoDeployButton.setTextFill(Color.WHITE);
        });
        autoDeployButton.setOnMouseExited(e -> {
            autoDeployButton.setTextFill(Color.BLACK);
        });

        Button resetDeploymentButton = new Button("Reset deployment");
        resetDeploymentButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        resetDeploymentButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        resetDeploymentButton.setTextFill(Color.BLACK);
        resetDeploymentButton.setOnMouseEntered(e -> {
            resetDeploymentButton.setTextFill(Color.WHITE);
        });
        resetDeploymentButton.setOnMouseExited(e -> {
            resetDeploymentButton.setTextFill(Color.BLACK);
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(5);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(readyButton, startButton, autoDeployButton, resetDeploymentButton);
        autoDeployButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");

        multiPlayerMenu.setContent(pane);

        menu.getPanes().add(multiPlayerMenu);
    }

    private void makeHelpMenu() {
        helpMenu = new TitledPane();
        helpMenu.setText("Help");
        //helpMenu.setStyle("-fx-font: 22 helvetica;");

        Label helpText = new Label(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque non tincidunt leo, " +
            "id sodales quam. Vivamus in rutrum lorem. Aliquam hendrerit ultricies lectus a finibus. " +
            "Maecenas augue elit, ullamcorper ac lobortis ut, scelerisque ac orci. Nam dapibus libero aliquam, " +
            "sagittis magna finibus, consequat lorem. Etiam mollis placerat massa eu maximus. In non fringilla sapien. " +
            "Ut feugiat condimentum enim vitae molestie. Sed blandit ultricies mauris quis sagittis."
        );
        helpText.setWrapText(true);
        helpText.setPrefWidth(220);
        helpText.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-background-color: transparent;");
        helpText.setPadding(new Insets(10));

        helpMenu.setContent(helpText);

        menu.getPanes().add(helpMenu);
    }

    private void configureChangeListener() {
        menu.expandedPaneProperty().addListener(((observable, oldValue, newValue) -> {
            // change between single and multiplayer mode
            if (newValue == singlePlayerMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().configureSinglePlayer();
            } else if (newValue == multiPlayerMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().configureMultiPlayer();
            }
        }));
    }

}
