package project.stratego.ui;

import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.stratego.ui.menus.*;

public class StrategoFrame extends Stage {

    private SideMenu sideMenu;
    private InGameView inGameView;

    public StrategoFrame() {
        setUp();
    }

    private void setUp() {
        HBox components = new HBox();
        components.setStyle("-fx-background-color: #bf1c1c");

        sideMenu = new SideMenu();
        inGameView = new InGameView(20);

        components.getChildren().addAll(sideMenu, inGameView);

        Scene scene = new Scene(components);
        setScene(scene);
        setTitle("New UI Test");
        setResizable(false);
        show();

        System.out.println(inGameView.getWidth() + " " + inGameView.getHeight());
        System.out.println(sideMenu.getHeight());
    }

    public InGameView getInGameView() {
        return inGameView;
    }

}
