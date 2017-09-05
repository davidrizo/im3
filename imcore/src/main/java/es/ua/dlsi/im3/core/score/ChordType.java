package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 *TODO Deberiamos poner la composicion de cada acorde aqui (con Intervals)
 * Kind from MusicXML
@author drizo
@date 07/06/2011
 **/
public enum ChordType {
	//MAJOR ("4,3"),
	MAJOR("M", new Degree[]{Degree.I, Degree.III, Degree.V}, new Intervals[]{Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.MAJOR),
	
	//MINOR ("3,4"),
	MINOR("m", new Degree[]{Degree.I, Degree.III, Degree.V},new Intervals[]{Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MAJOR_ASC}, HarmonyKind.MINOR),
	
	//AUGMENTED ("4,4"),
	AUGMENTED("a", new Degree[]{Degree.I, Degree.III, Degree.V},new Intervals[]{Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MAJOR_ASC}, HarmonyKind.AUGMENTED),
	
	//DIMINISHED ("3,3"),
	DIMINISHED("d", new Degree[]{Degree.I, Degree.III, Degree.V},new Intervals[]{Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.DIMINISHED),
	
	//MAJ7MIN ("4,3,3"),
	MAJ7MIN("M7m", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.DOMINANT),
	
	//AUG7MAJ("4,4,3"), // Placido: fifth_augmented_and_seven_major 
	AUG7MAJ("a7M", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.AUGMENTED_SEVENTH), 
	
	//DIM7MIN ("3,3,4"),
	DIM7MIN("d7m", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MAJOR_ASC}, HarmonyKind.HALF_DIMINISHED),
	
	//DIM7DIM ("3,3,3"),
	DIM7DIM("d7d", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.DIMINISHED_SEVENTH),
	
	//MAJ7MAJ ("4,3,4"),
	MAJ7MAJ("M7M", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MAJOR_ASC}, HarmonyKind.MAJOR_SEVENTH),
	
	//MIN7MIN ("3,4,3");
	MIN7MIN("m7m", new Degree[]{Degree.I, Degree.III, Degree.V, Degree.VII},new Intervals[]{Intervals.THIRD_MINOR_ASC, Intervals.THIRD_MAJOR_ASC, Intervals.THIRD_MINOR_ASC}, HarmonyKind.MINOR_SEVENTH);

	//(maj (4 3)) (min (3 4)) (aug (4 4)) (dim (3 3)) (maj7min (4 3 3)) (aug7maj (4 4 3)) (dim7min (3 3 4)) (dim7dim 3 3 3) (maj7maj 4 3 4) (min7min 3 4 3))
	
	//TODO VER LO DE BAJO CON PLACIDO
	/*,
	MINOR,
	DIMINISHED,
	MAJOR_MINOR,
	AUGMENTED_SEVENTH,
	DIMINISHED_SEVENTH,

	
	DOMINANT_11TH,
	DOMINANT_13TH,
	DOMINANT_NINTH,
	DOMINANT,
	MAJOR_NINTH,
	MAJOR_SEVENTH,
	MAJOR_SIXTH,
	MINOR_11TH,
	MINOR_NINTH,
	MINOR_SEVENTH,
	MINOR_SIXTH,
	SUSPENDED_FOURTH,
	SUSPENDED_SECOND;*/
	
	//private int [] semitonesSequence;
	/**
	 * Valid layouts of intervals
	 */
	Intervals [] intervals;
	Degree [] degrees;
	private final HarmonyKind equivalentHarmonyKind;
	private final String abbr;

	ChordType(String abbr, Degree[] degrees, Intervals [] itvs, HarmonyKind equivalentHK) {
		this.intervals = itvs;
		this.equivalentHarmonyKind = equivalentHK;
		this.abbr = abbr;
		this.degrees = degrees;
	}
	/**
	 * @return the equivalentHarmonyKind
	 */
	public final HarmonyKind getEquivalentHarmonyKind() {
		return equivalentHarmonyKind;
	}
	
	public Degree[] getDegrees() {
		return degrees;
	}

	
	//intervals = new Intervals[1];
		//intervals[0] = i1;

	/*ChordType(Intervals i1, Intervals i2) {
		intervals = new Intervals[2];
		intervals[0] = i1;
		intervals[1] = i2;
	}

	ChordType(Intervals i1, Intervals i2, Intervals i3) {
		intervals = new Intervals[3];
		intervals[0] = i1;
		intervals[1] = i2;
		intervals[2] = i3;
	}*/
	
	
	/*ChordType(String semitonesSeq) {
		String [] s = semitonesSeq.split(",");
		this.semitonesSequence = new int[s.length];
		for (int i=0; i<s.length; i++) {
			this.semitonesSequence[i] = new Integer(s[i]).intValue();
		}
		
	}

	public final int[] getSemitonesSequence() {
		return semitonesSequence;		
	}
	*/
	/*public Interval[] getIntervalSequence() {
		Interval [] result = new Interval[intervals.length];
		for (int i = 0; i < intervals.length; i++) {
			result[i] = intervals[i].createInterval();
		}
		return result;
	}*/
	
	public Interval[] getIntervals() {
		Interval [] result = new Interval[intervals.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = intervals[i].createInterval();
		}
		return result;
	}
	

	/**
	 * Triad = 3
	 * @return
	 */
	public double size() {
		return this.intervals.length+1;
	}
	
	public static ChordType harmonyKind2ChordType(HarmonyKind h) throws IM3Exception {
		for (ChordType ct : ChordType.values()) {
			if (ct.getEquivalentHarmonyKind().equals(h)) {
				return ct;
			}
		}
		throw new IM3Exception("Cannot find the chord type for the harmony kind " + h.toString());
	}

    public String getAbbr() {
	return abbr;
    }
    
	
	
}
