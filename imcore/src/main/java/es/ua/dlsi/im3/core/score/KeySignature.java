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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;


/**
 *
 * @author drizo
 */
public class KeySignature implements INotationTypeDependant, ITimedElementInStaff, ITimedElementWithSet, IUniqueIDObject {
	List<KeySignatureAccidentalElement> accidentals;
	private Accidentals accidental;
    NotationType notationType;
	private Staff staff;
	/**
	 * Used in layout algorithms
	 */
	TreeMap<DiatonicPitch, PitchClass> alteredDiatonicPitchSet;
	/**
	 * This is the instrumentKey shown in the staff, i.e., written pitch key. For a transposing instrument it will be different from
	 * the other keys. E.g. in a piano instrumentKey = concertPitchKey, e.g. C Major, for a Trumpet in Bb, 
	 * the instrumentKey will be G Major, the transposed instrumentKey will be C Major
	 */
	Key instrumentKey;
	String ID;
	Time time;
	
	/**
	 * For transposing instruments. The interval that has to be added to the notes
	 */
	private Interval transpositionInterval;
	/**
	 * In the case of non transposed instruments, this will equal to instrumentKey, , i.e., sounded pitch key, for transposing instruments
	 * it will be not the printed instrumentKey signature, but normally the instrumentKey common to all staves (see instrumentKey attribute)
	 */
	private Key concertPitchKey;

	public KeySignature(NotationType notationType, Key key){
		this.notationType = notationType;
		this.concertPitchKey = key;
		this.instrumentKey = key;
		this.time = new Time();
		init();
	}

	public List<KeySignatureAccidentalElement> getAccidentals() {
		return accidentals;
	}

	/**
	 * 
	 * @return Accidentals.NONE, it is a CMajor o Aminor (no accidental)
	 */
	public Accidentals getAccidental() {
		return accidental;
	}

	public String toDebugString() {
		return "KS#" + accidentals.size() + accidental.getAbbrName();
	}


	public void setTranspositionInterval(Interval transpositionInterval) throws IM3Exception  {
		this.transpositionInterval = transpositionInterval;
		PitchClass transposedKeyPitchClass = transpositionInterval.computePitchClassFrom(instrumentKey.getPitchClass());
		this.concertPitchKey = new Key(transposedKeyPitchClass, instrumentKey.getMode());
	}

	public Interval getTranspositionInterval() {
		return transpositionInterval;
	}

	private void init()  {
		this.accidental = instrumentKey.getAccidental();
		DiatonicPitch[] alteredNoteNames = instrumentKey.getAlteredNoteNames();
		accidentals = new ArrayList<>();

		if (alteredNoteNames.length != 0) {
			int octave = 0;
			int previousNoteOrder = 0;

			boolean nextUp = (accidental == Accidentals.SHARP);
			int i = 1;

			for (DiatonicPitch nn : alteredNoteNames) {
				int noteOrder = nn.getOrder() + octave * 7;
				if (i > 1) {
					if (nextUp) {
						if (noteOrder < previousNoteOrder) {
							octave++;
						}
					} else { // next down
						if (noteOrder > previousNoteOrder) {
							octave--;
						}
					}
				}
				previousNoteOrder = nn.getOrder() + octave * 7;
				nextUp = !nextUp;

				// Use the octave just as a distance in octaves from that specified by the clef for this accidental 
				KeySignatureAccidentalElement p = new KeySignatureAccidentalElement(this, nn, octave, i); 

				accidentals.add(p);
				i++;
			}
		}

        alteredDiatonicPitchSet = new TreeMap<>();
        Accidentals acc = getAccidental();
        for (DiatonicPitch nn1 : alteredNoteNames) {
            alteredDiatonicPitchSet.put(nn1, new PitchClass(nn1, acc));
        }

	}

	/**
	 * See instrumentKey attribute documentation
	 * @return
	 */
	public Key getConcertPitchKey() {
		return this.concertPitchKey;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.getStaff());
		hash = 89 * hash + Objects.hashCode(this.instrumentKey);
		if (this.concertPitchKey != null) {
			hash = 89 * hash + Objects.hashCode(concertPitchKey);
		}
		return hash;
	}

	// It could be added graphical symbols comparison
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final KeySignature other = (KeySignature) obj;
		if (!Objects.equals(this.getStaff(), other.getStaff())) {
			return false;
		}
		if (!Objects.equals(this.instrumentKey, other.instrumentKey)) {
			return false;
		}
        return Objects.equals(concertPitchKey, other.concertPitchKey);
    }

	@Override
	public String toString() {
		String transposition;
		if (instrumentKey != concertPitchKey) {
			transposition = ", concert pitch = " + concertPitchKey;
		} else {
			transposition = "";
		}
		if (this.accidentals.isEmpty()) {
			return "Key signature with no accidental, and key " + instrumentKey.toString() + transposition;
		} else {
			return "Key signature with " + this.accidentals.size() + " " + this.accidental.getAbbrName() + " and key " + instrumentKey.toString() + transposition;
		}
	}
	@Override
	public Time getTime() {
		return time;
	}
	
	@Override
	public NotationType getNotationType() {
		return notationType;
	}

	@Override
	public Staff getStaff() {
		return staff;
	}

	@Override
	public void setStaff(Staff staff) {
		this.staff = staff;
		
	}
	/**
	 * See instrumentKey attribute documentation
	 * @return
	 */
	public Key getInstrumentKey() {
		return instrumentKey;
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
		
	}

	@Override
	public String __getIDPrefix() {
		return "KS";
	}
	@Override
	public void setTime(Time time) {
		this.time = time;
	}
    @Override
    public void move(Time offset) throws IM3Exception {
	    staff.remove(this);
	    if (this.time == null) {
	    	time = offset;
		} else {
			this.time = time.add(offset);
		}

        staff.addCoreSymbol(this);
    }
    public TreeMap<DiatonicPitch, PitchClass> getAlteredDiatonicPitchSet() {
        return alteredDiatonicPitchSet;
    }

    public PositionInStaff [] computePositionsOfAccidentals() throws IM3Exception {
	    if (accidental.equals(Accidentals.NATURAL)) {
            return new PositionInStaff[]{};
        }

        if (staff == null) {
	        throw new IM3Exception("Cannot compute the positions of accidentals for a key signature not in a staff");
        }

        Clef clef = staff.getClefAtTime(this.getTime());
        if (clef == null) {
            throw new IM3Exception("Cannot compute positions of accidentals without a clef at time " + this.getTime());
        }

        PositionInStaff [] result;
        DiatonicPitch[] alteredNoteNames = getInstrumentKey().getAlteredNoteNames();
        if (alteredNoteNames == null) {
            return null;
        } else {
            result = new PositionInStaff[alteredNoteNames.length];
        }

        PositionInStaff [] clefPositions;

        if (accidental == Accidentals.FLAT) {
            clefPositions = clef.getFlatPositions();
        } else if (accidental == Accidentals.SHARP) {
            clefPositions = clef.getSharpPositions();
        } else {
            throw new IM3Exception("Accidental should be flat or sharp and it is " + accidental);
        }

        for (int i=0; i<result.length; i++) {
            result[i] = clefPositions[i];
        }
        return result;
    }

    /*20180207 public PositionInStaff [] computePositionsOfAccidentals() throws IM3Exception {
	    PositionInStaff [] result;
        int previousNoteOrder = 0;
        DiatonicPitch[] alteredNoteNames = getInstrumentKey().getAlteredNoteNames();
        if (alteredNoteNames == null) {
            return null;
        } else {
            result = new PositionInStaff[alteredNoteNames.length];
        }
        boolean nextUp = (accidental == Accidentals.SHARP);
        int i = 1;
        double nextRelativeXPosition = 0;
        int octave = getStartingOctave();
        for (DiatonicPitch nn : alteredNoteNames) {
            int noteOrder = nn.getOrder() + octave * 7;
            if (i > 1) {
                if (nextUp) {
                    if (noteOrder < previousNoteOrder) {
                        octave++;
                    }
                } else { // next down
                    if (noteOrder > previousNoteOrder) {
                        octave--;
                    }
                }
            }
            previousNoteOrder = nn.getOrder() + octave * 7;
            nextUp = !nextUp;

            PositionInStaff positionInStaff = staff.computePositionForPitchWithoutClefOctaveChange(time, nn, octave);
            if (positionInStaff.getLineSpace() <= 0) {
                // if falls below bottom line
                positionInStaff = new PositionInStaff(positionInStaff.getLineSpace() + 7);
            } else if (positionInStaff.getLineSpace() >= 10) {
                // if falls above top space
                positionInStaff = new PositionInStaff(positionInStaff.getLineSpace() - 7);
            }
            result[i-1] = positionInStaff;
            i++;
        }
        return result;
    }*/

    /*20180207 private int getStartingOctave() throws IM3Exception {
        if (accidental.equals(Accidentals.NATURAL)) {
            return 0;
        } else {
            if (staff == null) {
                throw new IM3Exception("Cannot compute the starting octave without a staff set");
            }
            Time time = getTime();
            Clef clef = staff.getClefAtTime(getTime());
            if (clef == null) {
                throw new IM3Exception("No active clef at time " + time);
            }
            return clef.getStartingOctave(accidental);
        }
    }*/

    /**
     *
     * @param noteName
     * @return null if the note is not among the altered notes in the key signature, or the accidental of the key signature
     * if present
     */
    public Accidentals getAccidentalOf(DiatonicPitch noteName) {
        if (this.alteredDiatonicPitchSet.containsKey(noteName)) {
            return accidental;
        } else {
            return null;
        }
    }
}
