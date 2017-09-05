package es.ua.dlsi.im3.core.score;
/**
@author drizo
@date 19/06/2011
 **/
public enum TonalFunction {
	// mantain this order because the ordinal may be used
	NONE(""),
	TONIC ("T"),
	DOMINANT ("D"),
	SUBDOMINANT ("S");
	private static final String SUBDOMINANT_ALTERNATIVE_STR = "SD";
	
	String abbr;
	TonalFunction(String abbr) {
		this.abbr = abbr;
	}
	/**
	 * @return the abbr
	 */
	public final String getAbbr() {
		return abbr;
	}
	
	public static TonalFunction[] validValues() {
		return new TonalFunction[] {TONIC, DOMINANT, SUBDOMINANT};
	}
	
	/**
	 * 
	 * @param t
	 * @return null if not found
	 */
	public static TonalFunction getTonalFunctionFromString(String t) {
	    for (TonalFunction tf : TonalFunction.values()) {
		if (tf.getAbbr().equals(t)) {
		    return tf;
		} else if (t.equals(SUBDOMINANT_ALTERNATIVE_STR)) {
		    return SUBDOMINANT;
		}
	    }
	    return null;
	}
	
}
