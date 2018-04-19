package es.ua.dlsi.im3.core.adt.graphics;

/**
 * @autor drizo
 */
public class BoundingBox implements Comparable<BoundingBox> {
    /**
     * Left corner X, relative to the region where it is contained
     */
    private double fromX;
    /**
     * Left corner Y, relative to the region where it is contained
     */
    private double fromY;
    /**
     * Bottom corner X, relative to the region where it is contained
     */
    private double toX;
    /**
     * Bottom corner Y, relative to the region where it is contained
     */
    private double toY;

    public BoundingBox(double fromX, double fromY, double toX, double toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
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

    @Override
    public int compareTo(BoundingBox o) {
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

    @Override
    public String toString() {
        return "RectangularRegion{" +
                "fromX=" + fromX +
                ", fromY=" + fromY +
                ", toX=" + toX +
                ", toY=" + toY +
                '}';
    }
}
