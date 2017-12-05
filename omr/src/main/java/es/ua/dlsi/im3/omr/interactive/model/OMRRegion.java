package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.omr.segmentation.RegionType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class OMRRegion {
    DoubleProperty fromX;
    DoubleProperty fromY;
    DoubleProperty toX;
    DoubleProperty toY;
    ObjectProperty<RegionType> regionType;

    public OMRRegion(double fromX, double fromY, double toX, double toY, RegionType regionType) {
        this.fromX = new SimpleDoubleProperty(fromX);
        this.fromY = new SimpleDoubleProperty(fromY);
        this.toX = new SimpleDoubleProperty(toX);
        this.toY = new SimpleDoubleProperty(toY);
        this.regionType = new SimpleObjectProperty<>(regionType);
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

    public double getToX() {
        return toX.get();
    }

    public void setToX(double toX) {
        this.toX.setValue(toX);
    }

    public double getToY() {
        return toY.get();
    }

    public void setToY(double toY) {
        this.toY.setValue(toY);
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

    public DoubleProperty toXProperty() {
        return toX;
    }

    public DoubleProperty fromYProperty() {
        return fromY;
    }

    public DoubleProperty toYProperty() {
        return toY;
    }

    public ObjectProperty<RegionType> regionTypeProperty() {
        return regionType;
    }
}
