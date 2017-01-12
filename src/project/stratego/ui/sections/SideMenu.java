package project.stratego.ui.sections;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import project.stratego.control.managers.ViewComManager;
import project.stratego.ui.utils.FileLoader;

import java.io.File;

public class SideMenu extends Pane {

    private StrategoFrame parent;

    private Accordion menu;

    private TitledPane singlePlayerMenu, multiPlayerMenu, showMatchMenu, specialFeaturesMenu, helpMenu;

    private FileChooser gameStateChooser, setupChooser;

    public SideMenu(StrategoFrame parent) {
        this.parent = parent;
        menu = new Accordion();

        gameStateChooser = new FileChooser();
        gameStateChooser.setTitle("Load Game State");
        gameStateChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //gameStateChooser.setInitialFileName(new SimpleDateFormat("StrategoGameState_HH:mm:ss_dd-MM").format(new Date()));
        gameStateChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Stratego Game State", "*.gamestate"));

        setupChooser = new FileChooser();
        setupChooser.setTitle("Load Setup");
        setupChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //setupChooser.setInitialFileName(new SimpleDateFormat("StrategoSetup_HH:mm:ss_dd-MM").format(new Date()));
        setupChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Stratego Setup", "*.setup"));

        makeSinglePlayerMenu();
        makeShowMatchMenu();
        makeMultiPlayerMenu();
        makeSpecialFeaturesMenu();
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

        Button loadSetupButton = new Button("Load setup");
        loadSetupButton.setOnAction((ActionEvent e) -> {
            String setupEncoding = FileLoader.load(setupChooser.showOpenDialog(parent));
            if (setupEncoding != null) {
                ViewComManager.getInstance().requestLoadSetup(parent.getInGameView().getPlayerIndex(), setupEncoding);
            }
        });
        loadSetupButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        loadSetupButton.setTextFill(Color.BLACK);
        loadSetupButton.setOnMouseEntered(e -> {
            loadSetupButton.setTextFill(Color.WHITE);
        });
        loadSetupButton.setOnMouseExited(e -> {
            loadSetupButton.setTextFill(Color.BLACK);
        });

        Button saveSetupButton = new Button("Save setup");
        saveSetupButton.setOnAction((ActionEvent e) -> {
            File file = setupChooser.showSaveDialog(parent);
            if (file != null) {
                ViewComManager.getInstance().requestSaveSetup(parent.getInGameView().getPlayerIndex(), file.getPath());
            }
        });
        saveSetupButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        saveSetupButton.setTextFill(Color.BLACK);
        saveSetupButton.setOnMouseEntered(e -> {
            saveSetupButton.setTextFill(Color.WHITE);
        });
        saveSetupButton.setOnMouseExited(e -> {
            saveSetupButton.setTextFill(Color.BLACK);
        });

        Button autoDeployButton = new Button("Auto deploy");
        autoDeployButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestAutoDeploy());
        autoDeployButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        autoDeployButton.setTextFill(Color.BLACK);
        autoDeployButton.setOnMouseEntered(e -> {
            autoDeployButton.setTextFill(Color.WHITE);
        });
        autoDeployButton.setOnMouseExited(e -> {
            autoDeployButton.setTextFill(Color.BLACK);
        });

        Button resetDeploymentButton = new Button("Reset deployment");
        resetDeploymentButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        resetDeploymentButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        resetDeploymentButton.setTextFill(Color.BLACK);
        resetDeploymentButton.setOnMouseEntered(e -> {
            resetDeploymentButton.setTextFill(Color.WHITE);
        });
        resetDeploymentButton.setOnMouseExited(e -> {
            resetDeploymentButton.setTextFill(Color.BLACK);
        });

        Button configureButton = new Button("Configure AI");
        configureButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        configureButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        configureButton.setTextFill(Color.BLACK);
        configureButton.setOnMouseEntered(e -> {
            configureButton.setTextFill(Color.WHITE);
        });
        configureButton.setOnMouseExited(e -> {
            configureButton.setTextFill(Color.BLACK);
        });

        Button startButton = new Button("Start game");
        startButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        startButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        startButton.setTextFill(Color.BLACK);
        startButton.setOnMouseEntered(e -> {
            startButton.setTextFill(Color.WHITE);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setTextFill(Color.BLACK);
        });

        Button saveGameButton = new Button("Save game");
        saveGameButton.setOnAction((ActionEvent e) -> {
            String filePath = gameStateChooser.showSaveDialog(parent).getPath();
            if (filePath != null) {
                ViewComManager.getInstance().requestSaveGame(filePath);
            }
        });
        saveGameButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        saveGameButton.setTextFill(Color.BLACK);
        saveGameButton.setOnMouseEntered(e -> {
            saveGameButton.setTextFill(Color.WHITE);
        });
        saveGameButton.setOnMouseExited(e -> {
            saveGameButton.setTextFill(Color.BLACK);
        });

        Button loadGameButton = new Button("Load game");
        loadGameButton.setOnAction((ActionEvent e) -> {
            String gameEncoding = FileLoader.load(gameStateChooser.showOpenDialog(parent));
            if (gameEncoding != null) {
                ViewComManager.getInstance().requestLoadGame(gameEncoding);
            }
        });
        loadGameButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        loadGameButton.setTextFill(Color.BLACK);
        loadGameButton.setOnMouseEntered(e -> {
            loadGameButton.setTextFill(Color.WHITE);
        });
        loadGameButton.setOnMouseExited(e -> {
            loadGameButton.setTextFill(Color.BLACK);
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(0);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(loadSetupButton, saveSetupButton, autoDeployButton, resetDeploymentButton, configureButton, startButton, saveGameButton, loadGameButton);

        singlePlayerMenu.setContent(pane);

        menu.getPanes().add(singlePlayerMenu);
    }

    private void makeMultiPlayerMenu() {
        multiPlayerMenu = new TitledPane();
        multiPlayerMenu.setText("Multiplayer");
        //multiPlayerMenu.setStyle("-fx-font: 22 helvetica;");

        Button readyButton = new Button("Ready");
        readyButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestPlayerReady());
        readyButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        readyButton.setTextFill(Color.BLACK);
        readyButton.setOnMouseEntered(e -> {
            readyButton.setTextFill(Color.WHITE);
        });
        readyButton.setOnMouseExited(e -> {
            readyButton.setTextFill(Color.BLACK);
        });

        Button startButton = new Button("Start game");
        startButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        startButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        startButton.setTextFill(Color.BLACK);
        startButton.setOnMouseEntered(e -> {
            startButton.setTextFill(Color.WHITE);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setTextFill(Color.BLACK);
        });

        Button loadSetupButton = new Button("Load setup");
        loadSetupButton.setOnAction((ActionEvent e) -> {
            String setupEncoding = FileLoader.load(setupChooser.showOpenDialog(parent));
            if (setupEncoding != null) {
                ViewComManager.getInstance().requestLoadSetup(parent.getInGameView().getPlayerIndex(), setupEncoding);
            }
        });
        loadSetupButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        loadSetupButton.setTextFill(Color.BLACK);
        loadSetupButton.setOnMouseEntered(e -> {
            loadSetupButton.setTextFill(Color.WHITE);
        });
        loadSetupButton.setOnMouseExited(e -> {
            loadSetupButton.setTextFill(Color.BLACK);
        });

        Button autoDeployButton = new Button("Auto deploy");
        autoDeployButton.setOnAction((ActionEvent e) -> {
            if (ViewComManager.getInstance().isConnected()) {
                ViewComManager.getInstance().requestAutoDeploy();
            }
        });
        autoDeployButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        autoDeployButton.setTextFill(Color.BLACK);
        autoDeployButton.setOnMouseEntered(e -> {
            autoDeployButton.setTextFill(Color.WHITE);
        });
        autoDeployButton.setOnMouseExited(e -> {
            autoDeployButton.setTextFill(Color.BLACK);
        });

        Button resetDeploymentButton = new Button("Reset deployment");
        resetDeploymentButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        resetDeploymentButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        resetDeploymentButton.setTextFill(Color.BLACK);
        resetDeploymentButton.setOnMouseEntered(e -> {
            resetDeploymentButton.setTextFill(Color.WHITE);
        });
        resetDeploymentButton.setOnMouseExited(e -> {
            resetDeploymentButton.setTextFill(Color.BLACK);
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(0);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(loadSetupButton, autoDeployButton, resetDeploymentButton, readyButton, startButton);
        autoDeployButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");

        multiPlayerMenu.setContent(pane);

        menu.getPanes().add(multiPlayerMenu);
    }

    private void makeShowMatchMenu() {
        showMatchMenu = new TitledPane();
        showMatchMenu.setText("AI Show Match");

        Button nextMoveButton = new Button("Next move");
        nextMoveButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestNextMove());
        nextMoveButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        nextMoveButton.setTextFill(Color.BLACK);
        nextMoveButton.setOnMouseEntered(e -> {
            nextMoveButton.setTextFill(Color.WHITE);
        });
        nextMoveButton.setOnMouseExited(e -> {
            nextMoveButton.setTextFill(Color.BLACK);
        });

        Button configureButton = new Button("Configure");
        configureButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestResetDeployment());
        configureButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        configureButton.setTextFill(Color.BLACK);
        configureButton.setOnMouseEntered(e -> {
            configureButton.setTextFill(Color.WHITE);
        });
        configureButton.setOnMouseExited(e -> {
            configureButton.setTextFill(Color.BLACK);
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        resetButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        resetButton.setTextFill(Color.BLACK);
        resetButton.setOnMouseEntered(e -> {
            resetButton.setTextFill(Color.WHITE);
        });
        resetButton.setOnMouseExited(e -> {
            resetButton.setTextFill(Color.BLACK);
        });

        Button saveGameButton = new Button("Save game");
        //saveGameButton.setOnAction((ActionEvent e) -> ViewComManager.getInstance().requestStartGame());
        saveGameButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        saveGameButton.setTextFill(Color.BLACK);
        saveGameButton.setOnMouseEntered(e -> {
            saveGameButton.setTextFill(Color.WHITE);
        });
        saveGameButton.setOnMouseExited(e -> {
            saveGameButton.setTextFill(Color.BLACK);
        });

        Button loadGameButton = new Button("Load game");
        loadGameButton.setOnAction((ActionEvent e) -> {
            String gameEncoding = FileLoader.load(gameStateChooser.showOpenDialog(parent));
            if (gameEncoding != null) {
                ViewComManager.getInstance().requestLoadGame(gameEncoding);
            }
        });
        loadGameButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        loadGameButton.setTextFill(Color.BLACK);
        loadGameButton.setOnMouseEntered(e -> {
            loadGameButton.setTextFill(Color.WHITE);
        });
        loadGameButton.setOnMouseExited(e -> {
            loadGameButton.setTextFill(Color.BLACK);
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(0);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(nextMoveButton, configureButton, resetButton, saveGameButton, loadGameButton);

        showMatchMenu.setContent(pane);

        menu.getPanes().add(showMatchMenu);
    }

    private void makeSpecialFeaturesMenu() {
        specialFeaturesMenu = new TitledPane();
        specialFeaturesMenu.setText("Special Features");

        Button loadReplayButton = new Button("Load Replay");
        loadReplayButton.setOnAction((ActionEvent e) -> {
            File replay = gameStateChooser.showOpenDialog(parent);
            if (replay != null) {
                System.out.println(replay.getAbsolutePath());
            }
        });
        loadReplayButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        loadReplayButton.setTextFill(Color.BLACK);
        loadReplayButton.setOnMouseEntered(e -> {
            loadReplayButton.setTextFill(Color.WHITE);
        });
        loadReplayButton.setOnMouseExited(e -> {
            loadReplayButton.setTextFill(Color.BLACK);
        });

        Button runAIMatchesButton = new Button("Run AI-matches \nautomatically");
        runAIMatchesButton.setOnAction((ActionEvent e) -> {
            // open dialog to configure which AIs to pit against each other, how many rounds to play, which stats to keep, where to store the results
        });
        runAIMatchesButton.setStyle("-fx-font: 20 helvetica; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;");
        runAIMatchesButton.setTextFill(Color.BLACK);
        runAIMatchesButton.setOnMouseEntered(e -> {
            runAIMatchesButton.setTextFill(Color.WHITE);
        });
        runAIMatchesButton.setOnMouseExited(e -> {
            runAIMatchesButton.setTextFill(Color.BLACK);
        });

        VBox pane = new VBox();
        pane.setPadding(new Insets(5));
        pane.setSpacing(0);
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(loadReplayButton, runAIMatchesButton);

        specialFeaturesMenu.setContent(pane);

        menu.getPanes().add(specialFeaturesMenu);
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
            if (newValue == singlePlayerMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().configureSinglePlayer();
            } else if (newValue == showMatchMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().configureAIShowMatch();
            } else if (newValue == multiPlayerMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().configureMultiPlayer();
            } else if (newValue == specialFeaturesMenu) {
                ViewComManager.getInstance().requestResetGame();
                ViewComManager.getInstance().sendAssignSide(-1);
            }
        }));
    }

    public void reset() {
        menu.setExpandedPane(null);
    }

}
