package es.ua.dlsi.im3.core.score;


import es.ua.dlsi.im3.core.IM3Exception;

public class RomanAnalysisValue {
	Degree degree;
	IntervalMode mode;
	boolean seventh;
	RomanAnalysisValue inversion; // vii07/VI TODO Ver si es inversion u otra cosa
	public RomanAnalysisValue() {		
	}
	
		
	public RomanAnalysisValue(String degree, IntervalMode mode,
			boolean seventh, RomanAnalysisValue inversion) {
		super();
		if (degree != null) {
			this.degree = degreeToEnum(degree);
		}
		this.mode = mode;
		this.seventh = seventh;
		this.inversion = inversion;
	}


	private Degree degreeToEnum(String str) {
		return Degree.valueOf(str.toUpperCase());
	}


	public Degree getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degreeToEnum(degree);
	}
	public IntervalMode getMode() {
		return mode;
	}
	public void setMode(IntervalMode mode) {
		this.mode = mode;
	}
	public boolean isSeventh() {
		return seventh;
	}
	public void setSeventh(boolean seventh) {
		this.seventh = seventh;
	}
	public RomanAnalysisValue getInversion() {
		return inversion;
	}
	public void setInversion(RomanAnalysisValue inversion) {
		this.inversion = inversion;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((degree == null) ? 0 : degree.hashCode());
		result = prime * result
				+ ((inversion == null) ? 0 : inversion.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + (seventh ? 1231 : 1237);
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
		RomanAnalysisValue other = (RomanAnalysisValue) obj;
		if (degree == null) {
			if (other.degree != null)
				return false;
		} else if (!degree.equals(other.degree))
			return false;
		if (inversion == null) {
			if (other.inversion != null)
				return false;
		} else if (!inversion.equals(other.inversion))
			return false;
		if (mode != other.mode)
			return false;
        return seventh == other.seventh;
    }


	@Override
	public String toString() {
		return degree + mode.name() + (seventh?"7":"") + (inversion!=null?inversion.toString():"");
	}

	//TODO Completar y test
	public HarmonyKind computeHarmonyKind() throws IM3Exception {
		if (this.mode.equals(IntervalMode.MAJOR)) {
			if (seventh) {
				return HarmonyKind.MAJOR_SEVENTH;
			} else {
				return HarmonyKind.MAJOR;
			}
		} else if (this.mode.equals(IntervalMode.MINOR)) {
			if (seventh) {
				return HarmonyKind.MINOR_SEVENTH;
			} else {
				return HarmonyKind.MINOR;
			}
		} else {
			throw new IM3Exception("Cannot find a HarmonyKind for " + this.toString());
		}
	}

	// TODO Completar y test
	public ChordType computeChordType() throws IM3Exception {
		if (this.mode.equals(IntervalMode.MAJOR)) {
			if (seventh) {
				return ChordType.MAJ7MAJ;
			} else {
				return ChordType.MAJOR;
			}
		} else if (this.mode.equals(IntervalMode.MINOR)) {
			if (seventh) {
				return ChordType.MIN7MIN;
			} else {
				return ChordType.MINOR;
			}
		} else {
			throw new IM3Exception("Cannot find a ChordType for " + this.toString());
		}
	}
}
