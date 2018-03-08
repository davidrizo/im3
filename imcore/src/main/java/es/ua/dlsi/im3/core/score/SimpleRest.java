package es.ua.dlsi.im3.core.score;

import java.util.List;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * This class should be used just as a preset. The main class to be used is Atom
 * @author drizo
 *
 */
public class SimpleRest extends SingleFigureAtom {
 	
	public SimpleRest(Figures figure, int dots) {
		super(figure, dots);
	}

	/**
	 * Package visibility, used by tuplets and mensural
	 * @param figure
	 * @param dots
	 */
	SimpleRest(Figures figure, int dots, Time alteredDuration) {
		super(figure, dots, alteredDuration);
	}

	@Override
	public List<PlayedScoreNote> computePlayedNotes() {
		return null;
	}

	@Override
	public List<AtomPitch> getAtomPitches() {
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", rest";
	}

}
