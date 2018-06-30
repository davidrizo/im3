package es.ua.dlsi.im3.mavr.model.graph;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;

/**
 * @autor drizo
 */
public class ConceptRelationEdgeLabel implements IEdgeLabel {
    /**
     * From 0 to 1
     */
    double percentage;

    /**
     * From 0 to 1
     * @param percentage
     */
    public ConceptRelationEdgeLabel(double percentage) throws IM3Exception {
        this.percentage = percentage;
        if (percentage < 0.0 || percentage > 1.0) {
            throw new IM3Exception("Percentage must be [0,1], and it is " + percentage);
        }
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(IEdgeLabel e) {
        return percentage == ((ConceptRelationEdgeLabel)e).percentage;
    }

    @Override
    public String toString() {
        return Double.toString(percentage);
    }
}
