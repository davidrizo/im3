package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.math.Fraction;

public class SimpleMultiMeasureRest extends SimpleRest {
	/**
	 * Number of measures
	 */
	int numMeasures;
	public SimpleMultiMeasureRest(Time measureDuration, int numMeasures) {
		this(Figures.WHOLE, measureDuration, numMeasures);
	}
	/**
	 *
	 * @param measureDuration
	 */
	public SimpleMultiMeasureRest(Figures figure, Time measureDuration, int numMeasures) {
		super(figure, 0, measureDuration.multiplyBy(Fraction.getFraction(numMeasures, 1)));
		this.numMeasures = numMeasures;
	}

	@Override
	public String toString() {
		return super.toString() + " " + numMeasures + " measures rest";
	}

	public void setFigure(Figures figure) {
		this.atomFigure.setFigure(figure);
	}

	public int getNumMeasures() {
		return numMeasures;
	}
}
