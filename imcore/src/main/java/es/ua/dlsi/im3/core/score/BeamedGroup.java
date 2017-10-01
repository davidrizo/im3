package es.ua.dlsi.im3.core.score;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;


/**
 * Group of notes, rests. The number of beams should be deduced from the duration of the included atoms
 * It does not allow beams of beams.
 * @author drizo
 */
public class BeamedGroup extends CompoundAtom {
    /**
     * True if the beam is not explicitly created but computed by our layout algorithms
     */
    boolean computedBeam;

    public BeamedGroup(boolean computedBeam) throws IM3Exception {
        this.computedBeam = computedBeam;
    }

    @Override
    public void addSubatom(Atom subatom) throws IM3Exception {
        if (!(subatom instanceof SingleFigureAtom)) {
            // It should be implemented with a layering, not inheritance. Done this way for convenience
            throw new IM3Exception("Only SingleFigureAtom objects can be added to BeamedGroup, not " + subatom.getClass() );
        }
        super.addSubatom(subatom);
    }

    public boolean isComputedBeam() {
        return computedBeam;
    }



    /*@Override
    public List<PlayedScoreNote> computePlayedNotes() throws IM3Exception {
        return compoundAtom.computePlayedNotes();
    }

    @Override
	public String toString() {
		return "Beamed group, computed?:" + computedBeam + ", " + super.toString();
	}

    @Override
    public List<AtomPitch> getAtomPitches() {
        return compoundAtom.getAtomPitches();
    }

    @Override
    public List<AtomFigure> getAtomFigures() {
        return compoundAtom.getAtomFigures();
    }

    @Override
    public List<Atom> getAtoms() {
        return compoundAtom.getAtoms();
    }

    public void addSubatom(SingleFigureAtom singleFigureAtom) throws IM3Exception {
        compoundAtom.addSubatom(singleFigureAtom);
        addDuration(singleFigureAtom.getDuration());
    }*/


}
