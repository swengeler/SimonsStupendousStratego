package project.stratego.ui.sections;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import project.stratego.control.DebugHelper;
import project.stratego.control.managers.ViewComManager;
import project.stratego.ui.sections.*;

public class StrategoFrame extends Stage {

    private SideMenu sideMenu;
    private InGameView inGameView;

    public StrategoFrame() {
        setUp();
    }

    private void setUp() {
        DebugHelper debugHelper = new DebugHelper();

        HBox components = new HBox();
        //components.setStyle("-fx-background-color: #bf1c1c");
        components.setStyle("-fx-background-color: linear-gradient(from 20% 50% to 100% 50%, #48a4f9, #bf1c1c);");

        sideMenu = new SideMenu(this);
        inGameView = new InGameView(20);

        components.getChildren().addAll(sideMenu, inGameView);

        Scene scene = new Scene(components);
        scene.getStylesheets().add("/menustyle.css");
        setScene(scene);
        setTitle("Simon's Stupendous Stratego");
        getIcons().add(new Image(getClass().getResourceAsStream("/icons/program_icon.png")));
        setResizable(false);
        show();
        setOnCloseRequest((WindowEvent w) -> {
            if (ViewComManager.getInstance().isConnected()) {
                // close client thread
                //System.out.println("window close requested");
                ViewComManager.getInstance().closeStrategoClient();
            }
            debugHelper.close();
        });

    }

    public InGameView getInGameView() {
        return inGameView;
    }

    public SideMenu getSideMenu() {
        return sideMenu;
    }

}
