package es.ua.dlsi.im3.core.score.scales;

import java.util.HashMap;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Degree;
import es.ua.dlsi.im3.core.score.Intervals;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.MotionDirection;
import es.ua.dlsi.im3.core.score.PitchClass;
import es.ua.dlsi.im3.core.score.ScaleMembership;
import es.ua.dlsi.im3.core.score.ScaleMembershipDegree;

/**
@author drizo
@date 12/09/2011
 **/
public class ScaleMinor extends ScaleWithNeapolitan {
	public static final ScaleMembership PICARDY_THIRD = new ScaleMembership("Picardy third", ScaleMembershipDegree.medium);
	public ScaleMinor() {
		super("Minor ascending and descending", new int [] {0, -1, 3, 5, 7, 9, 11});
	}
	
	public ScaleMinor(String name, int [] expectedSemitones) {
		super(name, expectedSemitones);
	}
	@Override
	public ScaleMembership noteBelongsToScale(Key key, PitchClass note, boolean isLastNote) throws IM3Exception {
		if (note.isRest()) {
			throw new IM3Exception("A rest never belongs to a scale");
		}
		
		int degree = key.computeDegree(note);
		int semitones = key.computeSemitonesFromKey(note);
		
		if (degree == 3 && semitones == 4) {
			return PICARDY_THIRD; //TODO Test unitario
		} else if (degree == 6) {
			if (semitones == 8 || semitones == 9) {
				return REGULAR_HIGH;
			} else {
				return REGULAR_LOW;
			}
		} else if (degree == 7) {
			if (semitones == 10 || semitones == 11) {
				return REGULAR_HIGH;
			} else {
				return REGULAR_LOW;
			}
		} else {
			return noteBelongsToScale(degree, semitones, isLastNote);
		}
	}	

	@Override
	public HashMap<PitchClass, ScaleMembership> computeDegree(PitchClass tonic, Degree degree, boolean isLastNote) throws IM3Exception {
		MotionDirection dir;
		if (degree == Degree.I) {
			dir = MotionDirection.EQUAL;
		} else {
			dir = MotionDirection.ASCENDING;
		}

		HashMap<PitchClass, ScaleMembership> result = new HashMap<>();		
		if (degree == Degree.III) {
			result.put(Intervals.getInterval(degree.ordinal(), 3, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
			if (isLastNote) {
				result.put(Intervals.getInterval(degree.ordinal(), 4, dir).computePitchClassFrom(tonic),
						PICARDY_THIRD);
			}
		} else if (degree == Degree.VI) {
			result.put(Intervals.getInterval(degree.ordinal(), 8, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
			result.put(Intervals.getInterval(degree.ordinal(), 9, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
		} else if (degree == Degree.VII) {
			result.put(Intervals.getInterval(degree.ordinal(), 10, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
			result.put(Intervals.getInterval(degree.ordinal(), 11, dir).computePitchClassFrom(tonic),
					REGULAR_HIGH.buildFromThis(this));
		} else {
			result = super.computeDegree(tonic, degree, isLastNote);
		}
		return result;
	}
	
}
