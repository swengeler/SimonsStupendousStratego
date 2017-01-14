package project.stratego.ui.sections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.stratego.ui.utils.Messages;

public class Configurator extends Stage {

    private int mode;

    private VBox layout;

    public Configurator(int mode) {
        this.mode = mode;

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Configure AI settings");
        getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        Button okButton = new Button("Ok");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> close());

        layout = new VBox();
        setupInterface();
        layout.getChildren().addAll(okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        setScene(scene);
        showAndWait();
    }

    private void setupInterface() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Random",
                        "Expectimax",
                        "MCTS"
                );
        ComboBox comboBox = new ComboBox(options);
        layout.getChildren().add(comboBox);
    }

}
