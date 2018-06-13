package es.ua.dlsi.im3.mavr.model.harmony;

import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;

/**
 * @autor drizo
 */
public class EdgeChordDistanceLabel implements IEdgeLabel {
    double distance;

    public EdgeChordDistanceLabel(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(IEdgeLabel e) {
        return distance == ((EdgeChordDistanceLabel)e).distance;
    }

    public double getDistance() {
        return distance;
    }
}
