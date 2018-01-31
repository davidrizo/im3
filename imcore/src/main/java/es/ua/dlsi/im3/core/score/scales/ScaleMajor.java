package es.ua.dlsi.im3.core.score.scales;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScaleMembership;


/**
@author drizo
@date 12/09/2011
 **/
public class ScaleMajor extends ScaleWithNeapolitan {
	public ScaleMajor() {
		super("Major", new int[]{0,-1, 4, 5, 7, 9, 11}); // -1 because the neapolitan is computed in the method with code);
	}

    @Override
    protected ScaleMembership noteBelongsToScale(int degree, int semitones, boolean isLastNote) throws IM3Exception {
        if (degree == 6) {
            if (semitones == 8) {
            return NEAPOLITAN.buildFromThis(this);
            } else if (semitones == 9) {
                return REGULAR_HIGH.buildFromThis(this);
            } else {
                return REGULAR_LOW.buildFromThis(this);
            }
        } else {
            return super.noteBelongsToScale(degree, semitones, isLastNote);
        }
	}
	
}
