package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.scales.ScaleWithNeapolitan;


/**
@author drizo
@date 12/09/2011
 **/
public abstract class Scale {
	protected static final ScaleMembership REGULAR_HIGH = new ScaleMembership("Regular", ScaleMembershipDegree.high);
	protected static final ScaleMembership REGULAR_LOW = new ScaleMembership("Regular", ScaleMembershipDegree.low);

    protected int[] expectedSemitones;

	/**
	 * Scale name
	 */
	private final String name;
	/**
	 * @param name
	 */
	public Scale(String name, int[] expectedSemitones) {
		super();
		this.name = name;
        this.expectedSemitones = expectedSemitones;
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

    //TODO ¿esto debe ir aquí o en instrumentKey?
    /**
     * It returns the membership degree of a note to a given scale
     * @param note
     * @param isLastNote (for piccardy third)
     * @return
     * @throws IM3Exception
     */
    public ScaleMembership noteBelongsToScale(Key key, PitchClass note, boolean isLastNote) throws IM3Exception {
        if (note.isRest()) {
            throw new IM3Exception("A rest never belongs to a scale");
        }

        int degree = key.computeDegree(note);
        int semitones = key.computeSemitonesFromKey(note);

        return noteBelongsToScale(degree, semitones, isLastNote);
    }

    /**
     * Used to allow the use of precomputed degree and semitones
     *
     * @param degree
     * @param semitones
     * @return
     * @throws IM3Exception
     */
    protected ScaleMembership noteBelongsToScale(int degree, int semitones, boolean isLastNote) throws IM3Exception {
        return (semitones == expectedSemitones[degree - 1]) ? REGULAR_HIGH.buildFromThis(this)
                : REGULAR_LOW.buildFromThis(this);
    }


	/**
	 * Several values for cases like the neapolitean, where the 2nd degree can be several notes with several degrees
	 * @param tonic
	 * @param degree
	 * @param isLastMeasure (for piccardy third)
	 * @return
	 * @throws IM3Exception 
	 */
	public HashMap<PitchClass, ScaleMembership> computeDegree(PitchClass tonic, Degree degree, boolean isLastMeasure) throws IM3Exception {
        MotionDirection dir;
        if (degree == Degree.I) {
            dir = MotionDirection.EQUAL;
        } else {
            dir = MotionDirection.ASCENDING;
        }

        HashMap<PitchClass, ScaleMembership> result = new HashMap<>();
        int semitones = expectedSemitones[degree.ordinal() - 1];
        result.put(Intervals.getInterval(degree.ordinal(), semitones, dir).computePitchClassFrom(tonic),
                REGULAR_HIGH.buildFromThis(this));
        return result;
    }
	
	public List<PitchClass> generateOneOctaveScale(Key key) {
        ArrayList<PitchClass> result = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            int semitones = expectedSemitones[i - 1];
            try {
                result.add(
                        Intervals.getInterval(i, semitones, i == 1 ? MotionDirection.EQUAL : MotionDirection.ASCENDING)
                                .computePitchClassFrom(key.getPitchClass()));
            } catch (IM3Exception ex) {
                Logger.getLogger(ScaleWithNeapolitan.class.getName()).log(Level.SEVERE, null, ex);
                throw new IM3RuntimeException("Cannot generate the scale", ex);
            }
        }
        return result;
    }
}
