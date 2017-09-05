package es.ua.dlsi.im3.core.score;

import java.util.HashMap;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;


/**
@author drizo
@date 12/09/2011
 **/
public abstract class Scale {
	protected static final ScaleMembership REGULAR_HIGH = new ScaleMembership("Regular", ScaleMembershipDegree.high);
	protected static final ScaleMembership REGULAR_LOW = new ScaleMembership("Regular", ScaleMembershipDegree.low);
	/**
	 * Scale name
	 */
	private final String name;
	/**
	 * @param name
	 */
	public Scale(String name) {
		super();
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	@Override
	public final String toString() {
		return name;
	}

	/**
	 * It returns the membership degree of a note to a given scale
     * @param instrumentKey
	 * @param note
	 * @param isLastNote (for piccardy third) 
	 * @return
	 * @throws IM3Exception
	 */
	public abstract ScaleMembership noteBelongsToScale(Key key, PitchClass note, boolean isLastNote) throws IM3Exception;

	/**
	 * Several values for cases like the neapolitean, where the 2nd degree can be several notes with several degrees
	 * @param tonic
	 * @param degree
	 * @param isLastMeasure (for piccardy third)
	 * @return
	 * @throws IM3Exception 
	 */
	public abstract HashMap<PitchClass, ScaleMembership> computeDegree(PitchClass tonic, Degree degree, boolean isLastMeasure) throws IM3Exception;
	
	public abstract List<PitchClass> generateOneOctaveScale(Key key);
}
