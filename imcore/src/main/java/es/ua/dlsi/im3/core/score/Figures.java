package es.ua.dlsi.im3.core.score;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;

public enum Figures {	
	MAX_FIGURE (Integer.MAX_VALUE, 1, Integer.MAX_VALUE, NotationType.eModern), // used the same way Integer.MAX_VALUE
	QUADRUPLE_WHOLE (16,1,-1, NotationType.eModern),
	DOUBLE_WHOLE (8,1,0, NotationType.eModern),
	WHOLE (4,1,1, NotationType.eModern),
	HALF (2,1,2, NotationType.eModern),
	QUARTER (1,1,4,NotationType.eModern),
	EIGHTH (1,2,8, NotationType.eModern),
	SIXTEENTH (1, 4, 16, NotationType.eModern),
	THIRTY_SECOND (1, 8, 32, NotationType.eModern),
	SIXTY_FOURTH (1, 16, 64,NotationType.eModern),
	HUNDRED_TWENTY_EIGHTH (1, 32, 128, NotationType.eModern),
	TWO_HUNDRED_FIFTY_SIX (1, 64, 256, NotationType.eModern),
	MAXIMA (16, 1, -2, NotationType.eMensural),
	LONGA (8, 1, -1, NotationType.eMensural),
	BREVE (4, 1, 0, NotationType.eMensural),
	SEMIBREVE (2, 1, 1, NotationType.eMensural),
	MINIM(1, 1, 2, NotationType.eMensural),
	SEMIMINIM(1, 2, 4, NotationType.eMensural),
	FUSA (1, 4, 8, NotationType.eMensural),
	SEMIFUSA (1, 8, 16, NotationType.eMensural),
	NO_DURATION (0,1,0, NotationType.eModern);
	
	Fraction duration;
	/**
	 * Classical interpretation (the one used in denominators of meters)
	 */
	int meterUnit;
	NotationType notationType;
	
	Figures(int quarters, int quarterSubdivisions, int meterUnit, NotationType notationType) {
		duration = Fraction.getFraction(quarters, quarterSubdivisions);
		this.meterUnit = meterUnit;
		this.notationType = notationType;
	}

	public Fraction getDuration() {
		return duration;
	}

	public int getMeterUnit() {
		return meterUnit;
	}

	public final NotationType getNotationType() {
		return notationType;
	}

	/**
	 * Compute the duration of the figure using dots
	 * @param dots
	 * @return
	 */
	public Fraction getDurationWithDots(int dots) {
		Fraction sumDurations = duration;
		Fraction lastDur = sumDurations;
		
		for (int i=0; i<dots; i++) {
			lastDur = lastDur.multiplyBy(Fraction.ONE_HALF);
			sumDurations = sumDurations.add(lastDur);
		}
		
		return sumDurations;		
	}

	public static Figures findDuration(Fraction duration, NotationType notationType) throws IM3Exception {
		for (Figures fig: Figures.values()) {
			if (fig.notationType == notationType && fig.duration.equals(duration)) {
				return fig;
			}
		}
		throw new IM3Exception("Cannot find a figure with duration " + duration + " and notation type " + notationType);
	}

	public static Figures findMeterUnit(int meterUnit, NotationType notationType) throws IM3Exception {
		for (Figures fig: Figures.values()) {
			if (fig.notationType == notationType && meterUnit == fig.meterUnit) {
				return fig;
			}
		}
		throw new IM3Exception("Cannot find a figure with meter unit " + meterUnit + " and notation type " + notationType);
	}

}
