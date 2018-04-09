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

/**
 * This clef is artificial, used for some experiments
 * @author drizo
 */
public class ClefG3 extends Clef {

    public ClefG3() {
        //20180207 		super(DiatonicPitch.G, 2, 4, 5, 4);
        super(DiatonicPitch.G, 3, 4,
                new PositionInStaff[] {SPACE_2, LINE_1, LINE_3, SPACE_1, SPACE_3, LINE_2, LINE_4},
                new PositionInStaff[] {LINE_4, LINE_2, SPACE_3, SPACE_1, LINE_3, LINE_1, SPACE_2});
	//super(DiatonicPitch.G, 2, 4, 5, 4);
    }
    @Override
    public Clef clone() {
    		return new ClefG3();
    }
    
}