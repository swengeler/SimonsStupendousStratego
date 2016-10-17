package project.stratego.control;

import project.stratego.game.StrategoGame;
import javafx.application.Application;
import javafx.stage.Stage;
import project.stratego.ui.StrategoFrame;

public class StrategoMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ManagerManager.configureMultiPlayer();

        primaryStage = new StrategoFrame();
        ManagerManager.getViewReceiver().setStrategoFrame((StrategoFrame) primaryStage);

        primaryStage.setTitle("Simon's Stupendous Stratego");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
