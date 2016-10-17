package project.stratego.ui;

import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import project.stratego.control.ManagerManager;
import project.stratego.control.ViewComManager;
import project.stratego.ui.menus.*;

public class StrategoFrame extends Stage {

    private SideMenu sideMenu;
    private InGameView inGameView;

    public StrategoFrame() {
        setUp();
    }

    private void setUp() {
        HBox components = new HBox();
        //components.setStyle("-fx-background-color: #bf1c1c");
        components.setStyle("-fx-background-color: linear-gradient(from 20% 50% to 100% 50%, #48a4f9, #bf1c1c);");

        sideMenu = new SideMenu(this);
        inGameView = new InGameView(20);

        components.getChildren().addAll(sideMenu, inGameView);

        Scene scene = new Scene(components);
        setScene(scene);
        scene.getStylesheets().add("/menustyle.css");
        setTitle("Simon's Stupendous Stratego");
        setResizable(false);
        show();
        setOnCloseRequest((WindowEvent w) -> {
            if (((ViewComManager) ManagerManager.getViewReceiver()).isConnected()) {
                // close client thread
                System.out.println("window close requested");
                ((ViewComManager) ManagerManager.getViewReceiver()).closeStrategoClient();
            }
        });

        System.out.println(inGameView.getWidth() + " " + inGameView.getHeight());
        System.out.println(sideMenu.getHeight());
    }

    public InGameView getInGameView() {
        return inGameView;
    }

}
