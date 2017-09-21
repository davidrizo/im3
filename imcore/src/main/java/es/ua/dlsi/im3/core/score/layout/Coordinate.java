package es.ua.dlsi.im3.core.score.layout;

public class Coordinate {
    CoordinateComponent x;
    CoordinateComponent y;

    public Coordinate(CoordinateComponent x, CoordinateComponent y) {
        this.x = x;
        this.y = y;
    }

    public CoordinateComponent getX() {
        return x;
    }

    public void setX(CoordinateComponent x) {
        this.x = x;
    }

    public CoordinateComponent getY() {
        return y;
    }

    public void setY(CoordinateComponent y) {
        this.y = y;
    }

    public double getAbsoluteX() {
        return x.getAbsoluteValue();
    }

    public double getAbsoluteY() {
        return y.getAbsoluteValue();
    }

    public static Coordinate min(Coordinate a, Coordinate b) {
        double diff = a.getAbsoluteY() - b.getAbsoluteY();
        if (diff < 0.0) {
            return a;
        } else if (diff > 0.0) {
            return b;
        } else {
            diff = a.getAbsoluteX() - b.getAbsoluteX();
            if (diff < 0.0) {
                return a;
            } else if (diff > 0.0) {
                return b;
            } else {
                return a;
            }
        }
    }
}
