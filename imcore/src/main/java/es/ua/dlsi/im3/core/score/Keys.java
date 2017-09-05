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

package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author drizo
 */
public class Keys {
    private static ArrayList<Key> keys;
    private static void build() {
	keys = new ArrayList<>();
	/*for (NoteNames nn: NoteNames.values()) {
	    if (nn != NoteNames.REST) {
		List<Accidentals> accs;
		if (nn == NoteNames.C) {
		    accs = Arrays.asList(Accidentals.NATURAL, Accidentals.SHARP);
		} else if (nn == NoteNames.B) {
		    accs = Arrays.asList(Accidentals.FLAT, Accidentals.NATURAL);
		} else {
		    accs = Arrays.asList(Accidentals.FLAT, Accidentals.NATURAL, Accidentals.SHARP);		
		}
		for (Accidentals acc: accs) {
		    PitchClass pc = new PitchClass(nn, acc);
		    for (Mode mode: Arrays.asList(Mode.MAJOR, Mode.MINOR)) {
			try {
			    keys.add(new Key(pc, mode));
			} catch (IM3Exception ex) {
			    Logger.getLogger(Keys.class.getName()).log(Level.SEVERE, null, ex);
			    throw new IM3RuntimeException("Cannot build the keys collection: " + ex.getMessage());
			}
		    }
		}
	    }
	}*/
	for (KeysEnum k: KeysEnum.values()) {
	    keys.add(k.getKey());
	}
    }

    public static List<Key> getKeys() {
	if (keys == null) {
	    build();
	}
	return keys;
    }
    
    
}
