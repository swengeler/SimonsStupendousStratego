package project.stratego.ui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import project.stratego.ui.menus.InGameView;
import project.stratego.ui.menus.SideMenu;

/**
 *
 */
public class StrategoFrame2 extends Stage {

    private SideMenu sideMenu;
    private InGameView inGameView;

    public StrategoFrame2() {
        setUp();
    }

    private void setUp() {
        HBox components = new HBox();
        // would be nice to make these window-size based
        sideMenu = new SideMenu();
        inGameView = new InGameView(null, 20);

        components.getChildren().addAll(sideMenu, inGameView);

        Scene scene = new Scene(components);
        setScene(scene);
        setTitle("New UI Test");
        //setResizable(false);
        show();

        System.out.println(inGameView.getWidth() + " " + inGameView.getHeight());
        System.out.println(sideMenu.getHeight());
    }

}
