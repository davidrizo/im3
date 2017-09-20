package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @author  drizo
 * @date  28/06/2011
 */
public class ScientificPitch implements Comparable<ScientificPitch>, Cloneable {
    /**
     * Middle C
     */
	public static final ScientificPitch C4 = new ScientificPitch(PitchClasses.C.getPitchClass(), 4);
    
	/**
	 * Pitch class
	 */
	PitchClass pitchClass;
	/**
	 * Octave name
	 */
	int octave;

	/**
	 * Computed
	 */
	int base40;

	private int base40Chroma;
		
	public ScientificPitch(PitchClass pc, int oct) {
		this.pitchClass = pc;
		this.octave = oct;
		computeBase40();
	}

	public ScientificPitch(PitchClasses pc, int oct) {
		this.pitchClass = pc.getPitchClass();
		this.octave = oct;
		computeBase40();
	}
	
	public ScientificPitch(DiatonicPitch noteName, Accidentals accidental, Integer octave) {
		this(new PitchClass(noteName, accidental), octave);
	}

	/**
	 * @return the pitchClass
	 */
	public final PitchClass getPitchClass() {
		return pitchClass;
	}

	/**
	 * @param pitchClass the pitchClass to set
	 */
	public final void setPitchClass(PitchClass pitchClass) {
		this.pitchClass = pitchClass;
		computeBase40();
	}

	/**
	 * @return the octave
	 */
	public final int getOctave() {
		return octave;
	}

	/**
	 * @param octave the octave to set
	 */
	public final void setOctave(int octave) {
		this.octave = octave;
		computeBase40();
	}
	public int computeMidiPitch()  {
		int result = pitchClass.getSemitonesFromC() + (octave+1)*12;
		if (result < 0 ||result > 127) {
			throw new RuntimeException("Invalid MIDI pitch: " + result + " for scientific pitch " + this.toString());
		}
		return result;		
	}

	@Override
	public int compareTo(ScientificPitch other) {
		int res = computeMidiPitch() - other.computeMidiPitch();
		if (res == 0) {
			res = this.octave - other.octave;
			if (res == 0) {
				res = this.pitchClass.compareTo(other.pitchClass);				
			}
		}
		return res;
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return pitchClass.toString()  + octave;
	}


	/** (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + octave;
		result = prime * result
				+ ((pitchClass == null) ? 0 : pitchClass.hashCode());
		return result;
	}


	/** (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScientificPitch other = (ScientificPitch) obj;
		if (octave != other.octave)
			return false;
		if (pitchClass == null) {
			if (other.pitchClass != null)
				return false;
		} else if (!pitchClass.equals(other.pitchClass))
			return false;
		return true;
	}

	public MotionDirection computeDirection(ScientificPitch previous) {
		if (previous == null) {
			return MotionDirection.UNDEFINED; // TODO ¿No deberia ser NONE?
		} else {
			int comp = previous.compareTo(this);
			if (comp == 0) {
				return MotionDirection.UNDEFINED;
			} else if (comp < 0) {
				return MotionDirection.ASCENDING;
			} else {
				return MotionDirection.DESCENDING;
			}
		} 
	}
	
	@Override
	public ScientificPitch clone() {
	    return new ScientificPitch(pitchClass, octave);
	}

	public ScientificPitch transpose(Interval interval) throws IM3Exception {
	    return interval.computeScientificPitchFrom(this);
	}
	
	public int getBase40() {
	    return base40;
	}
	
	public int getBase40Chroma() {
		return base40Chroma;
	}

	private void computeBase40() {
            if (pitchClass != null) {
                base40Chroma = pitchClass.getBase40Chroma();
                base40 = octave * 40 + base40Chroma;
            }
	}
}