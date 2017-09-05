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

package es.ua.dlsi.im3.core.score;

/**
 *
 * @author drizo
 */
public class KeySignatureAccidentalElement {
    int order;
    NoteNames noteName;
    int octave;
    Accidentals accidental;

    /**
     * 
     * @param coreSymbol
     * @param noteName
     * @param octaveRelativeToStartingOne
     * @param order From 1
     */
    public KeySignatureAccidentalElement(KeySignature coreSymbol, NoteNames noteName, int octave, int order) {
    		this.accidental = coreSymbol.getAccidental();
		this.octave = octave;
		this.order = order;
		this.noteName = noteName;
    }

    public int getOrder() {
        return order;
    }

    public int getOctave() {
        return octave;
    }

    public Accidentals getAccidental() {
        return accidental;
    }

    public NoteNames getNoteName() {
        return noteName;
    }
}
