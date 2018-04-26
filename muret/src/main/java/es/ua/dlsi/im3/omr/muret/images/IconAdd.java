package es.ua.dlsi.im3.omr.muret.images;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class IconAdd {
    Circle background;
    Text textPlus;
    StackPane stackPane;

    public IconAdd() {
        textPlus = new Text("+");
        textPlus.setFont(Font.font(40)); //TODO mejor icono svg
        textPlus.setFill(Color.WHITE);
        background = new Circle(40);
        background.setFill(Color.DARKBLUE);
        stackPane = new StackPane(background, textPlus);
    }

    public Node getRoot() {
        return stackPane;
    }

    public void highlight(boolean highlight) {
        if (highlight) {
            textPlus.setFill(Color.GRAY);
        } else {
            textPlus.setFill(Color.WHITE);
        }

    }
}
