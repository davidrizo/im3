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
    static ColorWheel colorWheel = new ColorWheel(12, 1, 1);

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

    private Color keyToColor(Key key) {
        int pc = key.getPitchClass().getSemitonesFromC();
        return colorWheel.getColors()[pc];
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
