package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.beans.property.*;

public class OMRSymbol {
    ObjectProperty<GraphicalToken> graphicalToken;
    DoubleProperty x;
    DoubleProperty y;
    DoubleProperty width;
    DoubleProperty height;
    /**
     * Whether it has been accepted by the user
     */
    private BooleanProperty accepted;



    public OMRSymbol(GraphicalToken graphicalToken, double x, double y, double width, double height) {
        this.graphicalToken = new SimpleObjectProperty<>(graphicalToken);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.accepted = new SimpleBooleanProperty(false);
    }

    public OMRSymbol(Symbol symbol) {
        this(symbol.getGraphicalToken(), symbol.getX(), symbol.getY(), symbol.getWidth(), symbol.getHeight());
        this.accepted.setValue(symbol.isAccepted());
    }

    public ObjectProperty<GraphicalToken> graphicalTokenProperty() {
        return graphicalToken;
    }

    public GraphicalToken getGraphicalToken() {
        return graphicalToken.get();
    }

    public void setGraphicalToken(GraphicalToken graphicalToken) {
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

    public boolean isAccepted() {
        return accepted.get();
    }

    public BooleanProperty acceptedProperty() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted.set(accepted);
    }

    public Symbol createPOJO() {
        Symbol pojoSymbol = new Symbol(getGraphicalToken(), getX(), getY(), getWidth(), getHeight());
        pojoSymbol.setAccepted(accepted.get());
        return pojoSymbol;
    }
}
