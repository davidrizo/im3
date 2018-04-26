package es.ua.dlsi.im3.core.adt.graphics;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * A bounding box sortable first by X, then by Y
 * @autor drizo
 */
public class BoundingBoxXY extends BoundingBox implements Comparable<BoundingBoxXY> {
    public BoundingBoxXY(double fromX, double fromY, double toX, double toY) throws IM3Exception {
        super(fromX, fromY, toX, toY);
    }

    public BoundingBoxXY(BoundingBox boundingBox) {
        super(boundingBox);
    }

    @Override
    public int compareTo(BoundingBoxXY o) {
        if (fromX < o.getFromX()) {
            return -1;
        } else if (fromX > o.getFromX()) {
            return 1;
        } else if (fromY < o.getFromY()) {
            return -1;
        } else if (fromY > o.getFromY()) {
            return 1;
        } else if (toX < o.getToX()) {
            return -1;
        } else if (toX > o.getToX()) {
            return 1;
        } else if (toY < o.getToY()) {
            return -1;
        } else if (toY > o.getToY()) {
            return 1;
        } else {
            return 0;
        }
    }

}
