package es.ua.dlsi.grfia.im3ws.muret.entity;

import java.util.Objects;

/**
 * This is not persistent, it is constructed from the string fromX,fromY,toX,toY stored in the database
 */
public class BoundingBox {
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public BoundingBox(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }


    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return fromX == that.fromX &&
                fromY == that.fromY &&
                toX == that.toX &&
                toY == that.toY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromX, fromY, toX, toY);
    }

    public int getWidth() {
        return this.toX - this.fromX;
    }

    public void setWidth(int width) {
        this.toX = this.fromX + width;
    }

    public boolean contains(int x, int y) {
        return x >= this.fromX && x <= this.toX
                && y >= this.fromY && y <= this.toY;

    }

    public void setHeight(int height) {
        this.toY = this.fromY + height;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "fromX=" + fromX +
                ", fromY=" + fromY +
                ", toX=" + toX +
                ", toY=" + toY +
                '}';
    }
}
