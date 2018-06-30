package es.ua.dlsi.im3.core.score.harmony;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Degree;

/**
 * Created by drizo on 20/6/17.
 */
public class QualifiedDegree {
    Degree degree;
    DegreeType degreeType;

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    @Override
    public String toString() {
        switch (degreeType) {
            case major:
                return degree.name().toUpperCase();
            case minor:
                return degree.name().toLowerCase();
            case augmented:
                return degree.name().toUpperCase() + "aug";
            case diminished:
                return degree.name().toUpperCase() + "dim";
            default:
                throw new IM3RuntimeException("Invalid degree type: " + degreeType);
        }
    }
}
