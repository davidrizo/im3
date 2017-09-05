package es.ua.dlsi.im3.core.score.mensural.meters;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;

public class TimeSignatureMensural extends TimeSignature {

	/**
	 * Or maximarum
	 */
	private Perfection modusMaior;
	/**
	 * Or longarum
	 */
	private Perfection modusMinor;
	private Perfection prolatio;
	private Perfection tempus;
	Fraction maximaDuration;
	Fraction longaDuration;
	Fraction breveDuration;
	Fraction semibreveDuration;

	public TimeSignatureMensural(Perfection tempus, Perfection prolatio) {
		this(null, null, tempus, prolatio);
	}
	
	//TODO Comprobar con Antonio Ezquerro la duración del compás - tests unitarios con obras

	/**
	 * 
	 * @param modusMaior When null it is taken as imperfect
	 * @param modusMinor When null it is taken as imperfect
	 * @param tempus
	 * @param prolatio
	 */
	public TimeSignatureMensural(Perfection modusMaior, Perfection modusMinor, Perfection tempus, Perfection prolatio) {
		super(NotationType.eMensural);
		this.prolatio = prolatio;
		if (prolatio == null || prolatio == Perfection.imperfectum) { // semibreve = 2 minimas
			semibreveDuration = Figures.MINIMA.getDuration().multiplyBy(Fraction.getFraction(2,1));
		} else if (prolatio == Perfection.perfectum) { // semibreve = 3 minimas
			semibreveDuration = Figures.MINIMA.getDuration().multiplyBy(Fraction.getFraction(3,1));
		} else {
			throw new IM3RuntimeException("Invalid prolatio: '" + prolatio + "'");
		}
		
		this.tempus = tempus;
		if (tempus == null || tempus == Perfection.imperfectum) { // breve = 2 semibreves
			breveDuration = semibreveDuration.multiplyBy(Fraction.getFraction(2,1));
		} else if (tempus == Perfection.perfectum) { // breve = 3 semibreves
			breveDuration = semibreveDuration.multiplyBy(Fraction.getFraction(3,1));
		} else {
			throw new IM3RuntimeException("Invalid tempus: '" + tempus + "'");
		}
		
		this.modusMinor = modusMinor;
		if (modusMinor == null || modusMinor == Perfection.imperfectum) { // longa = 2 breves
			longaDuration = breveDuration.multiplyBy(Fraction.getFraction(2,1));
		} else if (modusMinor == Perfection.perfectum) { // longa = 3 breves
			longaDuration = breveDuration.multiplyBy(Fraction.getFraction(3,1));			
		} else {
			throw new IM3RuntimeException("Invalid modusMinor: '" + modusMinor + "'");
		}

		this.modusMaior = modusMaior;
		if (modusMaior == null || modusMaior == Perfection.imperfectum) { // maxima = 2 longas
			maximaDuration = longaDuration.multiplyBy(Fraction.getFraction(2,1));
		} else if (modusMaior == Perfection.perfectum) { // or maximarum, maxima = 3 longas
			maximaDuration = longaDuration.multiplyBy(Fraction.getFraction(3,1));			
		} else {
			throw new IM3RuntimeException("Invalid modusMaior: '" + modusMaior + "'");
		}
	}

	

	public final Perfection getProlatio() {
		return prolatio;
	}

	public final Perfection getTempus() {
		return tempus;
	}

	

	public final Perfection getModusMaior() {
		return modusMaior;
	}



	public final Perfection getModusMinor() {
		return modusMinor;
	}



	public final Fraction getMaximaDuration() {
		return maximaDuration;
	}



	public final Fraction getLongaDuration() {
		return longaDuration;
	}



	public final Fraction getBreveDuration() {
		return breveDuration;
	}



	public final Fraction getSemibreveDuration() {
		return semibreveDuration;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((breveDuration == null) ? 0 : breveDuration.hashCode());
		result = prime * result + ((longaDuration == null) ? 0 : longaDuration.hashCode());
		result = prime * result + ((maximaDuration == null) ? 0 : maximaDuration.hashCode());
		result = prime * result + ((semibreveDuration == null) ? 0 : semibreveDuration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		TimeSignatureMensural other = (TimeSignatureMensural) obj;
		if (breveDuration == null) {
			if (other.breveDuration != null)
				return false;
		} else if (!breveDuration.equals(other.breveDuration))
			return false;
		if (longaDuration == null) {
			if (other.longaDuration != null)
				return false;
		} else if (!longaDuration.equals(other.longaDuration))
			return false;
		if (maximaDuration == null) {
			if (other.maximaDuration != null)
				return false;
		} else if (!maximaDuration.equals(other.maximaDuration))
			return false;
		if (semibreveDuration == null) {
			if (other.semibreveDuration != null)
				return false;
		} else if (!semibreveDuration.equals(other.semibreveDuration))
			return false;
		return true;
	}

	@Override
	public TimeSignatureMensural clone() {
		return new TimeSignatureMensural(modusMaior, modusMinor, tempus, prolatio);
	}

	@Override
	public String toString() {
		return "MensuralMeter [modusMaior=" + modusMaior + ", modusMinor=" + modusMinor + ", prolatio=" + prolatio
				+ ", tempus=" + tempus + ", maximaDuration=" + maximaDuration + ", longaDuration=" + longaDuration
				+ ", breveDuration=" + breveDuration + ", semibreveDuration=" + semibreveDuration + ", time=" + 
				time + "]";
	}

	public Fraction getDuration(Figures figure) {
		Fraction duration;
		switch (figure) {
			case MAXIMA:
				duration = maximaDuration;
				break;
			case LONGA:
				duration = longaDuration;
				break;
			case BREVE:
				duration = breveDuration;
				break;
			case SEMIBREVE:
				duration = semibreveDuration;
				break;
			default:
				duration = figure.getDuration();
				break;
		}
		return duration;
	}

	@Override
	public boolean isCompound() {
		return false;
	}

}
