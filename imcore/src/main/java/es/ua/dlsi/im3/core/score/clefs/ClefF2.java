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

package es.ua.dlsi.im3.core.score.clefs;

import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.PositionInStaff;

import static es.ua.dlsi.im3.core.score.PositionsInStaff.*;
import static es.ua.dlsi.im3.core.score.PositionsInStaff.LINE_1;

/**
 *
 * @author drizo
 */
public class ClefF2 extends Clef {

    public ClefF2() {
        //20180207 	super(DiatonicPitch.F, 2, 3, 3, 2);
        super(DiatonicPitch.F, 2, 3,
                new PositionInStaff[] {LINE_2, SPACE_0, SPACE_2, LINE_1, LINE_3, SPACE_1, SPACE_3},
                new PositionInStaff[] {SPACE_3, LINE_5, LINE_3, SPACE_4, SPACE_2, LINE_4, LINE_2});
	//super(DiatonicPitch.F, 4, 3, 3, 2);
    }
    @Override
    public Clef clone() {
    		return new ClefF2();
    }
    
}
