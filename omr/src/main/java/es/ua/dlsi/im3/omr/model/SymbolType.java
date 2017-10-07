package es.ua.dlsi.im3.omr.model;

public enum SymbolType {
	longa, longa_rest, 
	brevis, coloured_brevis, brevis_rest,
	semibrevis, coloured_semibrevis, semibrevis_rest,
	minima, coloured_minima, minima_rest,
	semiminima, coloured_semiminima, semiminima_rest, 
	sharp, flat, dot, beam, fermata, 
	proportio_maior, proportio_minor, common_time, cut_time, 
	g_clef, f_clef_1, f_clef_2, c_clef,   
	barline, double_barline, 
	custos, ligature, undefined;
	
	public static SymbolType parseString(String label) {
		SymbolType result = SymbolType.valueOf(label);
		if (result != null) {
			return result;
		}
		// if older symbols (Jorge's version) are found - on saving they will be updated
	    switch (label) {
	    	case "barline":
		    return barline;
	    	case "minima":
		    return minima;
	    	case "semibrevisrest":
		    return semibrevis_rest;
	    	case "brevisrest":
		    return brevis_rest;
	    	case "semiminima":
		    return semiminima;
	    	case "dot":
		    return dot;
	    	case "brevis":
		    return brevis;
	    	case "sharp":
		    return sharp;
	    	case "longarest":
		    return longa_rest;
	    	case "cclef":
		    return c_clef;
	    	case "endsymbol":
		    return custos;
	    	case "time":
		    return proportio_maior;
	    	case "semibrevis":
		    return semibrevis;
	    	case "semibrevisblack":
		    return coloured_semibrevis;
	    	case "flat":
		    return flat;
	    	case "fermata":
		    return fermata;
	    	case "minimarest":
		    return minima_rest;
	    	case "doublebarline":
		    return double_barline;
	    	case "longa":
		    return longa;
	    	case "brevisblack":
		    return coloured_brevis;
	    	case "fclef1":
		    return f_clef_1;
	    	case "fclef2":
		    return f_clef_2;
	    	case "minortime":
		    return proportio_minor;
	    	case "cuttime":
		    return cut_time;
	    	case "gclef":
		    return g_clef;
	    	case "semiminimarest":
		    return semiminima_rest;
	    	case "hook":
		    return beam;
	    	case "commontime":
		    return common_time;
	    	case "semiminimablack":
		    return coloured_semiminima;
	    	case "minimablack":
	    	case "hooked":
		    return coloured_minima;
	    	case "ligature":
	    		return ligature;
	    	case "undefined":
	    		return undefined;
	    	default:
		    throw new RuntimeException("Unhandled: " + label);
	    }
		
	}

	public static int size() {
		return values().length;
	}
}
