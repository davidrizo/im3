package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 06/06/2011
 **/
public enum Mode {
	MAJOR ("M"),
	MINOR ("m"),
	UNKNOWN ("unknown"); // for cases in which the major has been chosen because it is unknown
	/**
	 * Most usual string translation
	 */
	String abbreviation;
	
	Mode(String n) {
		this.abbreviation = n;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return abbreviation;
	}

	/**
	 * @return the name
	 */
	public final char getNameChar() {
		return abbreviation.charAt(0);
	}
	
	public static Mode stringToMode(String s) throws IM3Exception {
		if (s.equalsIgnoreCase(MAJOR.name()) || s.equals(MAJOR.abbreviation)) {
			return MAJOR;
		} else if (s.equalsIgnoreCase(MINOR.name()) || s.equals(MINOR.abbreviation)) {
			return MINOR;
		} else if (s.equalsIgnoreCase(UNKNOWN.name()) || s.equals(UNKNOWN.abbreviation))	{
		    return UNKNOWN;
		} else {
			throw new IM3Exception("Invalid mode: '" + s + "'");
		}
	}
	
	public static Mode[] validValues() {
		return new Mode[] {MAJOR, MINOR};
	}
	
	
}
