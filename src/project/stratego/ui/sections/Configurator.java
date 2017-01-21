package project.stratego.ui.sections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.stratego.ui.utils.Messages;

import java.awt.*;

public class Configurator extends Stage {

    private int mode;

    private VBox layout;

    public Configurator(int mode) {
        this.mode = mode;

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Configure AI settings");
        getIcons().add(new Image(Messages.class.getResourceAsStream("/icons/program_icon.png")));

        /*Button okButton = new Button("Ok");
        okButton.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction((ActionEvent e) -> close());

        layout = new VBox();
        setupInterface();
        layout.getChildren().addAll(okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);*/


        GridPane sections = new GridPane();

        Insets insets = new Insets(5, 10, 5, 10);

        VBox firstSection = new VBox();
        Label searchMethod = new Label("Search method:");
        firstSection.getChildren().add(searchMethod);
        VBox.setMargin(searchMethod, insets);
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Random",
                        "Expectimax",
                        "MCTS"
                );
        ComboBox searchMethodBox = new ComboBox(options);
        searchMethodBox.getSelectionModel().selectFirst();
        firstSection.getChildren().add(searchMethodBox);
        VBox.setMargin(searchMethodBox, insets);
        sections.add(firstSection, 0, 0);

        Separator vertSeparator1 = new Separator();
        vertSeparator1.setOrientation(Orientation.VERTICAL);
        vertSeparator1.setPadding(new Insets(10, 0, 10, 0));
        sections.add(vertSeparator1, 1, 0);

        VBox secondSection = new VBox();
        Label pruningMethod = new Label("Pruning method:");
        secondSection.getChildren().add(pruningMethod);
        VBox.setMargin(pruningMethod, insets);
        ToggleGroup toggleGroup1 = new ToggleGroup();
        RadioButton star1 = new RadioButton("STAR1");
        RadioButton star2 = new RadioButton("STAR2");
        star1.setToggleGroup(toggleGroup1);
        star2.setToggleGroup(toggleGroup1);
        secondSection.getChildren().add(star1);
        secondSection.getChildren().add(star2);
        VBox.setMargin(star1, insets);
        VBox.setMargin(star2, insets);
        sections.add(secondSection, 2, 0);

        Separator vertSeparator2 = new Separator();
        vertSeparator2.setOrientation(Orientation.VERTICAL);
        vertSeparator2.setPadding(new Insets(10, 0, 10, 0));
        sections.add(vertSeparator2, 3, 0);

        VBox thirdSection = new VBox();
        Label evaluationFunction = new Label("Evaluation function:");
        thirdSection.getChildren().add(evaluationFunction);
        VBox.setMargin(evaluationFunction, insets);
        ToggleGroup toggleGroup2 = new ToggleGroup();
        RadioButton naiveEval = new RadioButton("Naive");
        RadioButton marksEval = new RadioButton("Mark's");
        naiveEval.setToggleGroup(toggleGroup2);
        marksEval.setToggleGroup(toggleGroup2);
        thirdSection.getChildren().add(naiveEval);
        thirdSection.getChildren().add(marksEval);
        VBox.setMargin(naiveEval, insets);
        VBox.setMargin(marksEval, insets);
        sections.add(thirdSection, 4, 0);

        Separator vertSeparator3 = new Separator();
        vertSeparator3.setOrientation(Orientation.VERTICAL);
        vertSeparator3.setPadding(new Insets(10, 0, 10, 0));
        sections.add(vertSeparator3, 5, 0);

        VBox fourthSection = new VBox();
        Label otherSettings = new Label("Other settings:");
        fourthSection.getChildren().add(otherSettings);
        VBox.setMargin(otherSettings, insets);
        CheckBox moveOrdering = new CheckBox("Move ordering");
        fourthSection.getChildren().add(moveOrdering);
        VBox.setMargin(moveOrdering, insets);
        ToggleGroup toggleGroup3 = new ToggleGroup();

        RadioButton depthLimit = new RadioButton("Depth limit");
        depthLimit.setToggleGroup(toggleGroup3);
        fourthSection.getChildren().add(depthLimit);
        VBox.setMargin(depthLimit, insets);
        ObservableList<Integer> depthLimitOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        ComboBox depthLimitBox = new ComboBox(depthLimitOptions);
        depthLimitBox.setDisable(true);
        depthLimitBox.getSelectionModel().selectFirst();
        fourthSection.getChildren().add(depthLimitBox);
        VBox.setMargin(depthLimitBox, insets);
        depthLimit.setOnAction(e -> depthLimitBox.setDisable(false));

        RadioButton iterDeep = new RadioButton("Time limit");
        iterDeep.setToggleGroup(toggleGroup3);
        fourthSection.getChildren().add(iterDeep);
        VBox.setMargin(iterDeep, insets);
        ObservableList<Integer> iterDeepOptions = FXCollections.observableArrayList(250, 500, 1000, 1500, 2000, 3000, 5000, 100000);
        ComboBox iterDeepBox = new ComboBox(iterDeepOptions);
        iterDeepBox.setDisable(true);
        iterDeepBox.getSelectionModel().selectFirst();
        fourthSection.getChildren().add(iterDeepBox);
        VBox.setMargin(iterDeepBox, insets);
        iterDeep.setOnAction(e -> {
            iterDeepBox.setDisable(false);
            depthLimitBox.setDisable(true);
        });

        sections.add(fourthSection, 6, 0);

        Scene scene = new Scene(sections);
        setScene(scene);
        showAndWait();
    }

    private void setupInterface() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Random",
                        "Expectimax",
                        "STAR1",
                        "STAR2",
                        "MCTS"
                );
        ComboBox comboBox = new ComboBox(options);
        layout.getChildren().add(comboBox);
    }

}
