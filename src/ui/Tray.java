package ui;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 *
 */
public class Tray extends Pane {

    private StrategoFrame parent;

    private String packagePrefix;

    private double xOffset = 2;
    private double yOffset = 2;

    public Tray(StrategoFrame parent, String packagePrefix) {
        this.packagePrefix = packagePrefix;
        this.parent = parent;
        makeIcons();
    }

    private void makeIcons() {
        //ImageView i = new ImageView(new Image(getClass().getResourceAsStream("/" + packagePrefix + "_tray.png")));
        //getChildren().add(i);
        ImageView temp;
        for (int i = 0; i < 12; i++) {
            int index = i;
            temp = new ImageView(new Image(getClass().getResourceAsStream("/icons/pieceIcon_" + packagePrefix + "_" + (i + 1) + ".png"), 50, 50, true, false));
            temp.setLayoutY(i * (temp.getImage().getHeight()) + yOffset);
            temp.setLayoutX(xOffset);
            temp.setOnMouseClicked((MouseEvent e) -> {
                parent.getComManager().sendTrayPieceSelected(index);
            });
            getChildren().add(temp);
        }
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        DropShadow borderGlow = new DropShadow();
        borderGlow.setHeight(50);
        borderGlow.setWidth(50);
        borderGlow.setColor(Color.WHITE);
        setEffect(borderGlow);
    }

}
