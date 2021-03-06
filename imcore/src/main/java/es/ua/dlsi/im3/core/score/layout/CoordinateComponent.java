package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 * This represents the x or the y axis value of a coordinate.
 * Used to mantain relations between coordinates.
 * An absolute value will be represented with an empty "reference"
 * and a displacement value. If two coordinates FROM and TO are in the same
 * point, the reference value of TO will point to FROM and displacement will be
 * 0. If we want it to be displaced, this displacement value can be assigned
 * either a positive or a negative value
 */
public class CoordinateComponent {
    CoordinateComponent reference;
    double displacement;

    public CoordinateComponent() {
        this.reference = null;
        displacement = 0;
    }


    public CoordinateComponent(CoordinateComponent reference) {
        this.reference = reference;
        displacement = 0;
    }

    public CoordinateComponent(double displacement) {
        this.reference = null;
        this.displacement = displacement;
    }

    public CoordinateComponent(CoordinateComponent reference, double displacement) {
        this.reference = reference;
        this.displacement = displacement;
    }

    public CoordinateComponent getReference() {
        return reference;
    }

    public void setReference(CoordinateComponent reference) {
        if (this == reference) {
            throw new IM3RuntimeException("Cannot set a reference to the coordinate component itself: " + this);
        }
        this.reference = reference;
    }

    public double getDisplacement() {
        return displacement;
    }

    public void setDisplacement(double displacement) {
        this.displacement = displacement;
    }

    public double getAbsoluteValue() {
        if (reference == null) {
            return displacement;
        } else {
            return reference.getAbsoluteValue() + displacement;
        }
    }

    public double getAccumulatedDisplacement() {
        if (reference == null) {
            return displacement;
        } else {
            // TODO: 22/9/17 Ver cómo hacer para que no se repita tantas veces su cálculo ¿propiedad dirty?
            return reference.getAccumulatedDisplacement() + displacement;
        }
    }
}
