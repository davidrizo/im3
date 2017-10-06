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

import java.util.*;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;

/**
 * It is just a visual holder for elements. The note, chord and rest sequences are stored in ScoreLayer,
 * the meters in song. The instrumentKey signatures, for being able to represent transposing instruments are stored
 * here
 *
 * @author drizo
 */
public abstract class Staff extends VerticalScoreDivision {
	/**
	 * The staff belongs either to a page or is grouped into a system
	 */
	private StaffGroup parentSystem;

	protected int lines;

	/**
	 * Used to be able to quick locate a clef given a time. 
	 */
	TreeMap<Time, Clef> clefs;
	/**
	 * This is the only place there the instrumentKey signatures are stored
	 */
	TreeMap<Time, KeySignature> keySignatures;
	/**
	 * There cannot be a common meter for all the staves in the song because there may be
	 * different staves with different time signatures (e.g. Douce/Garison, Philippe de Vitry) 
	 */
	TreeMap<Time, TimeSignature> timeSignatures;
	
	/**
	 * Notes, rests, chords, time signatures, instrumentKey signatures, clefs, barlines 
	 * The order by time is not guaranteed here. It contains the time signatures, clefs, and instrumentKey signatures as well 
	 */
	ArrayList<ITimedElementInStaff> coreSymbols;

	protected TreeMap<Time, Fermate> fermate;
	
	/**
	 * Ledger lines are stored here as well. They are store in the ledgerLines tree map for speed up
	 * purposes
	 */
	private final ArrayList<StaffMark> marks; // ledger lines are inserted here
												// as
												// well in order to deal with
												// layout
	private final ArrayList<Attachment<?>> attachments;
	
	private boolean ossia;

	/**
	 * Voices it contains. Note that when we have an 4 octaves scale that has 2 scales in one staff 
	 * and other 2 in other one, it is split in two layers. 
	 * For occasional staff changes of one note N, N is maintained in the voice and its value staffChange modified     
	 */
	private final ArrayList<ScoreLayer> layers;
	/**
	 *
	 * @param hierarchicalOrder
	 *            The ordering in the score. It is absolute, even though they
	 *            are inside a group
	 * @param numberIdentifier
	 * @param nlines
	 */
	public Staff(ScoreSong song, String hierarchicalOrder, int numberIdentifier,
			int nlines) {
		super(song, hierarchicalOrder, numberIdentifier);
		init(nlines);
		fermate = new TreeMap<>();
		this.marks = new ArrayList<>();
		this.attachments = new ArrayList<>();
		ossia = false;
		layers = new ArrayList<>();
		coreSymbols = new ArrayList<>();
		// mainLayer = this.addLayer();
	}

	private void init(int nlines) {
		this.lines = nlines;
		this.clefs = new TreeMap<>();
		this.timeSignatures = new TreeMap<>();
		this.keySignatures = new TreeMap<>();
	}

	// ----------------------------------------------------------------------
	// ----------------------- General --------------------------------
	// ----------------------------------------------------------------------
	public StaffGroup getParentSystem() {
		return parentSystem;
	}


	// ----------------------------------------------------------------------
	// ----------------------- Children: actually this one
	// --------------------------------
	// ----------------------------------------------------------------------
	@Override
	public Staff getTopStaff() {
		return this;
	}

	@Override
	public Staff getBottomStaff() {
		return this;
	}

	@Override
	public List<Staff> getContainedStaves() {
		ArrayList<Staff> result = new ArrayList<>();
		result.add(this);
		return result;
	}

	// ----------------------------------------------------------------------	
	// Layers
	// 
	// ----------------------------------------------------------------------
	
	// ----------------------------------------------------------------------
	// ----------------------- Fermate --------------------------------
	// ----------------------------------------------------------------------
	public TreeMap<Time, Fermate> getFermate() {
		return fermate;
	}

	public void addFermata(AtomFigure snr, PositionAboveBelow position) throws IM3Exception {
		Fermate f = fermate.get(snr.getTime());
		if (!fermate.containsKey(snr.getTime())) {
			f = new Fermate(getNotationType(), this, snr, position);
			fermate.put(snr.getTime(), f);
			this.addMark(f);
		} else {
			f.addDurationalSymbol(snr, position);
		}
	}

	// ----------------------------------------------------------------------
	// ----------------------- Ledger lines --------------------------------
	// ----------------------------------------------------------------------
	public int getLineCount() {
		return this.lines;
	}


	public int computeNumberLedgerLinesNeeded(PositionInStaff positionInStaff) {
		int lineSpace = positionInStaff.getLineSpace();
		if (lineSpace < 0) {
			return -lineSpace / 2;
		} else if (lineSpace > (lines - 1) * 2) {
			return -(lineSpace - (lines - 1) * 2) / 2;
		} else {
			return 0;
		}
	}

	// ----------------------------------------------------------------------
	// ----------------------- Background data: clefs, signatures ....
	// --------------------
	// ----------------------------------------------------------------------
	public Clef getRunningClefAt(ITimedElement symbol) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, Clef> c = this.clefs.floorEntry(symbol.getTime());
		if (c == null) {
			throw new IM3Exception(
					"There is no clef set at symbol " + symbol.toString() + " at time  " + symbol.getTime());
		}
		return c.getValue();
	}

	public Clef getRunningClefAt(Time time) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, Clef> c = this.clefs.floorEntry(time);
		if (c == null) {
			throw new IM3Exception("There is no clef set at time  " + time);
		}
		return c.getValue();
	}

	public Clef getRunningClefAtOrNull(Time time) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, Clef> c = this.clefs.floorEntry(time);
		if (c == null) {
		    return null;
        } else {
            return c.getValue();
        }
	}

	public Collection<Clef> getClefs() {
		return this.clefs.values();
	}

	public Collection<TimeSignature> getTimeSignatures() {
		return this.timeSignatures.values();
	}

	public Collection<KeySignature> getKeySignatures() {
		return this.keySignatures.values();
	}

	/**
	 * @param time
	 * @return null if none
	 */
	public Clef getClefAtTime(Time time) {
		return this.clefs.get(time);
	}

	/**
	 * @param time
	 * @return null if none
	 */
	public TimeSignature getTimeSignatureWithOnset(Time time) {
		return this.timeSignatures.get(time);
	}

	/**
	 * @param time
	 * @return null if none
	 */
	public KeySignature getKeySignatureWithOnset(Time time) {
		return this.keySignatures.get(time);
	}

	public TimeSignature getRunningTimeSignatureAt(ITimedElement symbol) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, TimeSignature> c = this.timeSignatures.floorEntry(symbol.getTime());
		if (c == null) {
			throw new IM3Exception(
					"There is no time signature set at symbol " + symbol.toString() + " at time " + symbol.getTime());
		}
		return c.getValue();
	}

	public KeySignature getRunningKeySignatureAt(ITimedElement symbol) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, KeySignature> c = this.keySignatures.floorEntry(symbol.getTime());
		if (c == null) {
			throw new IM3Exception(
					"There is no time signature set at symbol " + symbol.toString() + " at time " + symbol.getTime());
		}
		return c.getValue();
	}

	public TimeSignature getRunningTimeSignatureAt(Time time) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, TimeSignature> c = this.timeSignatures.floorEntry(time);
		if (c == null) {
			throw new IM3Exception("There is no time signature set at time " + time);
		}
		return c.getValue();
	}

	public KeySignature getRunningKeySignatureAt(Time time) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, KeySignature> c = this.keySignatures.floorEntry(time);
		if (c == null) {
			throw new IM3Exception("There is no instrumentKey signature set at time " + time);
		}
		return c.getValue();
	}

	public KeySignature getRunningKeySignatureOrNullAt(Time time) throws IM3Exception {
		// The treeset with all clefs ordered by onsets is built after
		// computeOnsets
		Map.Entry<Time, KeySignature> c = this.keySignatures.floorEntry(time);
		if (c == null) {
			return null;
		} else {
			return c.getValue();
		}
	}

	/**
	 * Invoked from StaffLayer when the symbol is added is a clef or directly
	 *
	 * @param clef
	 * @throws IM3Exception
	 */
	public void addClef(Clef clef) throws IM3Exception {
		Clef prev = clefs.get(clef.getTime());
		if (prev != null) {
			if (prev.equals(clef)) {
				throw new IM3Exception("Inserting twice the same clef " + clef + " at time " + prev.getTime());
			} else {
				this.coreSymbols.remove(prev);
			}
		}
		
		this.clefs.put(clef.getTime(), clef);
		this.coreSymbols.add(clef);
		clef.setStaff(this);
	}

	public void addTimeSignature(TimeSignature ts) throws IM3Exception {		
		TimeSignature prev = timeSignatures.get(ts.getTime());
		if (prev != null) {
			if (prev.equals(ts)) {
				throw new IM3Exception("Inserting twice the same time signature " + ts);
			} else {
				this.coreSymbols.remove(prev);
			}
		}
		this.timeSignatures.put(ts.getTime(), ts);
		this.coreSymbols.add(ts);
		ts.setStaff(this);
	}

	public void addKeySignature(KeySignature ts) throws IM3Exception {
		KeySignature prev = keySignatures.get(ts.getTime());
		if (prev != null) {
			if (prev.equals(ts)) {
				throw new IM3Exception("Inserting twice the same instrumentKey signature " + ts);
			} else {
				this.coreSymbols.remove(prev);
			}
		}
		this.keySignatures.put(ts.getTime(), ts);
		this.coreSymbols.add(ts);
		ts.setStaff(this);
	}

	// ----------------------------------------------------------------------
	// ----------------------- Notation symbols --------------------------------
	// ----------------------------------------------------------------------
	/**
	 *
	 * @param fromTime
	 *            inclusive
	 * @param toTime
	 *            exclusive
	 * @return
	 * @throws IM3Exception 
	 */
	public List<ITimedElementInStaff> getCoreSymbolsOrderedWithOnsets(Time fromTime, Time toTime) throws IM3Exception {
		if (!fromTime.isZero() || !toTime.isMaxValue()) { // to avoid doing the loop when all possible elements fit
            ArrayList<ITimedElementInStaff> symbols = new ArrayList<>();

            for (ITimedElementInStaff cs : coreSymbols) { //TODO Speed up?
                if (fromTime.compareTo(cs.getTime()) <= 0 && cs.getTime().compareTo(toTime) < 0) {
                    symbols.add(cs);
                }
            }
            SymbolsOrderer.sortList(symbols);
            return symbols;
        } else {
            return getCoreSymbolsOrdered();
        }
	}

	public List<ITimedElementInStaff> getCoreSymbolsOrdered() {
		ArrayList<ITimedElementInStaff> symbols = new ArrayList<>(coreSymbols);
		SymbolsOrderer.sortList(symbols);
		return symbols;
	}	
	public void addMark(StaffMark mark) throws IM3Exception {
		this.marks.add(mark);
	}

	public void addAttachment(Attachment<?> attachment) {
		this.attachments.add(attachment);
	}

	public ArrayList<StaffMark> getMarks() {
		return marks;
	}

	public ArrayList<Attachment<?>> getAttachments() {
		return attachments;
	}
	
	public void addCoreSymbol(ITimedElementInStaff e) throws IM3Exception {
		e.setStaff(this);
        this.coreSymbols.add(e);
    }

	/**
	 *
	 * @param clef
	 * @param noteName
	 * @param octave
	 * @return 0 = bottom line (i.e. E in G2 clef), 1 is F in F in G2 clef, -1
	 *         is D in G2 clef
	 * @throws IM3Exception
	 */
	public PositionInStaff computePositionInStaff(Clef clef, DiatonicPitch noteName, int octave) throws IM3Exception {
		DiatonicPitch bottomClefNoteName = clef.getBottomLineDiatonicPitch();
		int bottomClefOctave = clef.getBottomLineOctave();

		int noteOrder = noteName.getOrder() + octave * 7;
		int bottomLinePitchOrder = bottomClefNoteName.getOrder() + bottomClefOctave * 7;
		int result = noteOrder - bottomLinePitchOrder;
		return new PositionInStaff(result);
	}

    public PositionInStaff computePositionInStaff(Time time, DiatonicPitch noteName, int octave) throws IM3Exception {
        Clef clef = getRunningClefAt(time);
        return computePositionInStaff(clef, noteName, octave);

    }

    /**
     *
     * @param clef
     * @param positionInStaff
     * @return
     * @throws IM3Exception When no possible pitch is found
     */
    public ScientificPitch computeScientificPitch(Clef clef, PositionInStaff positionInStaff) throws IM3Exception {
        int bottomClefOctave = clef.getBottomLineOctave();
        DiatonicPitch bottomClefNoteName = clef.getBottomLineDiatonicPitch();
        int intervalWithC = bottomClefNoteName.getOrder() - DiatonicPitch.C.getOrder();
        int lineSpace = positionInStaff.getLineSpace();
        int octaveDifference = (lineSpace + intervalWithC) / 7;

        int diatonicPitchDifference = lineSpace % 7; // TODO: 5/10/17 ¿Si es negativo?

        DiatonicPitch[] noteNames = DiatonicPitch.getJustNoteNames();
        DiatonicPitch diatonicPitch = noteNames[(diatonicPitchDifference + bottomClefNoteName.getOrder())%7];
        int octave = (octaveDifference + bottomClefOctave);
        ScientificPitch result = new ScientificPitch(diatonicPitch, null, octave);
        return result;
    }

	public abstract boolean isPitched();

	public final boolean isOssia() {
		return ossia;
	}

	public final void setOssia(boolean ossia) {
		this.ossia = ossia;
	}

	public void addLayer(ScoreLayer layer) {
		if (!this.layers.contains(layer)) {
			this.layers.add(layer);
			layer.setStaff(this);
		}
	}

	public void addLayer(int index, ScoreLayer layer) {
		if (layer == null) {
			throw new IM3RuntimeException("Cannot insert a null layer");
		}
		if (!this.layers.contains(layer)) {
			this.layers.add(index, layer);
			layer.setStaff(this);
		}
	}

	public final List<ScoreLayer> getLayers() {
		return layers;
	}
	
	public List<AtomPitch> getAtomPitches() throws IM3Exception {
		ArrayList<AtomPitch> result = new ArrayList<>();
		
		for (ITimedElementInStaff symbol: coreSymbols) {
			if (symbol instanceof Atom) {
				Atom atom = (Atom) symbol;
				List<AtomPitch> aps = atom.getAtomPitches();
				if (aps != null) {
					for (AtomPitch ap: aps) {
						if (ap.getStaff() == this) {
							result.add(ap);
						}
					}
				}
			} else if (symbol instanceof AtomPitch) {
				result.add((AtomPitch) symbol);
			}
		}
		return result;
	}

	public List<Atom> getAtoms() throws IM3Exception {
		ArrayList<Atom> result = new ArrayList<>();
		
		for (ITimedElementInStaff symbol: coreSymbols) {
			if (symbol instanceof Atom) {
				result.add((Atom) symbol);
			}
		}
		return result;
	}
	
	public List<AtomFigure> getAtomFiguresWithOnsetWithin(Measure bar) throws IM3Exception {
		ArrayList<AtomFigure> result = new ArrayList<>();
		Time from = bar.getTime();
		Time to = bar.getEndTime();
		for (ScoreLayer layer: layers) {
			result.addAll(layer.getAtomFiguresWithOnsetWithin(from, to));
		}
		return result;
	}

	public List<Atom> getAtomsWithOnsetWithin(Measure bar) throws IM3Exception {
		ArrayList<Atom> result = new ArrayList<>();
		Time from = bar.getTime();
		Time to = bar.getEndTime();
		List<Atom> atoms = getAtoms();
		for (Atom atom: atoms) {
			if (atom.getTime().isContainedIn(from, to)) {
				result.add(atom);
			} 
		}
		return result;
	}


    public void remove(ITimedElementInStaff element) {
        if (coreSymbols.remove(element)) { // if not removed yet
            element.setStaff(null);
        }
    }

	/**
	 * It tells the accidental that must be drawn for each note in the given range
	 * @return A map with each note and the accidental to be drawn
	 * @throws IM3Exception
	 */
	public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow() throws IM3Exception {
	    return createNoteAccidentalsToShow(Time.TIME_ZERO, Time.TIME_MAX);
	}

    /**
     * It tells the accidental that must be drawn for each note in the given range
     * @return A map with each note and the accidental to be drawn
     * @throws IM3Exception
     */
	public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow(Time fromTime, Time toTime) throws IM3Exception {
		TreeMap<DiatonicPitch, ScientificPitch> alteredDiatonicPitchInBar = new TreeMap<>();
		TreeMap<DiatonicPitch, PitchClass> alteredDiatonicPitchInKeySignature = new TreeMap<>();
        HashMap<AtomPitch, Accidentals> result = new HashMap<>();
		KeySignature currentKeySignature = null; // getRunningKeySignatureAt(fromTime);
        List<ITimedElementInStaff> symbols = this.getCoreSymbolsOrderedWithOnsets(fromTime, toTime);
        Measure lastMeasure = null;
        for (ITimedElementInStaff symbol: symbols) {
            Measure measure = null;
            if (getScoreSong().hasMeasures()) {
                measure = getScoreSong().getMeasureActiveAtTime(symbol.getTime());
            }
            if (lastMeasure != measure) {
                alteredDiatonicPitchInBar.clear();
                lastMeasure = measure;
            }

            if (symbol instanceof KeySignature) {
                alteredDiatonicPitchInKeySignature = ((KeySignature)symbol).getAlteredDiatonicPitchSet();
            } else if (symbol instanceof SingleFigureAtom) {
                SingleFigureAtom singleFigureAtom = (SingleFigureAtom) symbol;
                List<AtomPitch> atomPitches = singleFigureAtom.getAtomPitches();
                if (atomPitches != null) {
                    for (AtomPitch atomPitch : atomPitches) {
                        computeRequiredAccidentalsForPitch(alteredDiatonicPitchInBar, alteredDiatonicPitchInKeySignature,
                                result, atomPitch);
                    }
                }
            }
        }
        return result;
	}

    void computeRequiredAccidentalsForPitch(TreeMap<DiatonicPitch, ScientificPitch> alteredNoteNamesInBar,
											TreeMap<DiatonicPitch, PitchClass> alteredNoteNamesInKeySignature,
                                            HashMap<AtomPitch, Accidentals> result, AtomPitch ps) throws IM3Exception {
		ScientificPitch pc = ps.getScientificPitch();
		if (!alteredNoteNamesInBar.containsValue(pc)) { // if not previously altered
			Accidentals requiredAccidental = computeRequiredAccidental(alteredNoteNamesInKeySignature,
					pc.getPitchClass());

            result.put(ps, requiredAccidental);
		}
	}

	private Accidentals computeRequiredAccidental(TreeMap<DiatonicPitch, PitchClass> alteredSet, PitchClass pc) {
		// needs accidental?
		Accidentals requiredAccidental = null;
		PitchClass pcInKey = alteredSet.get(pc.getNoteName());
		if (pcInKey != null) { // altered note name in key signature
			if (!pc.equals(pcInKey)) { // alteration not valid for this pitch
				// class
				if (pc.getAccidental() == null || pc.getAccidental() == Accidentals.NATURAL) {
					requiredAccidental = Accidentals.NATURAL;
				} else {
					requiredAccidental = pc.getAccidental(); // either flat or
					// sharp
				}
			}
		} else if (pc.getAccidental() != null && pc.getAccidental() != Accidentals.NATURAL) {
			requiredAccidental = pc.getAccidental(); // either flat or sharp
		}
		return requiredAccidental;
	}

    /**
     * It returns the y position for a given diatonic pitch at a given time (for taking into account the clef changes)
     * without taking into account the octave change. Used usually for key signatures
     * @param time
     * @param noteName
     * @param octave
     * @return
     * @throws IM3Exception
     */
    public PositionInStaff computePositionForPitchWithoutClefOctaveChange(Time time, DiatonicPitch noteName, int octave) throws IM3Exception {
        Clef clef = getRunningClefAt(time);
        return computePositionInStaff(clef, noteName, octave + clef.getOctaveChange());
    }

    /**
     * The last inserted clef
     * @return Could return null
     */
    public Clef getLastClef() {
        if (clefs == null || clefs.isEmpty()) {
            return null;
        }
        return clefs.lastEntry().getValue();
    }

    public TimeSignature getLastTimeSignature() {
        if (timeSignatures == null || timeSignatures.isEmpty()) {
            return null;
        }
        return timeSignatures.lastEntry().getValue();
    }

    public void clear() {
        this.timeSignatures.clear();
        this.coreSymbols.clear();
        this.keySignatures.clear();
        this.clefs.clear();
    }
}
