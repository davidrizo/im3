package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import javafx.beans.property.*;

/**
 * Order by x, then by y
 */
public class OMRSymbol implements IOMRBoundingBox, Comparable<OMRSymbol> {
    OMRRegion omrRegion;
    /**
     * Agnostic symbol type and its position in staff
     */
    ObjectProperty<AgnosticSymbol> graphicalSymbol;

    /**
     * Absolute value
     */
    DoubleProperty x;
    /**
     * Absolute value
     */
    DoubleProperty y;
    /**
     * Width of the bounding box
     */
    DoubleProperty width;
    /**
     * Height of the bounding box
     */
    DoubleProperty height;
    /**
     * Whether it has been accepted by the user
     */
    private BooleanProperty accepted;
    /**
     * Name using agnostic encoding
     */
    private StringProperty name;


    public OMRSymbol(OMRRegion omrRegion, AgnosticSymbol graphicalSymbol, double x, double y, double width, double height) throws IM3Exception {
        if (x < 0) {
            throw new IM3Exception("Cannot build a symbol with fromX (" + x + ") < 0");
        }
        if (y < 0) {
            throw new IM3Exception("Cannot build a symbol with fromY (" + y + ") < 0");
        }
        if (width <= 0) {
            throw new IM3Exception("Cannot build a symbol with width (" + width + ") <= 0");
        }
        if (height <= 0) {
            throw new IM3Exception("Cannot build a symbol with height (" + height + ") <= 0");
        }

        this.omrRegion = omrRegion;
        this.graphicalSymbol = new SimpleObjectProperty<>(graphicalSymbol);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.accepted = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty();
        this.name.bind(this.graphicalSymbol.asString());
    }

    public OMRSymbol(OMRRegion omrRegion, Symbol symbol) throws IM3Exception {
        this(omrRegion, symbol.getAgnosticSymbol(), symbol.getBoundingBox().getFromX(), symbol.getBoundingBox().getFromY(), symbol.getWidth(), symbol.getHeight());
        this.accepted.setValue(symbol.isAccepted());
        this.name = new SimpleStringProperty();
        this.name.bind(this.graphicalSymbol.asString());
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

    @Override
    public DoubleProperty fromXProperty() {
        return x;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    @Override
    public DoubleProperty fromYProperty() {
        return y;
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

    @Override
    public StringProperty nameProperty() {
        return name;
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

    public AgnosticSymbol getGraphicalSymbol() {
        return graphicalSymbol.get();
    }

    public ObjectProperty<AgnosticSymbol> graphicalSymbolProperty() {
        return graphicalSymbol;
    }

    public void setGraphicalSymbol(AgnosticSymbol graphicalSymbol) {
        this.graphicalSymbol.set(graphicalSymbol);
    }

    public Symbol createPOJO() throws IM3Exception {
        Symbol pojoSymbol = new Symbol(graphicalSymbol.get(), x.get(), y.get(), x.get()+width.get(), y.get()+height.get());
        pojoSymbol.setAccepted(accepted.get());
        return pojoSymbol;
    }

    public OMRRegion getOMRRegion() {
        return omrRegion;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public void setOMRRegion(OMRRegion omrRegion) throws IM3Exception {
        if (omrRegion == null) {
            throw new IM3Exception("Cannot set a null region");
        }
        if (this.omrRegion != null) {
            this.omrRegion.removeSymbol(this);
        }
        this.omrRegion = omrRegion;
        if (!this.omrRegion.containsSymbol(this)) {
            this.omrRegion.addSymbol(this);
        }
    }

    @Override
    public int compareTo(OMRSymbol o) {
        if (x.get() < o.getX()) {
            return -1;
        } else if (x.get() > o.getX()) {
            return 1;
        } else if (y.get() < o.getY()) {
            return -1;
        } else if (y.get() > o.getY()) {
            return 1;
        } else {
            return graphicalSymbol.hashCode() - o.graphicalSymbol.hashCode(); // any sorting
        }
    }

    public double getCenterY() {
        return y.get() + height.get()/2.0;
    }

    public double getCenterX() {
        return x.get() + width.get()/2.0;
    }
}
