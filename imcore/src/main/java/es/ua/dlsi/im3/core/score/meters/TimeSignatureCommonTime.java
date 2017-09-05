/*
 * Copyright (C) 2013 David Rizo Valero
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

package es.ua.dlsi.im3.core.score.meters;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ITimeSignatureWithDuration;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.TimeSignature;

/**
 *
 * @author drizo
 */
public class TimeSignatureCommonTime extends TimeSignature implements ITimeSignatureWithDuration {

    public TimeSignatureCommonTime(NotationType notationType) {
    		super(notationType);
    }

    @Override
    public String toString() {
    		return "C";
    }

	@Override
	public Time getMeasureDuration() {
		return new Time(Figures.WHOLE.getDuration());
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof TimeSignatureCommonTime;
	}


	@Override
	public boolean isCompound() {
		return false;
	}

}
