package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OMRSymbol {
    ObjectProperty<GraphicalToken> graphicalToken;
    DoubleProperty x;
    DoubleProperty y;
    DoubleProperty width;
    DoubleProperty height;

    public OMRSymbol(GraphicalToken graphicalToken, double x, double y, double width, double height) {
        this.graphicalToken = new SimpleObjectProperty<>(graphicalToken);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
    }

    public OMRSymbol(Symbol symbol) {
        this(symbol.getGraphicalToken(), symbol.getX(), symbol.getY(), symbol.getWidth(), symbol.getHeight());
    }

    public ObjectProperty<GraphicalToken> graphicalTokenProperty() {
        return graphicalToken;
    }

    public GraphicalToken getGraphicalToken() {
        return graphicalToken.get();
    }

    public void setGraphicalSymbol(GraphicalToken graphicalToken) {
        this.graphicalToken.set(graphicalToken);
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public double getWidth() {
        return width.get();
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }
}
