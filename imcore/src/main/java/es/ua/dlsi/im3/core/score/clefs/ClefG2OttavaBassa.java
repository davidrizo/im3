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
import es.ua.dlsi.im3.core.score.NoteNames;

/**
 *
 * @author drizo
 */
public class ClefG2OttavaBassa extends Clef {
   
    public ClefG2OttavaBassa() {
	//super(NoteNames.G, 2, 3, 5, 4);
    		super(NoteNames.G, 2, 3, 5, 4, -1);
    }
    @Override
    public Clef clone() {
    		return new ClefG2OttavaBassa();
    }

}
