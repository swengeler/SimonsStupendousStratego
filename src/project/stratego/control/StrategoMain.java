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
        CommunicationManager controller = CommunicationManager.getInstance();
        primaryStage = new StrategoFrame();
        controller.setStrategoFrame((StrategoFrame) primaryStage);
        StrategoGame game = new StrategoGame();
        controller.setStrategoGame(game);

        primaryStage.setTitle("Test");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
