package es.ua.dlsi.im3.omr.muret;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @autor drizo
 */
public class Utils {
    public static Button addOpenOtherProjectButton(String imagePath, double width, double height, String cssClass, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button();
        if (cssClass != null) {
            button.getStyleClass().add(cssClass);
        }
        Image image = new Image(Utils.class.getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width); //TODO Does not work with css
        imageView.setFitHeight(height);
        button.setGraphic(imageView);

        button.setOnAction(eventHandler);

        return button;
    }
}
