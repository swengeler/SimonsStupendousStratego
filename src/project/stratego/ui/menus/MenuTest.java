package project.stratego.ui.menus;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 */
public class MenuTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group group = new Group();
        Scene scene = new Scene(group, 300, 150);

        Rectangle r = new Rectangle(50, 50, Color.RED);
        r.setLayoutX(200);
        r.setLayoutY(50);
        group.getChildren().add(r);

        TitledPane tp1 = new TitledPane();
        tp1.setText("Menu item 1");
        tp1.setContent(new Button("Button doing something"));
        tp1.setStyle("-fx-box-border: transparent;");
        tp1.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                r.setVisible(newValue ? true : false);
            }
        });
        tp1.setAnimated(false);

        TitledPane tp2 = new TitledPane();
        tp2.setText("Menu item 2");
        tp2.setContent(new ImageView(new Image(getClass().getResourceAsStream("/spy_piece.png"), 40, 40, true, true)));
        tp2.setStyle("-fx-box-border: transparent;");
        tp2.setAnimated(false);

        TitledPane tp3 = new TitledPane();
        tp3.setText("Menu item 3");
        tp3.setContent(new Rectangle(50, 15, Color.AQUA));
        tp3.setStyle("-fx-box-border: transparent;");
        tp3.setAnimated(false);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(tp1, tp2, tp3);
        accordion.setStyle("-fx-box-border: transparent;");

        group.getChildren().add(accordion);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Menu test");
        primaryStage.show();
    }

}
