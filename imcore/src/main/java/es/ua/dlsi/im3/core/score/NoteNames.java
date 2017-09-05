package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 05/06/2011
 **/
public enum NoteNames {
	C (0,0),
	D (2,1),
	E (4,2),
	F (5,3),
	G (7,4),
	A (9,5),
	B (11,6),
	REST (-1,-1), // rest
	NONE(-2, -2);
	
	private final int semitonesFromC;
	private final int order; // from 0
	NoteNames(int semitonesFromC, int order) {
		this.semitonesFromC = semitonesFromC;
		this.order = order;
	}
	/**
	 * @return the semitonesFromC
	 */
	public final int getSemitonesFromC() {
		return semitonesFromC;
	}
	/**
	 * @return the order (from C=0 to B=6. -1=rest)
	 */
	public final int getOrder() {
		return order;
	}

    /**
     * Without rest or none
     * @return
     */
	public static NoteNames[] getJustNoteNames() {
        return new NoteNames[] {C,D,E,F,G,A,B};
    }

	/**
	 * @return the base7Name, 0 is the rest, 1 is C, 7 is B
	 */
	public final int getBase7Name() {
	    return order+1;
	}
	/**
	 * 
	 * @param order
	 * @return
	 * @throws IM3Exception 
	 */
	public static NoteNames noteFromOrder(int order) throws IM3Exception {
		for (NoteNames nn : NoteNames.values()) {
			if (nn != REST && nn != NONE && nn.getOrder() == order) {
				return nn;
			}
		}
		throw new IM3Exception("Order " + order + " not found among the orders in note names");
	}
	
	public static NoteNames noteFromName(String name) throws IM3Exception {
		for (NoteNames acc : NoteNames.values()) {
			if (acc.name().equals(name)) {
				return acc;
			}
			
		}
		throw new IM3Exception("Cannot find an NoteName for " + name);
	}

    public static NoteNames noteFromName(char c) throws IM3Exception {
        return noteFromName(Character.toString(c));
    }
}
