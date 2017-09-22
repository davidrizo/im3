package es.ua.dlsi.im3.core.score;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;

public enum Figures {
	MAX_FIGURE (Integer.MAX_VALUE, 1, Integer.MAX_VALUE, NotationType.eModern, false, 0), // used the same way Integer.MAX_VALUE
	QUADRUPLE_WHOLE (16,1,-1, NotationType.eModern, false, 0),
	DOUBLE_WHOLE (8,1,0, NotationType.eModern, false, 0),
	WHOLE (4,1,1, NotationType.eModern, false, 0),
	HALF (2,1,2, NotationType.eModern, true, 0),
	QUARTER (1,1,4,NotationType.eModern, true, 0),
	EIGHTH (1,2,8, NotationType.eModern, true, 1),
	SIXTEENTH (1, 4, 16, NotationType.eModern, true, 2),
	THIRTY_SECOND (1, 8, 32, NotationType.eModern, true, 3),
	SIXTY_FOURTH (1, 16, 64,NotationType.eModern, true, 4),
	HUNDRED_TWENTY_EIGHTH (1, 32, 128, NotationType.eModern, true, 5),
	TWO_HUNDRED_FIFTY_SIX (1, 64, 256, NotationType.eModern, true, 6),
	MAXIMA (16, 1, -2, NotationType.eMensural, false, 0),
	LONGA (8, 1, -1, NotationType.eMensural, false, 0),
	BREVE (4, 1, 0, NotationType.eMensural, false, 0),
	SEMIBREVE (2, 1, 1, NotationType.eMensural, false, 0),
	MINIM(1, 1, 2, NotationType.eMensural, false, 0),
	SEMIMINIM(1, 2, 4, NotationType.eMensural, false, 0),
	FUSA (1, 4, 8, NotationType.eMensural, false, 1),
	SEMIFUSA (1, 8, 16, NotationType.eMensural, false, 2),
	NO_DURATION (0,1,0, NotationType.eModern, false, 0); // TODO: 22/9/17 Que tenga plica o no depende de la tipografía?
	
	final Fraction duration;
	/**
	 * Classical interpretation (the one used in denominators of meters)
	 */
    final int meterUnit;
    final NotationType notationType;

    final boolean usesStem;
    final int numFlags;
	
	Figures(int quarters, int quarterSubdivisions, int meterUnit, NotationType notationType, boolean usesStem, int flags) {
		duration = Fraction.getFraction(quarters, quarterSubdivisions);
		this.meterUnit = meterUnit;
		this.notationType = notationType;
		this.usesStem = usesStem;
		this.numFlags = flags;
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

	public boolean usesFlag() {
		return numFlags > 0;

	}
	public boolean usesStem() {
		return usesStem;
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

    public int getNumFlags() {
        return numFlags;
    }
}
