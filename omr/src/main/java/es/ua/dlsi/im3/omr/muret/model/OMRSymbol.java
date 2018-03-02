package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.beans.property.*;

public class OMRSymbol {
    ObjectProperty<GraphicalSymbol> graphicalSymbol;
    StringProperty value;
    ObjectProperty<PositionInStaff> positionInStaff;

    /**
     * Relative to the retion
     */
    DoubleProperty x;
    DoubleProperty y;
    DoubleProperty width;
    DoubleProperty height;
    /**
     * Whether it has been accepted by the user
     */
    private BooleanProperty accepted;


    public OMRSymbol(GraphicalSymbol graphicalSymbol, PositionInStaff positionInStaff, String value, double x, double y, double width, double height) {
        this.graphicalSymbol = new SimpleObjectProperty<>(graphicalSymbol);
        this.positionInStaff = new SimpleObjectProperty<>(positionInStaff);
        this.value = new SimpleStringProperty(value);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.accepted = new SimpleBooleanProperty(false);
    }

    public OMRSymbol(Symbol symbol) {
        this(symbol.getGraphicalToken().getSymbol(), symbol.getGraphicalToken().getPositionInStaff(), symbol.getGraphicalToken().getValue(), symbol.getX(), symbol.getY(), symbol.getWidth(), symbol.getHeight());
        this.accepted.setValue(symbol.isAccepted());
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

    public GraphicalSymbol getGraphicalSymbol() {
        return graphicalSymbol.get();
    }

    public ObjectProperty<GraphicalSymbol> graphicalSymbolProperty() {
        return graphicalSymbol;
    }

    public void setGraphicalSymbol(GraphicalSymbol graphicalSymbol) {
        this.graphicalSymbol.set(graphicalSymbol);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public PositionInStaff getPositionInStaff() {
        return positionInStaff.get();
    }

    public ObjectProperty<PositionInStaff> positionInStaffProperty() {
        return positionInStaff;
    }

    public void setPositionInStaff(PositionInStaff positionInStaff) {
        this.positionInStaff.set(positionInStaff);
    }

    public Symbol createPOJO() {
        Symbol pojoSymbol = new Symbol(new GraphicalToken(graphicalSymbol.get(), value.get(), positionInStaff.get()), getX(), getY(), getWidth(), getHeight());
        pojoSymbol.setAccepted(accepted.get());
        return pojoSymbol;
    }
}
