package project.stratego.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Messages {

    public static void showNoConnectionMessage() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("No connection to server");
        stage.setMaxWidth(180);
        stage.getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        Label label = new Label("Could not connect to the server. Please try again later.");
        label.setStyle("-fx-font: 14 helvetica");
        label.setMaxWidth(150);
        label.setWrapText(true);

        Button okButton = new Button("Ok");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> stage.close());

        VBox layout = new VBox();
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static void showOpponentDisonnectedMessage() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Opponent disconnected");
        stage.setMaxWidth(180);
        stage.getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        Label label = new Label("Your opponent disconnected. The game will now be reset.");
        label.setStyle("-fx-font: 14 helvetica");
        label.setMaxWidth(150);
        label.setWrapText(true);

        Button okButton = new Button("Ok");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> stage.close());

        VBox layout = new VBox();
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static void showPlayerWon() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("You won!");
        stage.setMaxWidth(180);
        stage.getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        Label label = new Label("Congratulations, you won the game! Good luck and have fun next time!");
        label.setStyle("-fx-font: 14 helvetica");
        label.setMaxWidth(150);
        label.setWrapText(true);

        Button okButton = new Button("Great!");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> stage.close());

        VBox layout = new VBox();
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public static void showOpponentWon() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Opponent won");
        stage.setMaxWidth(180);
        stage.getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        Label label = new Label("Your opponent won the game. Better luck next time!");
        label.setStyle("-fx-font: 14 helvetica");
        label.setMaxWidth(150);
        label.setWrapText(true);

        Button okButton = new Button("Ok :(");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> stage.close());

        VBox layout = new VBox();
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

}
