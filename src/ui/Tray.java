package ui;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 */
public class Tray extends Group {

    private String packagePrefix;

    public Tray(String packagePrefix) {
        this.packagePrefix = packagePrefix;
        makeIcons();
    }

    private void makeIcons() {
        ImageView i = new ImageView(new Image(getClass().getResourceAsStream("/" + packagePrefix + "_tray.png")));
        getChildren().add(i);
    }

}
