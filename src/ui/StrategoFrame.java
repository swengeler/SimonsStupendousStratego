package ui;

import game.CommunicationManager;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 */
public class StrategoFrame extends Stage {

    public static final int FRAME_WIDTH = 1100;
    public static final int FRAME_HEIGHT = 800;

    private CommunicationManager controller;

    private Scene scene;

    private MainMenu mainMenu;
    private SinglePlayerMenu singlePlayerMenu;
    private MultiPlayerMenu multiPlayerMenu;
    private InGameView inGameView;

    public StrategoFrame(CommunicationManager controller) {
        mainMenu = new MainMenu(this);
        scene = new Scene(mainMenu, FRAME_WIDTH, FRAME_HEIGHT);
        scene.getStylesheets().add("styles.css");
        this.setScene(scene);
        this.controller = controller;
    }

    /* Getter methods */

    public CommunicationManager getComManager() {
        return controller;
    }

    public void setToMainMenu() {
        scene.setRoot(mainMenu);
    }

    public void setToSinglePlayerMenu() {
        if (singlePlayerMenu == null) {
            singlePlayerMenu = new SinglePlayerMenu(this);
        }
        scene.setRoot(singlePlayerMenu);
    }

    public void setToMultiPlayerMenu() {
        if (multiPlayerMenu == null) {
            multiPlayerMenu = new MultiPlayerMenu(this);
        }
        scene.setRoot(multiPlayerMenu);
    }

    public void setToInGameView() {
        inGameView = new InGameView(this);
        scene.setRoot(inGameView);
    }

}
