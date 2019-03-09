package es.ua.dlsi.im3.core.adt.graphics;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Objects;

/**
 * @autor drizo
 */
public abstract class BoundingBox  {
    /**
     * Left corner X, relative to the region where it is contained
     */
    protected double fromX;
    /**
     * Left corner Y, relative to the region where it is contained
     */
    protected double fromY;
    /**
     * Bottom corner X, relative to the region where it is contained
     */
    protected double toX;
    /**
     * Bottom corner Y, relative to the region where it is contained
     */
    protected double toY;

    public BoundingBox(double fromX, double fromY, double toX, double toY) throws IM3Exception {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        if (fromX >= toX) {
            throw new IM3Exception("fromX (" + fromX + ") must be less than toX (" + toX + ")");
        }
        if (fromY >= toY) {
            throw new IM3Exception("fromY (" + fromY + ") must be less than toY (" + toY + ")");
        }
    }

    public BoundingBox(BoundingBox boundingBox) {
        this.fromX = boundingBox.fromX;
        this.fromY = boundingBox.fromY;
        this.toX = boundingBox.toX;
        this.toY = boundingBox.toY;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return Double.compare(that.fromX, fromX) == 0 &&
                Double.compare(that.fromY, fromY) == 0 &&
                Double.compare(that.toX, toX) == 0 &&
                Double.compare(that.toY, toY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromX, fromY, toX, toY);
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

    public double getWidth() {
        return toX - fromX;
    }

    public double getHeight() {
        return toY - fromY;
    }

    /**
     * It returns the center of the symbol
     * @return [x,y]
     */
    public double[] getCenter() {
        double [] result = new double[2];
        result[0] = (fromX + toX) / 2.0;
        result[1] = (fromY + toY) / 2.0;
        return result;
    }

    public boolean contains(double x, double y) {
        return x >= fromX && x <= toX && y >= fromY && y <= toY;
    }

    /**
     * Equals where an absolute difference threshold is taken into accound
     * @param other
     * @param threshold
     * @return
     */
    public boolean equals(BoundingBox other, double threshold) {
        if (Math.abs(fromX - other.fromX) > threshold) {
            return false;
        }
        if (Math.abs(fromY - other.fromY) > threshold) {
            return false;
        }
        if (Math.abs(toX - other.toX) > threshold) {
            return false;
        }
        if (Math.abs(toY - other.toY) > threshold) {
            return false;
        }
        return true;
    }

    public boolean overlaps(BoundingBox boundingBox) {
        return boundingBox.contains(fromX, fromY) || boundingBox.contains(fromX, toY) || boundingBox.contains(toX, fromY) || boundingBox.contains(toX, toY)
                || contains(boundingBox.fromX, boundingBox.fromY)  || contains(boundingBox.fromX, boundingBox.toY) || contains(boundingBox.toX, boundingBox.fromY) || contains(boundingBox.toX, boundingBox.toY);


    }

    /**
     *
     * @param boundingBox
     * @return True if the center of the boundingBox is contained inside this bounding box
     */
    public boolean containsCenterOf(BoundingBox boundingBox) {
        double [] center = boundingBox.getCenter();
        return this.contains(center[0], center[1]);
    }
}
