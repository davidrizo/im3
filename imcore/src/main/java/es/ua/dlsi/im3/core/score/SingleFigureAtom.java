package es.ua.dlsi.im3.core.score;

import java.util.Arrays;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.math.Fraction;

public abstract class SingleFigureAtom extends Atom {
	AtomFigure atomFigure;
	
	public SingleFigureAtom(Figures figure, int dots) {
		atomFigure = new AtomFigure(this, figure, dots);
		addDuration(atomFigure.getDuration());
	}
	
	/**
	 * Package visibility, used by tuplets and mensural
	 * @param figure
	 * @param dots
	 */
	SingleFigureAtom(Figures figure, int dots, Fraction alteredDuration) {
		atomFigure = new AtomFigure(this, figure, dots, alteredDuration);
		addDuration(atomFigure.getDuration());
	}
	
	public void setRelativeToAtomOnset(Fraction currentRelativeOnset) {
		atomFigure.setRelativeOnset(currentRelativeOnset);
	}

	/**
	 * Note this is the represented figure, its duration may be different for tuplet elements or multiple and measure rests
	 * @return
	 */
	public final AtomFigure getAtomFigure() {
		return atomFigure;
	}
	
	@Override
	public List<AtomFigure> getAtomFigures() {
		return Arrays.asList(atomFigure);
	}
	@Override
	public List<Atom> getAtoms() {
		return Arrays.asList(this);
	}

	public void setRelativeOnset(Fraction relativeOnset) {
		this.atomFigure.setRelativeOnset(relativeOnset);
		
	}

	@Override
	public String toString() {
		return super.toString() + ", " + atomFigure;
	}

	/**
	 * Use with care, usually this method should be used just by importers in cases such as tuplets.
	 * It can be used only when the current figure is Figure.NO_DURATION
	 * @param figure
	 */
	public void setFigure(Figures figure) {
		this.atomFigure.setFigure(figure);
		addDuration(atomFigure.getDuration());
    }

    @Override
    public void setDuration(Fraction duration) {
        super.setDuration(duration);
        this.atomFigure.setDuration(new Time(duration));
    }
}
