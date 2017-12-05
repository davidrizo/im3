package es.ua.dlsi.im3.omr.segmentation;

import es.ua.dlsi.im3.omr.traced.Coordinate;

/**
 * Currently we use rectangular regions
 */
public class PageSegment {
    RegionType segmentType;
    Coordinate leftTop;
    Coordinate bottomRight;

    public PageSegment(RegionType segmentType, Coordinate leftTop, Coordinate bottomRight) {
        this.segmentType = segmentType;
        this.leftTop = leftTop;
        this.bottomRight = bottomRight;
    }

    public PageSegment(RegionType segmentType, int leftTopX, int leftTopY, int rightBottomX, int rightBottomY) {
        this.segmentType = segmentType;
        this.leftTop = new Coordinate(leftTopX, leftTopY);
        this.bottomRight = new Coordinate(rightBottomX, rightBottomY);
    }

    public RegionType getSegmentType() {
        return segmentType;
    }

    public Coordinate getLeftTop() {
        return leftTop;
    }

    public Coordinate getBottomRight() {
        return bottomRight;
    }
}
