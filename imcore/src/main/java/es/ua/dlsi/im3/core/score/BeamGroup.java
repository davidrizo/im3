package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.List;

/**
 * The elements inside this object must replace the flags in the stems for the number of required beams given their duration.
 * A group 8th-16th-16th needs just a beam. We follow here the MEI rationale for beams.
 */
public class BeamGroup {
    /**
     * Whether it has been computed or explicitly specified
     */
    private boolean computed;
    /**
     * If null a broken beam is created
     */
    private SingleFigureAtom from;
    /**
     * If null a broken beam is created
     */
    private SingleFigureAtom to;

    private ArrayList<SingleFigureAtom> includedFigures;

    public BeamGroup(boolean computed) {
        includedFigures = new ArrayList<>();
        this.computed = computed;
    }

    public SingleFigureAtom getFrom() {
        return from;
    }
    public SingleFigureAtom getTo() {
        return to;
    }

    public void add(SingleFigureAtom singleFigureAtom) {
        includedFigures.add(singleFigureAtom);
        singleFigureAtom.setBelongsToBeam(this);
        if (from == null || singleFigureAtom.getTime().compareTo(from.getTime()) < 0) {
            from = singleFigureAtom;
        }
        if (to == null || to.getTime().compareTo(singleFigureAtom.getTime()) < 0) {
            to = singleFigureAtom;
        }
    }

    public List<SingleFigureAtom> getIncludedFigures() {
        return includedFigures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeamGroup beamGroup = (BeamGroup) o;

        if (from != null ? !from.equals(beamGroup.from) : beamGroup.from != null) return false;
        return to != null ? to.equals(beamGroup.to) : beamGroup.to == null;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    public boolean isComputed() {
        return computed;
    }
}
