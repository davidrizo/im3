package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.Mode;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Visual representation of key
 * @autor drizo
 */
public class KeyView {
    private final Text text;
    Group group;
    Rectangle rectangle;
    Key key;

    public KeyView(Key key) throws IM3Exception {
        this.key = key;
        group = new Group();
        rectangle = new Rectangle();
        rectangle.setHeight(100); //TODO
        rectangle.setFill(keyToColor(key));
        text = new Text(key.getAbbreviationString());
        text.xProperty().bind(rectangle.xProperty());
        text.yProperty().bind(rectangle.yProperty());
        group.getChildren().add(rectangle);
        group.getChildren().add(text);
    }

    public Node getRoot() {
        return group;
    }


    private Color keyToColor(Key key) throws IM3Exception {
        double hue = 12 + key.getFifths()*11;
        double saturation;
        double brightness;
        if (key.getMode() == Mode.MAJOR) {
            saturation = 1;
            brightness = 1;
        } else {
            saturation = 0.5;
            brightness = 0.5;
        }
        Color color = Color.hsb(hue % 360, saturation, brightness); //TODO
        return color;
    }

    public Key getKey() {
        return key;
    }

    public void setX(double x) {
        rectangle.setX(x);
    }

    public DoubleProperty yProperty() {
        return rectangle.yProperty();
    }

    public void setEndX(double x) {
        rectangle.setWidth(x - rectangle.getX());
    }

    public double getHeight() {
        return rectangle.getHeight();
    }

    public double getX() {
        return rectangle.getX();
    }

    public double getWidth() {
        return rectangle.getWidth();
    }

    public double getY() {
        return rectangle.getY();
    }
}
