package es.ua.dlsi.im3.core.score.harmony;

import java.util.Arrays;

/**
 * Created by drizo on 20/6/17.
 */
public class NonFunctionalChord extends ChordSpecification {
    QualifiedDegree degree;

    /**
     * In IIIM3A4, intervals[0] = M3, intervals[1] = A4
     */
    ChordInterval [] intervals;

    public QualifiedDegree getDegree() {
        return degree;
    }

    public void setDegree(QualifiedDegree degree) {
        this.degree = degree;
    }

    public void setIntervals(ChordInterval[] intervals) {
        this.intervals = intervals;
    }

    public ChordInterval[] getIntervals() {
        return intervals;
    }

    @Override
    public String toString() {
        return "NonFunctionalChord{" +
                "implicit=" + implicit +
                ", degree=" + degree +
                ", intervals=" + Arrays.toString(intervals) +
                '}';
    }
}
