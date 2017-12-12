package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.omr.model.pojo.Region;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class OMRRegion {
    DoubleProperty fromX;
    DoubleProperty fromY;
    DoubleProperty width;
    DoubleProperty height;
    ObjectProperty<RegionType> regionType;
    ObservableList<OMRSymbol> symbolList;

    public OMRRegion(double fromX, double fromY, double width, double height, RegionType regionType) {
        this.fromX = new SimpleDoubleProperty(fromX);
        this.fromY = new SimpleDoubleProperty(fromY);
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.regionType = new SimpleObjectProperty<>(regionType);
        this.symbolList = FXCollections.observableList(new LinkedList<>());
    }

    public OMRRegion(Region region) {
        this.fromX = new SimpleDoubleProperty(region.getFromX());
        this.fromY = new SimpleDoubleProperty(region.getFromY());
        this.width = new SimpleDoubleProperty(region.getToX()-region.getFromX());
        this.height = new SimpleDoubleProperty(region.getToY()-region.getFromY());
        this.regionType = new SimpleObjectProperty<>(region.getRegionType());
        this.symbolList = FXCollections.observableList(new LinkedList<>());
        if (region.getSymbols() != null) {
            for (Symbol symbol : region.getSymbols()) {
                // use the height of the region
                this.symbolList.add(new OMRSymbol(symbol.getGraphicalToken(), symbol.getX(), fromY.get(), symbol.getWidth(), height.get()));
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

    public ObjectProperty<RegionType> regionTypeProperty() {
        return regionType;
    }

    public ObservableList<OMRSymbol> symbolListProperty() {
        return symbolList;
    }

    public void clearSymbols() {
        symbolList.clear();
    }

    public void addSymbol(OMRSymbol s) {
        symbolList.add(s);
    }
}
