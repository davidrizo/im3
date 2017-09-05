package es.ua.dlsi.im3.core.score.harmony;

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
        return "QualifiedDegree{" +
                "degree=" + degree +
                ", degreeType=" + degreeType +
                '}';
    }
}
