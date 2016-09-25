package tst;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 */
public class MainTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*public void start(Stage primaryStage) {
        Rectangle front = new Rectangle(50, 50);

        ScaleTransition stHideFront = new ScaleTransition(Duration.millis(500), front);
        stHideFront.setFromX(1);
        stHideFront.setToX(0);

        Rectangle back = new Rectangle(50, 50, Color.RED);
        back.setScaleX(0);

        ScaleTransition stShowBack = new ScaleTransition(Duration.millis(500), back);
        stShowBack.setFromX(0);
        stShowBack.setToX(1);

        stHideFront.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stShowBack.play();
            }
        });

        StackPane root = new StackPane();
        root.getChildren().addAll(front, back);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);

        primaryStage.show();
        stHideFront.play();
    }*/

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Play by resizing the window");
        VBox root = new VBox();
        root.getChildren().add(btn);
        root.setStyle("-fx-background-color: gray");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(2));
        primaryStage.minHeightProperty().bind(scene.widthProperty().divide(2));
        primaryStage.show();
    }

}
