package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;

public abstract class TimeSignatureMensural extends SignTimeSignature {

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
	Time maximaDuration;
    Time longaDuration;
    Time breveDuration;
    Time semibreveDuration;

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
        this.tempus = tempus;
        this.modusMinor = modusMinor;
        this.modusMaior = modusMaior;

        initDurations();
    }

    private void initDurations() {
        semibreveDuration = Figures.MINIM.getDuration().multiplyBy(Fraction.getFraction(
                prolatio == null? 2: prolatio.getDivisions(),
                1)
        );

        breveDuration = semibreveDuration.multiplyBy(Fraction.getFraction(
                tempus == null? 2: tempus.getDivisions(),
                1)
        );


        longaDuration = breveDuration.multiplyBy(Fraction.getFraction(
                modusMinor == null? 2: modusMinor.getDivisions(),
                1)
        );

        maximaDuration = longaDuration.multiplyBy(Fraction.getFraction(
                modusMaior == null? 2: modusMaior.getDivisions(),
                1)
        );
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



	public final Time getMaximaDuration() {
		return maximaDuration;
	}



	public final Time getLongaDuration() {
		return longaDuration;
	}



	public final Time getBreveDuration() {
		return breveDuration;
	}



	public final Time getSemibreveDuration() {
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
	public String toString() {
		return "MensuralMeter [modusMaior=" + modusMaior + ", modusMinor=" + modusMinor + ", prolatio=" + prolatio
				+ ", tempus=" + tempus + ", maximaDuration=" + maximaDuration + ", longaDuration=" + longaDuration
				+ ", breveDuration=" + breveDuration + ", semibreveDuration=" + semibreveDuration + ", time=" + 
				time + "]";
	}

	public Time getDuration(Figures figure) {
        Time duration;
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

    public void setModusMaior(Perfection modusMaior) {
	    this.modusMaior = modusMaior;
	    initDurations();
    }

    public void setModusMinor(Perfection modusMinor) {
	    this.modusMinor = modusMinor;
	    initDurations();
    }
}
