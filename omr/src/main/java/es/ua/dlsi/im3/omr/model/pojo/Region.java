package es.ua.dlsi.im3.omr.model.pojo;

import java.util.LinkedList;
import java.util.List;

/**
 * It is comparable against coordinate
 */
public class Region implements Comparable<Region> {
    double fromX;
    double fromY;
    double toX;
    double toY;
    RegionType regionType;
    List<Symbol> symbols;

    public Region(RegionType regionType, double fromX, double fromY, double toX, double toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.regionType = regionType;
        symbols = new LinkedList<>();
    }

    public double getFromX() {
        return fromX;
    }

    public void setFromX(double fromX) {
        this.fromX = fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public void setFromY(double fromY) {
        this.fromY = fromY;
    }

    public double getToX() {
        return toX;
    }

    public void setToX(double toX) {
        this.toX = toX;
    }

    public double getToY() {
        return toY;
    }

    public void setToY(double toY) {
        this.toY = toY;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    @Override
    public int compareTo(Region o) {
        if (fromY < o.getFromY()) {
            return -1;
        } else if (fromY > o.getFromY()) {
            return 1;
        } else if (fromX < o.getFromX()) {
            return -1;
        } else if (fromX > o.getFromX()) {
            return 1;
        } else if (toY < o.getToY()) {
            return -1;
        } else if (toY > o.getToY()) {
            return 1;
        } else if (toX < o.getToX()) {
            return -1;
        } else if (toX > o.getToX()) {
            return 1;
        } else {
            return regionType.compareTo(o.regionType);
        }
    }
}
