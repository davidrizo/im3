package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.math.Fraction;

public class SimpleMeasureRest extends SimpleRest {
	/**
	 * 
	 * @param measureDuration
	 */
	public SimpleMeasureRest(Figures figure, Time measureDuration) {
        super(figure, 0, measureDuration);
	}

	@Override
	public String toString() {
		return super.toString() + ", measure rest";
	}

}
