package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Simon on 17/09/2016.
 */
public class Main extends Application {

    private static Main instance;

    private Stage primaryStage;

    private Scene scene;

    private MainMenu mainMenu;
    private SinglePlayerMenu singlePlayerMenu;
    private MultiPlayerMenu multiPlayerMenu;
    private InGameView inGameView;

    public static void main(String[] args) {
        launch(args);
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {
        // linking the instance of Main that is running to a static variable so it can be accessed easily
        instance = this;

        // creating the first menu that the user will see and then assigning it to the scene that will make it visible
        // adding a style sheet for the UI elements
        mainMenu = new MainMenu();
        scene = new Scene(mainMenu, 1200, 800);
        scene.getStylesheets().add("styles.css");

        // setting up the window
        primaryStage = stage;
        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // method to change between different menus by changing the root of the visible scene
    // maybe not the best option for that functionality
    public void setView(int id) {
        if (id == 0) {
            scene.setRoot(mainMenu);
        } else if (id == 1) {
            if (singlePlayerMenu == null) {
                singlePlayerMenu = new SinglePlayerMenu();
            }
            scene.setRoot(singlePlayerMenu);
        } else if (id == 2) {
            if (multiPlayerMenu == null) {
                multiPlayerMenu = new MultiPlayerMenu();
            }
            scene.setRoot(multiPlayerMenu);
        } else if (id == 3) {
            if (inGameView == null) {
                inGameView = new InGameView();
            }
            scene.setRoot(inGameView);
        }
    }

    public Scene getScene() {
        return scene;
    }

}
