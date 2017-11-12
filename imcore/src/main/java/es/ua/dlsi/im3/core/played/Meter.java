package es.ua.dlsi.im3.core.played;

import es.ua.dlsi.im3.core.IM3Exception;

public class Meter implements IPlayedEvent {
	int numerator;
	int denominator;
	long time;

	public Meter(int num, int den) {
		this.numerator = num;
		this.denominator = den;
	}

	public final int getNumerator() {
		return numerator;
	}

	public final int getDenominator() {
		return denominator;
	}

	public final void setTime(long time) {
		this.time = time;
	}

	@Override
	public long getTime() {
		return time;
	}

	/**
	 * It parses a string in the form NN/DD and creates a meter
	 *
	 * @param meterString
	 * @return
	 * @throws IM3Exception
	 */
	public static Meter parseTimeSignature(String meterString) throws IM3Exception {
		String[] tokens = meterString.split("/");
		if (tokens.length != 2) {
			throw new IM3Exception("Invalid meter string: " + meterString + ", must have the form NN/DD");
		}
		try {
			int num = Integer.parseInt(tokens[0]);
			int den = Integer.parseInt(tokens[1]);
			Meter meter = new Meter(num, den);
			return meter;
		} catch (NumberFormatException e) {
			if (tokens.length != 2) {
				throw new IM3Exception("Invalid meter string: " + meterString + ", must have the form NN/DD");
			}
		}
		return null;
	}

	public int getMeasureDurationAsTicks(int resolution) {
		if(this.isCompound()) {
			// the resolution is always set (at least from Finale 2008), in terms of quarter, 
			// not in terms of beat
			//float longBeat = resolution / (getDenominator() / 4); // the
			float longBeat = resolution*4 / (getDenominator()); // the
																								
			return (int) (getNumerator() * longBeat);
		} else {
			float beatRelation = (float) getDenominator() / 4;
			float longBeat = (float) resolution / beatRelation;
			return (int) (getNumerator() * longBeat);
		}
	}
	
	public boolean isCompound() { 
		return (getDenominator() == 2 && getNumerator() == 6) || (getDenominator() > 2) && (getNumerator() % 3 == 0)
				&& (getDenominator() % 2 == 0) && (getNumerator() / 3 > 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + denominator;
		result = prime * result + numerator;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meter other = (Meter) obj;
		if (denominator != other.denominator)
			return false;
        return numerator == other.numerator;
    }

	@Override
	public String toString() {
		return "Meter [time=" + time + ", numerator=" + numerator + ", denominator=" + denominator + "]";
	}

	
}
