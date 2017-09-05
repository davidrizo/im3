package es.ua.dlsi.im3.core.score;

/**
 * It gives the relative position in a staff for elements with the same time
 * @author drizo
 *
 */
public class StaffPositionComparator {
	private static int getPosition(Object o) {
		// must use instanceof for allowing inheritante
		if (o instanceof Barline) {
			return 0;
		} else if (o instanceof Clef) {
			return 1;
		} else if (o instanceof KeySignature) {
			return 2;
		} else if (o instanceof TimeSignature) {
			return 3;
		} else {
			return 4;
		}
	}
	public static int compare(Object o1, Object o2) {
		return getPosition(o1) - getPosition(o2);
	}

}
