package es.ua.dlsi.im3.core.score;

/**
@author drizo
@date 15/06/2011
 **/
public enum PitchClasses {
	C_DFLAT(NoteNames.C, Accidentals.DOUBLE_FLAT,0),
	C_FLAT(NoteNames.C, Accidentals.FLAT,1),
	C(NoteNames.C, Accidentals.NATURAL,2),
	C_SHARP(NoteNames.C, Accidentals.SHARP,3),
	C_DSHARP(NoteNames.C, Accidentals.DOUBLE_SHARP,4),

	D_TFLAT(NoteNames.D, Accidentals.TRIPLE_FLAT,5),
	D_DFLAT(NoteNames.D, Accidentals.DOUBLE_FLAT,6),
	D_FLAT(NoteNames.D, Accidentals.FLAT,7),
	D(NoteNames.D, Accidentals.NATURAL,8),
	D_SHARP(NoteNames.D, Accidentals.SHARP,9),
	D_DSHARP(NoteNames.D, Accidentals.DOUBLE_SHARP,10),

	E_TFLAT(NoteNames.E, Accidentals.TRIPLE_FLAT,11),
	E_DFLAT(NoteNames.E, Accidentals.DOUBLE_FLAT,12),
	E_FLAT(NoteNames.E, Accidentals.FLAT,13),
	E(NoteNames.E, Accidentals.NATURAL,14),
	E_SHARP(NoteNames.E, Accidentals.SHARP,15),
	E_DSHARP(NoteNames.E, Accidentals.DOUBLE_SHARP,16),

	F_DFLAT(NoteNames.F, Accidentals.DOUBLE_FLAT,17),
	F_FLAT(NoteNames.F, Accidentals.FLAT,18),
	F(NoteNames.F, Accidentals.NATURAL,19),
	F_SHARP(NoteNames.F, Accidentals.SHARP,20),
	F_DSHARP(NoteNames.F, Accidentals.DOUBLE_SHARP,21),
	
	G_DFLAT(NoteNames.G, Accidentals.DOUBLE_FLAT,23),
	G_FLAT(NoteNames.G, Accidentals.FLAT,24),
	G(NoteNames.G, Accidentals.NATURAL,25),
	G_SHARP(NoteNames.G, Accidentals.SHARP,26),
	G_DSHARP(NoteNames.G, Accidentals.DOUBLE_SHARP,27),

	A_TFLAT(NoteNames.A, Accidentals.TRIPLE_FLAT,28),
	A_DFLAT(NoteNames.A, Accidentals.DOUBLE_FLAT,29),
	A_FLAT(NoteNames.A, Accidentals.FLAT,30),
	A(NoteNames.A, Accidentals.NATURAL,31),
	A_SHARP(NoteNames.A, Accidentals.SHARP,32),
	A_DSHARP(NoteNames.A, Accidentals.DOUBLE_SHARP,33),

	B_TFLAT(NoteNames.B, Accidentals.TRIPLE_FLAT,34),
	B_DFLAT(NoteNames.B, Accidentals.DOUBLE_FLAT,35),
	B_FLAT(NoteNames.B, Accidentals.FLAT,36),
	B(NoteNames.B, Accidentals.NATURAL,37),
	B_SHARP(NoteNames.B, Accidentals.SHARP,38),
	B_DSHARP(NoteNames.B, Accidentals.DOUBLE_SHARP,39);
	
	
	//PitchClass pc; // cannot initialize
	NoteNames notename;
	Accidentals accidental;
	/**
	 * See http://wiki.ccarh.org/wiki/Base_40
	 */
	int base40ChromaValue;
	
	/**
	 * @param notename
	 * @param accidental
	 */
	PitchClasses(NoteNames notename, Accidentals accidental, int base40ChromaValue) {
		this.notename = notename;
		this.accidental = accidental;
		this.base40ChromaValue = base40ChromaValue;
	}
	/**
	 * @return the notename
	 */
	public final NoteNames getNotename() {
		return this.notename;
	}
	/**
	 * @return the accidental
	 */
	public final Accidentals getAccidental() {
		return this.accidental;
	}
	/**
	 * @return the pc
	 */
	public final PitchClass getPitchClass() {
		return new PitchClass(notename, accidental);
	}

	public int getBase40ChromaValue() {
	    return base40ChromaValue;
	}
	
	
	
}
