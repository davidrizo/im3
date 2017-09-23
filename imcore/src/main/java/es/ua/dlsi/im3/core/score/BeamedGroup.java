package es.ua.dlsi.im3.core.score;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;


/**
 * Group of notes, rests, or other beamed groups
 * @author drizo
 */
public class BeamedGroup extends CompoundAtom {
    private final Time eachElementDuration;
    /**
     * True if the beam is not explicitly created but computed by our layout algorithms
     */
    boolean computedBeam;

    Figures eachElementFigure;

    final private NotationType notationType;

    public BeamedGroup(Time eachElementDuration, NotationType notationType, boolean computedBeam) throws IM3Exception {
        this.computedBeam = computedBeam;
        this.notationType = notationType;
        this.eachElementDuration = eachElementDuration;
        eachElementFigure = Figures.findDuration(eachElementDuration, notationType);
    }

    @Override
    public void addSubatom(Atom subatom) throws IM3Exception {
        if (!subatom.getDuration().equals(eachElementDuration)) {
            throw new IM3Exception("The duration of the subatom should be " + eachElementDuration + " and it is " + subatom.getDuration());
        }
        super.addSubatom(subatom);
    }

    @Override
	public String toString() {
		return "Beamed group of " + eachElementDuration + ", computed?:" + computedBeam + ", " + super.toString();
	}

    public boolean isComputedBeam() {
        return computedBeam;
    }

    public int getNumBeams() {
        return eachElementFigure.getNumFlags();
    }
}
