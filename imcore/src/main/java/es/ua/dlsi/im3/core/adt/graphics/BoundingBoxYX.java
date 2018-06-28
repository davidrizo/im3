package es.ua.dlsi.im3.core.adt.graphics;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * A bounding box sortable first by Y, then by X
 * @autor drizo
 */
public class BoundingBoxYX extends BoundingBox implements Comparable<BoundingBoxYX> {
    public BoundingBoxYX(double fromX, double fromY, double toX, double toY) throws IM3Exception {
        super(fromX, fromY, toX, toY);
    }

    public BoundingBoxYX(BoundingBox boundingBox) {
        super(boundingBox);
    }

    @Override
    public int compareTo(BoundingBoxYX o) {
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
            return 0;
        }
    }
}
