package es.ua.dlsi.im3.omr.model.entities;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An page in an image. Usually we'll find one or two imagesold in the same image. Compared first X, then Y
 * @autor drizo
 */
public class Page implements Comparable<Page> {
    /**
     * Bounding box, absolute values
     */
    BoundingBoxXY boundingBox;
    /**
     * Instrument, it may be null if not the same for all regions or if defined in a higher level
     */
    Instrument instrument;
    /**
     * Sorted set of regions
     */
    SortedSet<Region> regions;
    /**
     * Editorial comments
     */
    private String comments;

    public Page(int fromX, int fromY, int toX, int toY) throws IM3Exception {
        boundingBox = new BoundingBoxXY(fromX, fromY, toX, toY);
        regions = new TreeSet<>();
    }

    public Page(BoundingBoxXY boundingBox) {
        this.boundingBox = boundingBox;
        regions = new TreeSet<>();
    }

    public SortedSet<Region> getRegions() {
        return regions;
    }

    public void setRegions(SortedSet<Region> regions) {
        this.regions = regions;
    }

    public void add(Region region) {
        regions.add(region);
        region.setPage(this);
    }

    public BoundingBoxXY getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxXY boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public int compareTo(Page o) {
        if (boundingBox == null || o.boundingBox == null) {
            throw new IM3RuntimeException("Cannot compare imagesold without bounding boxes");
        }
        return boundingBox.compareTo(o.boundingBox);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(boundingBox, page.boundingBox) &&
                Objects.equals(instrument, page.instrument) &&
                Objects.equals(regions, page.regions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boundingBox, instrument, regions);
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }
}
