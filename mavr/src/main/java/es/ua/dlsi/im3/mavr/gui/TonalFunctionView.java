package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.TonalFunction;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @autor drizo
 */
public class TonalFunctionView {
    private final Rectangle rectangle;
    private final KeyView keyView;
    Text text;
    Group group;
    TonalFunction tonalFunction;

    public TonalFunctionView(TonalFunction tonalFunction, KeyView keyView) {
        this.tonalFunction = tonalFunction;
        this.keyView = keyView;
        group = new Group();

        rectangle = new Rectangle();
        group.getChildren().add(rectangle);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.WHEAT);
        rectangle.setHeight(keyView.getHeight());

        text = new Text(tonalFunction.getAbbr());
        group.getChildren().add(text);
    }

    public TonalFunction getTonalFunction() {
        return tonalFunction;
    }

    public Node getRoot() {
        return group;
    }

    public void setX(double x) {
        rectangle.setX(x);
    }

    public DoubleProperty yProperty() {
        return rectangle.yProperty();
    }

    public void setEndX(double x) throws IM3Exception {
        rectangle.setWidth(x - rectangle.getX());

        positionText();
    }

    private void positionText() throws IM3Exception {
        double xmultiplier;
        double ymultiplier;
        switch (tonalFunction) {
            case TONIC:
                xmultiplier = 0.5; // center
                ymultiplier = 0.5;
                break;
            case DOMINANT:
                xmultiplier = 0.9; // top right
                ymultiplier = 0.1;
                break;
            case SUBDOMINANT:
                xmultiplier = 0.7; // between both
                ymultiplier = 0.7;
                break;
            default:
                throw new IM3Exception("Invalid tonal function: " + tonalFunction);
        }
        text.setX(rectangle.getX() + rectangle.getWidth()*xmultiplier - text.getLayoutBounds().getWidth()/2);
        text.setY(rectangle.getY() + rectangle.getHeight()*ymultiplier  + text.getLayoutBounds().getHeight()/2);

    }
}
