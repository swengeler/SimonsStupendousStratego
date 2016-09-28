package project.stratego.ui;

import project.stratego.control.CommunicationManager;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.stratego.ui.menus.*;

/**
 *
 */
public class StrategoFrame extends Stage {

    public static final int FRAME_WIDTH = 1100;
    public static final int FRAME_HEIGHT = 800;

    private static StrategoFrame instance;
    private static boolean initialised;

    private CommunicationManager controller;

    private Scene scene;

    private MainMenu mainMenu;
    private SinglePlayerMenu singlePlayerMenu;
    private MultiPlayerMenu multiPlayerMenu;
    private InGameView inGameView;

    public static StrategoFrame getInstance() {
        return instance;
    }

    public static void initialise(CommunicationManager controller) {
        if (!initialised) {
            instance = new StrategoFrame(controller);
            initialised = true;
        }
    }

    private StrategoFrame(CommunicationManager controller) {
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

    public InGameView getInGameView() {
        return inGameView;
    }

    /* Setter methods */

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
