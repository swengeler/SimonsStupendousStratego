package project.stratego.ui;

import project.stratego.control.managers.ModelComManager;
import project.stratego.control.managers.ViewComManager;
import javafx.application.Application;
import javafx.stage.Stage;
import project.stratego.ui.sections.StrategoFrame;

public class StrategoMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = new StrategoFrame();

        ViewComManager.getInstance().setStrategoFrame((StrategoFrame) primaryStage);
        ViewComManager.getInstance().configureSinglePlayer();

        //ModelComManager.getInstance().configureAIMatch();

        primaryStage.setTitle("Simon's Stupendous Stratego");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
