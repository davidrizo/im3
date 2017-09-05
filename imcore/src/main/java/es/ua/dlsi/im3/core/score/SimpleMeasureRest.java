package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.math.Fraction;

public class SimpleMeasureRest extends SimpleMultiMeasureRest {
	/**
	 * 
	 * @param measureDuration
	 */
	public SimpleMeasureRest(Figures figure, Fraction measureDuration) {
		super(figure, measureDuration, 1);
	}

	@Override
	public String toString() {
		return super.toString() + ", measure rest";
	}
}
