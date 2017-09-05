package es.ua.dlsi.im3.core.score.meters;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimeSignatureWithDuration;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.TimeSignature;

/**
 * //TODO Add C and C/ TODO Hacer jerarquía de objetos, compuesto, simple,
 * etc... - orientación a objetos - compases de amalgama
 *
 * Modern (vs mensural) meter
 * @author drizo
 * @date 03/06/2011
 *
 */
public class FractionalTimeSignature extends TimeSignature implements Comparable<FractionalTimeSignature>, ITimeSignatureWithDuration {
	private static final String SLASH = "/";

	protected Fraction fraction;
	/**
	 * Cached
	 */
	private Time duration;

	/**
	 * The lesser the tempo, the most stable or strong //TODO Generalizarlo -
	 * podemos usar los �rboles m�tricos
	 */
	// protected int [] rhythmicStability;
	/**
	 * @param numerator
	 * @param denominator
	 */
	public FractionalTimeSignature(int numerator, int denominator) {
		super(NotationType.eModern);
		fraction = Fraction.getFraction(numerator, denominator);				
		duration = new Time(Fraction.getFraction(numerator, 1).multiplyBy(Fraction.getFraction(4, denominator)));
	}

	/**
	 * Compute the figureAndDots in ticks of a measure
	 *
	 * @return
	 */
	/*FRACCIONES public long getMeasureDurationAsTicks() {
		if (this.isCompound()) {
			float longBeat = (float) ScoreSong.DEFAULT_RESOLUTION / (getDenominator() / 4); // the
																								// resolution
																								// is
																								// always
																								// set
																								// (at
																								// least
																								// from
																								// Finale
																								// 2008)
																								// in
																								// terms
																								// of
																								// quarter,
																								// not
																								// in
																								// terms
																								// of
																								// beat
			return (int) (getNumerator() * longBeat);
		} else {
			float beatRelation = (float) getDenominator() / 4; // the resolution
																// is always set
																// (at least
																// from Finale
																// 2008) in
																// terms of
																// quarter, not
																// in terms of
																// beat
			float longBeat = (float) ScoreSong.DEFAULT_RESOLUTION / beatRelation;
			return (int) (getNumerator() * longBeat);
		}
	}*/

	/**
	 * getMeasureDuration / DEFAULT_DESOLUTION E.g. a 4/4 will return 4
	 * 
	 * @return
	 */
	/*FRACCIONES public double getMeasureDurationAsRatio() {
		return (double) getMeasureDurationAsTicks() / (double) ScoreSong.DEFAULT_RESOLUTION;
	}*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return "[getNumerator()=" + getNumerator() + ", getDenominator()=" + getDenominator() + "]";
	}

	/**
	 * @return the getNumerator()
	 */
	public final int getNumerator() {
		return fraction.getNumerator();
	}

	/**
	 * @return the getDenominator()
	 */
	public final int getDenominator() {
		return fraction.getDenominator();
	}
	// TODO Javi Ver esto para compases compuestos
	// TODO Test unitario


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getDenominator();
		result = prime * result + getNumerator();
		return result;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().isAssignableFrom(obj.getClass())) { // Important to
															// allow inheritance
			return false;
		}
		FractionalTimeSignature other = (FractionalTimeSignature) obj;
		if (getDenominator() != other.getDenominator()) {
			return false;
		}
		return getNumerator() == other.getNumerator();
	}

	/**
	 * It parses a string in the form NN/DD and creates a meter
	 *
	 * @param meterString
	 * @return
	 * @throws IM3Exception
	 */
	public static FractionalTimeSignature parseTimeSignature(String meterString) throws IM3Exception {
		String[] tokens = meterString.split(SLASH);
		if (tokens.length != 2) {
			throw new IM3Exception("Invalid meter string: " + meterString + ", must have the form NN/DD");
		}
		try {
			int num = Integer.parseInt(tokens[0]);
			int den = Integer.parseInt(tokens[1]);
			FractionalTimeSignature meter = new FractionalTimeSignature(num, den);
			return meter;
		} catch (NumberFormatException e) {
			if (tokens.length != 2) {
				throw new IM3Exception("Invalid meter string: " + meterString + ", must have the form NN/DD");
			}
		}
		return null;
	}

	/**
	 * Returns true if the meter is compound (not simple)
	 *
	 * @return
	 */
	public boolean isCompound() { // TODO Javi Tests unitarios
		return (getDenominator() == 2 && getNumerator() == 6) || (getDenominator() > 2) && (getNumerator() % 3 == 0)
				&& (getDenominator() % 2 == 0) && (getNumerator() / 3 > 1);
	}

	@Override
	public FractionalTimeSignature clone() {
		return new FractionalTimeSignature(this.getNumerator(), this.getDenominator());
	}

	@Override
	public Time getMeasureDuration() {
		return duration;
	}

	public int compareTo(FractionalTimeSignature other) {
		return getNumerator() * getDenominator() - other.getNumerator() * other.getDenominator();
	}	
}
