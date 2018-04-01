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

import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 * TODO Deberían haber claves asociadas a tipos de staff (no tiene sentido
 * percusión con clave de fa)
 *
 * @author drizo
 */
public abstract class Clef implements INotationTypeDependant, ITimedElementInStaff {
    private final PositionInStaff[] flatPositions;
    private final PositionInStaff[] sharpPositions;
    DiatonicPitch note;
    /**
     * Bottom line is 1, in a pentagram, top line is 5
     */
	int line;
	Time time;
	Staff staff;
	/**
	 * e.g 4 for G2, 3 for F4
	 */
	int noteOctave;
	/**
	 * 1 for ottava alta, -1 for ottava bassa
	 */
	private final int octaveChange;
	private NotationType notationType;
    /**
     * Staff bottom line (1) diatonic pitch. We save it for avoiding its recomputing each time it is needed
     */
	private DiatonicPitch bottomLineDiatonicPitch;
    /**
     * Staff bottom line (1) pitch octave. We save it for avoiding its recomputing each time it is needed
     */
    private int bottomLineOctave;

	public Clef(DiatonicPitch note, int line, int noteOctave, PositionInStaff [] sharpPositions, PositionInStaff [] flatPositions
                ) {
        //20180207 this (note, line, noteOctave, sharpKeySignatureStartingOctave, flatKeySignatureStartingOctave, 0);
        this (note, line, noteOctave, 0, sharpPositions, flatPositions);
	}

    /**
     *
     * @param note
     * @param line
     * @param noteOctave
     * @param octaveChange
     * @param sharpPositions For key signatures
     * @param flatPositions For key signatures
     */
	public Clef(DiatonicPitch note, int line, int noteOctave,
                int octaveChange, PositionInStaff [] sharpPositions, PositionInStaff [] flatPositions
                ) {
		this.time = new Time();
		this.octaveChange = octaveChange;
		this.note = note;
		this.line = line;
		this.noteOctave = noteOctave; // clef.getOctaveTransposition();
        this.sharpPositions = sharpPositions;
        this.flatPositions = flatPositions;

        // 	private int computeBottomLineNoteOrder(Clef clef) {

        // compute staff bottom line diatonic pitch
        int noteOrder = note == null?0:(note.getOrder() + noteOctave * 7);
        int bottomLineNoteOrder = noteOrder - (line - 1) * 2;
        this.bottomLineDiatonicPitch = DiatonicPitch.values()[bottomLineNoteOrder % 7];
        this.bottomLineOctave = bottomLineNoteOrder / 7;
    }


	public DiatonicPitch getNote() {
		return note;
	}

    /**
     * Bottom line is 1, in a pentagram, top line is 5
     * @return
     */
	public int getLine() {
		return line;
	}

	public int getNoteOctave() {
		return noteOctave;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + this.note.getOrder();
		hash = 53 * hash + this.line;
		hash = 53 * hash + this.noteOctave;
		Staff stf = getStaff();
		if (stf != null) {
		    hash = 53 * hash + stf.hashCode();
        }
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Clef other = (Clef) obj;
		if (this.note != other.note) {
			return false;
		}
		if (this.line != other.line) {
			return false;
		}
		return this.noteOctave == other.noteOctave;
	}

	@Override
	public String toString() {
		return "Clef{" + "note=" + note + ", line=" + line + ", noteOctave=" + noteOctave + '}';
	}

	public int getOctaveChange() {
		return octaveChange;
	}

	@Override
	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public final NotationType getNotationType() {
		return notationType;
	}

	@Override
	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public final void setNotationType(NotationType notationType) {
		this.notationType = notationType;
	}

	public abstract Clef clone();

	public DiatonicPitch getBottomLineDiatonicPitch() {
		return bottomLineDiatonicPitch;
	}

    public int getBottomLineOctave() {
        return bottomLineOctave;
    }

    public PositionInStaff[] getFlatPositions() {
        return flatPositions;
    }

    public PositionInStaff[] getSharpPositions() {
        return sharpPositions;
    }
}
