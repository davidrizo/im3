package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxYX;

import java.util.Collection;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * It is comparable against coordinate
 */
public class Region implements Comparable<Region> {
    /**
     * Owner page, transient because we do not want to serialize it
     */
    private transient Page page;
    /**
     * Absolute values
     */
    BoundingBoxYX boundingBox;
    /**
     * Title, author, staff, etc..
     */
    private RegionType regionType;
    /**
     * Ordered (first X, then Y) of symbols contained in the region
     */
    SortedSet<Symbol> symbols;

    public Region(RegionType regionType, double fromX, double fromY, double toX, double toY) throws IM3Exception {
        if (fromX < 0) {
            throw new IM3Exception("Cannot build a region with fromX (" + fromX + ") < 0");
        }
        if (fromY < 0) {
            throw new IM3Exception("Cannot build a region with fromY (" + fromY + ") < 0");
        }
        if (toX <= fromX) {
            throw new IM3Exception("Cannot build a region with toX (" + toX + ") <= fromX (" + fromX + ")");
        }
        if (toY <= fromY) {
            throw new IM3Exception("Cannot build a region with toY (" + toY + ") <= fromY (" + fromY + ")");
        }
        boundingBox = new BoundingBoxYX(fromX, fromY, toX, toY);
        this.regionType = regionType;
        this.symbols = new TreeSet<>();
    }

    /**
     * It creates a region using the bounding box defined by the symbols
     * @param regionType
     * @param symbols
     * @throws IM3Exception
     */
    public Region(RegionType regionType, Collection<Symbol> symbols) throws IM3Exception {
        this.symbols = new TreeSet<>();
        double fromX = Double.MAX_VALUE;
        double fromY = Double.MAX_VALUE;
        double toX = 0;
        double toY = 0;

        for (Symbol symbol: symbols) {
            this.symbols.add(symbol);
            fromX = Math.min(fromX, symbol.getBoundingBox().getFromX());
            fromY = Math.min(fromY, symbol.getBoundingBox().getFromY());

            toX = Math.max(toX, symbol.getBoundingBox().getToX());
            toY = Math.max(toY, symbol.getBoundingBox().getToY());
        }

        boundingBox = new BoundingBoxYX(fromX, fromY, toX, toY);
        this.regionType = regionType;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxYX boundingBox) {
        this.boundingBox = boundingBox;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public SortedSet<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(SortedSet<Symbol> symbols) {
        this.symbols = symbols;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public int compareTo(Region o) {
        int result = boundingBox.compareTo(o.boundingBox);
        if (result == 0) {
            return regionType.compareTo(o.regionType);
        } else {
            return result;
        }
    }

    public void addSymbol(Symbol symbol) {
        this.symbols.add(symbol);
        symbol.setRegion(this);
    }

    @Override
    public String toString() {
        return "Region{" +
                "boun=" + boundingBox +
                ", regionType=" + regionType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return Objects.equals(boundingBox, region.boundingBox) &&
                regionType == region.regionType &&
                Objects.equals(symbols, region.symbols);
    }

    @Override
    public int hashCode() {

        return Objects.hash(boundingBox, regionType, symbols);
    }

    /**
     * It computes the bounding box relative to the page
     * @return
     */
    public BoundingBox computeRelativeBoundingBox() throws IM3Exception {
        if (page == null) {
            return new BoundingBoxYX(boundingBox);
        } else {
            return new BoundingBoxYX(
                    boundingBox.getFromX()-page.getBoundingBox().getFromX(),
                    boundingBox.getFromY()-page.getBoundingBox().getFromY(),
                    boundingBox.getToX()-page.getBoundingBox().getFromX(),
                    boundingBox.getToY()-page.getBoundingBox().getFromY());
        }
    }

}
