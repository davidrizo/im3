package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * The x and y cannot be changed, just their reference
 */
public class Coordinate {
    final CoordinateComponent x;
    final CoordinateComponent y;

    /**
     * If x or y null, a CoordinateComponent will be created. We can move the coordinate by changing the reference coordinate component
     * (see CoordinateComponent.reference) by setReferenceX or setReferenceY
     * @param x
     * @param y
     */
    public Coordinate(CoordinateComponent x, CoordinateComponent y) {
        if (x == null) {
            this.x = new CoordinateComponent();
        } else {
            this.x = x;
        }
        if (y == null) {
            this.y = new CoordinateComponent();
        } else {
            this.y = y;
        }
    }

    public Coordinate() {
        this.x = new CoordinateComponent();
        this.y = new CoordinateComponent();
    }

    public CoordinateComponent getX() {
        return x;
    }

    /**
     * It sets the reference, not the x itself
     * @param x
     */
    public void setX(CoordinateComponent x) {
        this.x.setReference(x);
    }

    public CoordinateComponent getY() {
        return y;
    }

    /**
     * It sets the reference, not the y itself
     * @param y
     */
    public void setReferenceY(CoordinateComponent y) {
        this.y.setReference(y);
    }

    public void setDisplacementX(double x) {this.x.setDisplacement(x);}
    public void setDisplacementY(double y) {this.y.setDisplacement(y);}

    public double getAbsoluteX() {
        return x.getAbsoluteValue();
    }

    public double getAbsoluteY() throws IM3Exception {
        if (y == null) {
            throw new IM3Exception("The y coordinate is null");
        }
        return y.getAbsoluteValue();
    }

    public static Coordinate min(Coordinate a, Coordinate b) throws IM3Exception {
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

    @Override
    public String toString() {
        return "("+ x.getAbsoluteValue() + "," + y.getAbsoluteValue() + ")";
    }

    public double getDisplacementY() {
        return y.getDisplacement();
    }
}
