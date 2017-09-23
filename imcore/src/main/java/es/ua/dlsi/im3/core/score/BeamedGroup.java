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
    /**
     * True if the beam is not explicitly created but computed by our layout algorithms
     */
    boolean computedBeam;

    Figures figure;

    public BeamedGroup(Figures figure, boolean computedBeam) {
        this.computedBeam = computedBeam;
        this.figure = figure;
    }

    @Override
    public void addSubatom(Atom subatom) throws IM3Exception {
        if (!subatom.getDuration().equals(figure.getDuration())) {
            throw new IM3Exception("The duration of the subatom should be " + figure.getDuration() + " and it is " + subatom.getDuration());
        }
        super.addSubatom(subatom);
    }

    @Override
	public String toString() {
		return "Beamed group of " + figure + ", computed?:" + computedBeam + ", " + super.toString();
	}

    public boolean isComputedBeam() {
        return computedBeam;
    }

    public Figures getFigure() {
        return figure;
    }

    public int getNumBeams() {
        return figure.getNumFlags();
    }
}
