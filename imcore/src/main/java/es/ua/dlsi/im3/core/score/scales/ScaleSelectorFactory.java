/*
 * Copyright (C) 2014 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ua.dlsi.im3.core.score.scales;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Mode;
import es.ua.dlsi.im3.core.score.MotionDirection;
import es.ua.dlsi.im3.core.score.Scale;

/**
 *
 * @author drizo
 */
public class ScaleSelectorFactory {
	public static final ScaleMinor SCALE_MINOR = new ScaleMinor();
	public static final ScaleMajor SCALE_MAJOR = new ScaleMajor();
	
    public static Scale selectScale(Mode mode, MotionDirection direction) throws IM3Exception {
	    if (mode == null || mode == Mode.MAJOR || mode == Mode.UNKNOWN) {
		return new ScaleMajor();
	    } else if (direction == null || direction == MotionDirection.UNDEFINED || direction == MotionDirection.EQUAL) {
		return new ScaleMinorHarmonic(); 
	    } else if (direction == MotionDirection.ASCENDING) {
		return new ScaleMinorMelodicAsc();
	    } else if (direction == MotionDirection.DESCENDING) {
		return new ScaleMinorMelodicDesc();
	    } else {
		throw new IM3Exception("Invalid condition to obtain the scale: mode = " + mode + " and direction = " + direction);
	    }	
    }
    
    public static Scale[] selectAllMinorScales() {
	return new Scale [] {new ScaleMinorHarmonic(), new ScaleMinorMelodicAsc(), new ScaleMinorMelodicDesc()};
    }
}
