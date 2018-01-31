package es.ua.dlsi.im3.core.score.scales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Degree;
import es.ua.dlsi.im3.core.score.Intervals;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.MotionDirection;
import es.ua.dlsi.im3.core.score.PitchClass;
import es.ua.dlsi.im3.core.score.Scale;
import es.ua.dlsi.im3.core.score.ScaleMembership;
import es.ua.dlsi.im3.core.score.ScaleMembershipDegree;

/**
 * @author drizo
 * @date 12/09/2011
 *
 */
public abstract class ScaleWithNeapolitan extends Scale {

	public static final ScaleMembership NEAPOLITAN = new ScaleMembership("Neapolitan", ScaleMembershipDegree.medium);
	// protected static final ScaleMembership SECONDARY_DOMINANT = new
	// ScaleMembership("Secondary dominant", ScaleMembershipDegree.medium);

	public ScaleWithNeapolitan(String name, int[] expectedSemitones) {
		super(name, expectedSemitones);
	}


	/**
	 * Used to allow the use of precomputed degree and semitones
	 *
	 * @param degree
	 * @param semitones
	 * @return
	 * @throws IM3Exception
	 */
	@Override
	protected ScaleMembership noteBelongsToScale(int degree, int semitones, boolean isLastNote) throws IM3Exception {
		if (degree == 2) {
			if (semitones == 2) {
				return REGULAR_HIGH.buildFromThis(this);
			} else if (semitones == 1) {
				return NEAPOLITAN.buildFromThis(this);
			} else {
				return REGULAR_LOW.buildFromThis(this);
			}
		} else if (degree == 1) {
			/*
			 * if (semitones == 1) { return
			 * SECONDARY_DOMINANT.buildFromThis(this); } else
			 */
			if (semitones == 0) {
				return REGULAR_HIGH.buildFromThis(this);
			} else {
				return REGULAR_LOW.buildFromThis(this);
			}
		} else if (degree == 4) {
			/*
			 * if (semitones == 6) { return
			 * SECONDARY_DOMINANT.buildFromThis(this); } else
			 */
			if (semitones == 5) {
				return REGULAR_HIGH.buildFromThis(this);
			} else {
				return REGULAR_LOW.buildFromThis(this);
			} // neapolitan for 6th is checked in the major and minor scales
		} else {
		    return super.noteBelongsToScale(degree, semitones, isLastNote);
		}
	}

	@Override
	public HashMap<PitchClass, ScaleMembership> computeDegree(PitchClass tonic, Degree degree, boolean isLastNote) throws IM3Exception {
		if (degree == Degree.II) {
            MotionDirection dir;
            if (degree == Degree.I) {
                dir = MotionDirection.EQUAL;
            } else {
                dir = MotionDirection.ASCENDING;
            }

            HashMap<PitchClass, ScaleMembership> result = new HashMap<>();

			result.put(Intervals.getInterval(degree.ordinal(), 2, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
			//Logger.getLogger(ScaleWithNeapolitan.class.getName()).log(Level.SEVERE,
			//		"De momento quito la napolitana que me da error");
			//try {
				result.put(Intervals.getInterval(degree.ordinal(), 1, dir).computePitchClassFrom(tonic), NEAPOLITAN.buildFromThis(this));
			//} catch (IM3Exception e) {
				//Logger.getLogger(ScaleWithNeapolitan.class.getName()).log(Level.WARNING, "TO-DO NEAPOLITAN error???", e);
			//}
			// TODO ¿otros acordes como belongs to scale?
            return result;
		} else {
		    return super.computeDegree(tonic, degree, isLastNote);
			// TODO ¿otros acordes como belongs to scale?
			// return (semitones == expectedSemitones[degree-1]) ?
			// REGULAR_HIGH.buildFromThis(this):
			// REGULAR_LOW.buildFromThis(this);
		}
	}

	@Override
	public List<PitchClass> generateOneOctaveScale(Key key) {
		ArrayList<PitchClass> result = new ArrayList<>();

		for (int i = 1; i <= 7; i++) {
			int semitones;
			if (i == 2) {
				semitones = 2;
			} else {
				semitones = expectedSemitones[i - 1];
			}
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
