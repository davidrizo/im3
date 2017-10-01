package es.ua.dlsi.im3.core.score;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;


/**
 * Group of notes, rests, or other beamed groups. The number of beams should be deduced from the duration of the included atoms
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
	public String toString() {
		return "Beamed group, computed?:" + computedBeam + ", " + super.toString();
	}

    public boolean isComputedBeam() {
        return computedBeam;
    }
}
