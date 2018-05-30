package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.IM3Exception;

public enum Perfection {
	perfectum(3), imperfectum(2), alteratio(2);

	private int divisions;

	Perfection(int divisions) {
	    this.divisions = divisions;
    }

    public int getDivisions() {
        return divisions;
    }

    public static Perfection getPerfection(int divisions) throws IM3Exception {
	    for (Perfection p: Perfection.values()) {
	        if (p.divisions == divisions) {
	            return p;
            }
        }
        throw new IM3Exception("Cannot find Perfection for " + divisions + " divisions");
    }
}
