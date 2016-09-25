package game;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.StrategoFrame;

/**
 *
 */
public class StrategoMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CommunicationManager controller = new CommunicationManager();
        primaryStage = new StrategoFrame(controller);
        controller.setStrategoFrame((StrategoFrame) primaryStage);

        primaryStage.setTitle("Test");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
