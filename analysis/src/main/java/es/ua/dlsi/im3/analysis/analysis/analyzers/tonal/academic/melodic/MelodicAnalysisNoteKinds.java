package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;


import es.ua.dlsi.im3.core.score.MelodicFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
@author drizo
@date 11/06/2011
 **/
public enum MelodicAnalysisNoteKinds {
	ANTICIPATION ("an", true, "a", "Anticipation"),
	APPOGIATURA ("ap", true, "g", "Appogiatura"),
	ECHAPEE ("es", true, "e", "Échappée"),
	HARMONIC ("H", true, "h", "Harmonic Tone"),
	NEIGHBOUR_TONE ("n", true, "n", "Neighbour tone"),
	PASSING_TONE ("p", true, "p", "Passing tone"),
	PEDAL ("pd", true, "d", "Intermediate Pedal (not first or last that are taken as harmonic)"),
	SUSPENSION ("s", true, "s", "Suspension"),
	X ("x", true, "x", "X"), //URGENT ? qué es
	NHT ("nht", false, "o", "Other non harmonic tone"), // any other NHT
	NONE ("?", false, "?", "None"),
	TOBECHANGED ("PLACIDO", false, "P", "To be changed");

	String longName;

	String abbreviation;

	String shortcut;

	/**
	 * If false, it is used just to be able to set a different type for muret purposes
	 */
	boolean classifiableKind;

	MelodicAnalysisNoteKinds(String abbr, boolean classifiable, String shortcut, String longName) {
		this.abbreviation = abbr;
		this.classifiableKind = classifiable;
		this.shortcut = shortcut;
		this.longName = longName;
	}

	public String getLongName() {
	    return longName;
	}



	/**
	 * @return the abbreviation
	 */
	public final String getAbbreviation() {
		return abbreviation;
	}

	public String getShortcut() {
	    return shortcut;
	}

	/**
	 * For searching (possibly not found) abbr use: findAbbrForMelodicAnalysisStr
	 * @param abbr
	 * @return
	 * @throws MelodicAnalysisException
	 */
	public static MelodicAnalysisNoteKinds abbrToMelodicAnalysis(String abbr) throws MelodicAnalysisException {
		MelodicAnalysisNoteKinds result = findAbbrForMelodicAnalysisStr(abbr);
		if (result == null) {
			throw new MelodicAnalysisException("MelodicAnalysisNoteKinds abbreviation " + abbr + " not valid");
		}
		return result;
	}

	/**
	 *
	 * @param abbr
	 * @return null if not found. Use abbrToMelodicAnalysis if an exception is expected
	 */
	public static MelodicAnalysisNoteKinds findAbbrForMelodicAnalysisStr(String abbr)  {
		if (abbr.equals("h") || abbr.equals("husr")) {
			return MelodicAnalysisNoteKinds.HARMONIC; // now, harmonic tones are uppercase
		}
		for (MelodicAnalysisNoteKinds ma: MelodicAnalysisNoteKinds.values()) {
			if (ma.getAbbreviation().equals(abbr) || (ma.getAbbreviation()+"usr").equals(abbr) || (ma.getAbbreviation()+"+").equals(abbr)) {
				return ma;
			}
		}
		return null;
	}

	public boolean isClassifiableKind() {
		return classifiableKind;
	}

	/**
	 * Return just those classifiable melodic analysis note kinds
	 * @return
	 */
	public static List<MelodicAnalysisNoteKinds> valuesClassifiable() {
		ArrayList<MelodicAnalysisNoteKinds> melodicAnalysisNoteKinds = new ArrayList<>();
		for (MelodicAnalysisNoteKinds mak: MelodicAnalysisNoteKinds.values()) {
			if (mak.isClassifiableKind()) {
				melodicAnalysisNoteKinds.add(mak);
			}
		}
		return melodicAnalysisNoteKinds;
	}

	public MelodicFunction toMelodicFunction() {
		return MelodicAnalysisNoteKinds.melodicAnalysisKindToMelodicFunction(this);
	}
	public static MelodicAnalysisNoteKinds melodicFunctionToMelodicAnalysisKind(MelodicFunction mf) {
		switch (mf) {
			case mf43SUS:
			case mf98SUS:
			case mfSUS: return MelodicAnalysisNoteKinds.SUSPENSION;
			case mfANT: return MelodicAnalysisNoteKinds.ANTICIPATION;
			case mfAPP: return MelodicAnalysisNoteKinds.APPOGIATURA;
			case mfCT:
			case mfCT7: return MelodicAnalysisNoteKinds.HARMONIC;
			case mfET: return MelodicAnalysisNoteKinds.ECHAPEE;
			case mfPED: return MelodicAnalysisNoteKinds.PEDAL;
			case mfALN:
			case mfAUN:
			case mfCLN:
			case mfCUN:
			case mfLN:
			case mfUN:
			case mfUN7: return MelodicAnalysisNoteKinds.NEIGHBOUR_TONE;
			case mfAPT:
			case mfCUP:
			case mfUPT:
			case mfUPT7: return MelodicAnalysisNoteKinds.PASSING_TONE;
			default:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "Unsupported melodic function: " + mf);
				return null;
		}
	}


	public static MelodicFunction melodicAnalysisKindToMelodicFunction(MelodicAnalysisNoteKinds kind) {
		switch (kind) {
			case ANTICIPATION: return MelodicFunction.mfANT;
			case ECHAPEE: return MelodicFunction.mfET;
			case APPOGIATURA: return MelodicFunction.mfAPP;
			case HARMONIC:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "There are several MEI types for " + kind + " choosing one of them");
				return MelodicFunction.mfCT; // TODO differenciate between CT and CT7 (Chord tone (7th added to the chord).
			//TODO differenciate ln (Lower neighbor), aln (accented lower neighbor), aun Accented upper neighbor, cln (Chromatic lower neighbor),
			//cun (Chromatic upper neighbor), un (Upper neighbor.), un7 (Upper neighbor (7th added to the chord),
			case NEIGHBOUR_TONE:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "There are several MEI types for " + kind + " choosing one of them");
				return MelodicFunction.mfUN;
			//TODO Differenciate apt Accented passing tone., cup, Chromatic unaccented passing tone., upt Unaccented passing tone.,upt7 Unaccented passing tone (7th added to the chord).
			case PASSING_TONE:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "There are several MEI types for " + kind + " choosing one of them");
				return MelodicFunction.mfUPT;
			case PEDAL: return MelodicFunction.mfPED;
			//TODO Differenciate 43sus 4-3 suspension. , 98sus 9-8 suspension. , 76sus 7-6 suspension.
			case SUSPENSION:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "There are several MEI types for " + kind + " choosing one of them");
				return MelodicFunction.mfSUS;
			default:
				Logger.getLogger(MelodicAnalysisNoteKinds.class.getName()).log(Level.WARNING, "Unsupported melodic function: " + kind);
				return null;
		}
	}




}
