package es.ua.dlsi.im3.core.score;

import java.util.List;

import es.ua.dlsi.im3.core.IM3RuntimeException;
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

	public SimpleRest(SimpleRest simpleRest) throws IM3Exception {
		this(simpleRest.getAtomFigure().getFigure(), simpleRest.getAtomFigure().getDots(), simpleRest.getDuration());

		if (simpleRest.getAtomFigure().isExplicitMensuralPerfection()) {
			this.getAtomFigure().setExplicitMensuralPerfection(simpleRest.getAtomFigure().getMensuralPerfection());
		} else {
			this.getAtomFigure().setComputedMensuralPerfection(simpleRest.getAtomFigure().getMensuralPerfection(), simpleRest.getAtomFigure().getPerfectionRuleApplied());
		}
	}

	public SimpleRest clone() {
		try {
			return new SimpleRest(this);
		} catch (IM3Exception e) {
			throw new IM3RuntimeException("Cannot clone", e);
		}
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
