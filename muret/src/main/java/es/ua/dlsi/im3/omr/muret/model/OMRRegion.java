package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.RegionType;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.LinkedList;
import java.util.TreeSet;

public class OMRRegion implements Comparable<OMRRegion>, IOMRBoundingBox {
    private final OMRPage omrPage;
    /**
     * ID just used to identify it from the GUI
     */
    int id;
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty fromX;
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty fromY;
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty width;
    /**
     * Bounding box height
     */
    DoubleProperty height;
    /**
     * Name based on type
     */
    StringProperty name;
    /**
     * Region type
     */
    ObjectProperty<RegionType> regionType;
    /**
     * Set of ordered symbols
     */
    ObservableSet<OMRSymbol> symbols;


    public OMRRegion(OMRPage omrPage, int id, double fromX, double fromY, double width, double height, RegionType regionType) {
        this.id = id;
        this.omrPage = omrPage;
        this.fromX = new SimpleDoubleProperty(fromX);
        this.fromY = new SimpleDoubleProperty(fromY);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.regionType = new SimpleObjectProperty<>(regionType);
        this.symbols = FXCollections.observableSet(new TreeSet<>());
        name = new SimpleStringProperty();
        name.bind(this.regionType.asString().concat(" #" + id));
    }

    public OMRRegion(OMRPage omrPage, int id, Region region) {
        this.id = id;
        this.omrPage = omrPage;
        this.fromX = new SimpleDoubleProperty(region.getBoundingBox().getFromX());
        this.fromY = new SimpleDoubleProperty(region.getBoundingBox().getFromY());
        this.width = new SimpleDoubleProperty(region.getBoundingBox().getToX()-region.getBoundingBox().getFromX());
        this.height = new SimpleDoubleProperty(region.getBoundingBox().getToY()-region.getBoundingBox().getFromY());
        this.regionType = new SimpleObjectProperty<>(region.getRegionType());
        this.symbols = FXCollections.observableSet(new TreeSet<>());
        name = new SimpleStringProperty();
        name.bind(this.regionType.asString().concat(" #" + id));
        if (region.getSymbols() != null) {
            for (Symbol symbol : region.getSymbols()) {
                OMRSymbol omrSymbol = new OMRSymbol(this, symbol);
                this.symbols.add(omrSymbol);
            }
        }
    }

    public double getFromX() {
        return fromX.get();
    }

    public void setFromX(double fromX) {
        this.fromX.setValue(fromX);
    }

    public double getFromY() {
        return fromY.get();
    }

    public void setFromY(double fromY) {
        this.fromY.setValue(fromY);
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.setValue(width);
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.setValue(height);
    }

    public RegionType getRegionType() {
        return regionType.get();
    }

    public void setRegionType(RegionType regionType) {
        this.regionType.setValue(regionType);
    }

    public DoubleProperty fromXProperty() {
        return fromX;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty fromYProperty() {
        return fromY;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<RegionType> regionTypeProperty() {
        return regionType;
    }

    public ObservableSet<OMRSymbol> symbolsProperty() {
        return symbols;
    }

    public void clearSymbols() {
        symbols.clear();
    }

    public void addSymbol(OMRSymbol s) {
        symbols.add(s);
    }

    public void removeSymbol(OMRSymbol omrSymbol) {
        symbols.remove(omrSymbol);
    }

    public Region createPOJO() throws IM3Exception {
        Region pojoRegion = new Region(getRegionType(), getFromX(), getFromY(), getFromX() + getWidth(), getFromY() + getHeight());
        for (OMRSymbol omrSymbol: symbols) {
            pojoRegion.addSymbol(omrSymbol.createPOJO());
        }
        return pojoRegion;
    }

    /*public String toString() {
        return "[(" + fromX.get() + ", " + fromY.get() + "), w=" + width.get() + ", h= " + height.get() + "]";
    }*/

    @Override
    public int compareTo(OMRRegion o) {
        if (fromY.get() < o.fromY.get()) {
            return -1;
        } else if (fromY.get() > o.fromY.get()) {
            return 1;
        } else if (fromX.get() < o.fromX.get()) {
            return -1;
        } else if (fromX.get() > o.fromX.get()) {
            return 1;
        } else {
            return regionType.get().compareTo(o.regionType.get());
        }
    }

    public OMRPage getOMRPage() {
        return omrPage;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public boolean containsSymbol(OMRSymbol omrSymbol) {
        return symbols.contains(omrSymbol);
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        double offsetX = boundingBox.getFromX() - fromX.get();
        double offsetY = boundingBox.getFromX() - fromY.get();

        this.fromX.setValue(boundingBox.getFromX());
        this.fromY.setValue(boundingBox.getFromY());
        this.width.setValue(boundingBox.getToX()-boundingBox.getFromX());
        this.fromX.setValue(boundingBox.getToY()-boundingBox.getFromY());

        for (OMRSymbol omrSymbol: symbols) {
            omrSymbol.setX(omrSymbol.getX()+offsetX);
            omrSymbol.setY(omrSymbol.getX()+offsetY);
        }
    }

    public boolean containsAbsoluteCoordinate(double x, double y) {
        return x >= this.getFromX() && x <= this.getFromX()+this.getWidth()
                && y >= this.getFromY() && y <= this.getFromY()+this.getHeight();
    }
}
