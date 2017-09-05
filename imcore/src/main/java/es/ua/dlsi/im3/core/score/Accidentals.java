package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 05/06/2011
 **/
public enum Accidentals {
	TRIPLE_FLAT (-3, "bbb"),
	DOUBLE_FLAT (-2, "bb"),
	FLAT (-1, "b"),
	NATURAL (0, "n"), // explicit natural
	SHARP (1, "#"),
	DOUBLE_SHARP (2, "x");
	
	/**
	 * Alteration that the accidental adds
	 */
	private final int alteration;
	/**
	 * Name, print tempo
	 */
	private final String name;

	/**
	 * @return the alteration
	 */
	public final int getAlteration() {
		return alteration;
	}

	Accidentals(int alter, String aname) {
		this.alteration = alter;
		this.name = aname;
	}

	public static Accidentals alterToAccidentals(int value) throws IM3Exception {
		switch (value) {
		case -3:
			return TRIPLE_FLAT;
		case -2:
			return DOUBLE_FLAT;
		case -1:
			return FLAT;
		case 0:
			return NATURAL;
		case 1:
			return SHARP;
		case 2:
			return DOUBLE_SHARP;
		default:
			throw new IM3Exception("Invalid number of accidentals: " + value);
		}
	}

	@Override
	public String toString() {
		if (alteration == 0) {
			return "";
		} else {
			return this.name;
		}
	}

	public String getAbbrName() {
		return this.name;
	}

	public static Accidentals accidentalForAlter(int alter) throws IM3Exception {
		for (Accidentals acc : Accidentals.values()) {
			if (acc.alteration == alter) {
				return acc;
			}
		}
		throw new IM3Exception("Alteration " + alter + " not found in Accidentals");
	}

	public static Accidentals accidentalFromName(String name) throws IM3Exception {
		for (Accidentals acc : Accidentals.values()) {
			if (acc.name.equals(name)) {
				return acc;
			}

		}
		throw new IM3Exception("Cannot find an accidental for " + name);
	}
}
