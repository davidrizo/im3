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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

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
		ArrayList<ITimedElementInStaff> symbols = new ArrayList<>();
		for (ITimedElementInStaff cs: coreSymbols) { //TODO Speed up? 
			if (fromTime.compareTo(cs.getTime()) >= 0 && cs.getTime().compareTo(toTime) < 0) {
				symbols.add(cs);
			}
		}
		SymbolsOrderer.sortList(symbols);
		return symbols;
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

	// ----------------------------------------------------------------------
	// ----------------------- Accidental processing-------------------------
	// ----------------------------------------------------------------------
	/*FRACCIONES private Accidentals computeRequiredAccidental(TreeMap<DiatonicPitch, PitchClass> alteredSet, PitchClass pc) {
		// needs accidental?
		Accidentals requiredAccidental = null;
		PitchClass pcInKey = alteredSet.get(pc.getNoteName());
		if (pcInKey != null) { // altered note name in instrumentKey signature
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
	}*/

	/**
	 *
	 * Browse from left to right and set the required accidentals for each note
	 * depending on the current instrumentKey signature and previous accidentals
	 *
	 * param fromTime
	 *            inclusive
	 * param toTime
	 *            exclusive
	 * @return
	 */
	/*FRACCIONES public void createNoteAccidentals(Time fromTime, Time toTime) throws IM3Exception {
		List<ISymbolInLayer> symbols = getNotationSymbolsOrdered(fromTime, toTime);
		TreeMap<DiatonicPitch, ScientificPitch> alteredNoteNamesInBar = new TreeMap<>();
		TreeMap<DiatonicPitch, PitchClass> alteredNoteNamesInKeySignature = new TreeMap<>();
		KeySignature currentKeySignature = null; // getRunningKeySignatureAt(fromTime);

		// alteredNoteNamesInKeySignature =
		// currentKeySignature.getScoreElement().getAlteredNoteNamesSet();

		for (ISymbolInLayer symbol : symbols) {
			if (symbol instanceof Barline) {
				alteredNoteNamesInBar.clear();
			} else if (symbol instanceof KeySignature) {
				currentKeySignature = (KeySignature) symbol;
				alteredNoteNamesInKeySignature = currentKeySignature.getScoreElement().getAlteredNoteNamesSet();
			} else if (symbol instanceof ScoreNote) {
				NotePitchSymbolElement ps = ((ScoreNote) symbol).getPitchSymbol();
				computeRequiredAccidentalsForPitch(alteredNoteNamesInBar, alteredNoteNamesInKeySignature, ps);
			} else if (symbol instanceof ScoreChord) {
				TreeSet<NotePitchSymbolElement> spitches = ((ScoreChord) symbol).getScorePitchElements();
				for (NotePitchSymbolElement ps : spitches) {
					computeRequiredAccidentalsForPitch(alteredNoteNamesInBar, alteredNoteNamesInKeySignature, ps);
				}
			}
		}

	}

	void computeRequiredAccidentalsForPitch(TreeMap<DiatonicPitch, ScientificPitch> alteredNoteNamesInBar,
			TreeMap<DiatonicPitch, PitchClass> alteredNoteNamesInKeySignature, NotePitchSymbolElement ps)
			throws NotationException {
		ScientificPitch pc = ps.getScorePitch().getPitch();
		if (!alteredNoteNamesInBar.containsValue(pc)) { // if not previously
														// altered
			Accidentals requiredAccidental = computeRequiredAccidental(alteredNoteNamesInKeySignature,
					pc.getPitchClass());
			if (requiredAccidental != null) {
				if (ps.getAccidentalSymbol() != null
						&& !ps.getAccidentalSymbol().getAccidental().equals(requiredAccidental)) {
					ps.removeSymbolElement(ps.getAccidentalSymbol());
				}
				if (ps.getAccidentalSymbol() == null) {
					alteredNoteNamesInBar.put(pc.getPitchClass().getNoteName(), pc);
					ps.setAccidental(requiredAccidental);
				} // else it is already the one we need
			}
		}
	}*/

	//TODO Speed up
	/*FRACCIONES public Time getDuration() throws IM3Exception {
		Time maxDur = Time.TIME_ZERO;
		for (StaffLayer layer : layers.values()) {
			maxDur = Time.max(maxDur, layer.getDuration());
		}
		return maxDur;
	}*/

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



	
	/*public Collection<? extends ISymbolInLayer> getSymbolsOrderdInTime() {
		ArrayList<ISymbolInLayer> result = new ArrayList<>();
		for (StaffLayer layer : layers.values()) {
			result.addAll(layer.getSymbols());
		}
		Collections.sort(result, new Comparator<ISymbolInLayer>() {

			@Override
			public int compare(ISymbolInLayer o1, ISymbolInLayer o2) {
				int diff;
				try {
					diff = o1.getTime().compareTo(o2.getTime());
				} catch (IM3Exception e) {
					throw new IM3RuntimeException("Cannot compare elements without time: "  + e);
				}
				if (diff == 0) {
					diff = o1.hashCode() - o2.hashCode();
				}
				return diff;
			}
			
		});
		return result;
	}*/

	// ----------------------------------------------------------------------
	// ----------------------- Meter information ------------------------------
	// ----------------------------------------------------------------------
	/*public void replace(Meter oldValue, Meter newValue) throws IM3Exception {
		if (newValue == null) {
			this.meters.remove(newValue);
		} else if (oldValue == null) {
			this.meters.addValue(newValue);
			newValue.setSong(this);
		} else {
			newValue.setSong(this);
			throw new UnsupportedOperationException("Not supported yet"); // TODO
																			// Es
																			// posible
																			// que
																			// haya
																			// que
																			// cambiar
																			// figuras

		}
	}

	public void clearMeters() {
		this.meters.clear();
	}

	public boolean hasMeter() {
		return !this.meters.isEmpty();
	}
	public Meter getFirstMeter() throws NoMeterException {
		if (this.meters.isEmpty()) {
			throw new NoMeterException();
		}
		return this.meters.getFirstElement();
	}*/

	/**
	 * It returns the meter active at a given time
	 *
	 * @param time
	 * @return
	 */
	/*public Meter getActiveMeterAtTime(Time time) {
		try {
			return meters.getValueAtTime(time);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException("No meter at time " + time, ex);
		}
	}*/

	/**
	 *
	 * @param time
	 * @return The instrumentKey or null of not exists
	 */
	/*public Meter getActiveMeterAtTimeOrNull(Time time) {
		return meters.getValueAtTimeOrNull(time);
	}

	public Meter getActiveMeterAtBar(Measure bar) {
		try {
			return meters.getValueAtTime(bar.getTime());
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException("No meter at bar " + bar, ex);
		}
	}*/

	/**
	 * It adds the meter and updates automatically adds time signatures to the
	 * present staves as well as barlines
	 * 
	 * @param time
	 * @param meter
	 * @return The new added time signatures
	 * @throws IM3Exception
	 */
	/*public List<TimeSignature> addMeter(Time time, Meter meter) throws IM3Exception {
		meter.setTime(time);
		meter.setSong(this);
		this.meters.addValue(meter);
		idManager.assignNextID(meter);

		// now the meter is added to all staves
		ArrayList<TimeSignature> result = new ArrayList<>();
		for (Staff staff : staves) {
			if (staff.getTimeSignatureAtTime(meter.getTime()) == null) { // it not existed
				TimeSignature ts = meter.addNotation(staff.getNotationType(), staff);
				if (ts != null) { // if not existed
					staff.addTimeSignature(ts);
					result.add(ts);
				}
			}
			
			// if the meter has changed, the barlines must be moved, we recompute them
			NavigableMap<Time, Barline> barlines = staff.getBarlinesAfter(time);
			for (Barline barline: barlines.values()) {
				Time expectedTime = barline.getBar().getEndTime();
				if (!expectedTime.equals(barline.getTime())) {
					barline.setTime(expectedTime);
				} else {
					break; // the rest of barlines are correctly set
				}
			}
		}
		
		return result;
	}*/
	

	// ----------------------------------------------------------------------
	// ----------------------- Key information ------------------------------
	// ----------------------------------------------------------------------
	/*FRACTIONS public void replace(Key oldValue, Key newValue) throws IM3Exception {
		if (newValue == null) {
			this.keys.remove(oldValue);
		} else if (oldValue == null) {
			idManager.assignNextID(newValue);
			this.keys.addValue(newValue);
			newValue.setSong(this);
		} else {
			idManager.assignNextID(newValue);
			newValue.setSong(this);
			this.keys.replace(oldValue, newValue);
		}
	}*/

	/*public boolean hasKey() {
		return !this.keys.isEmpty();
	}
	
	public Collection<Key> getKeys() {
		return this.keys.getValues();
	}

	public void clearKeys() {
		this.keys.clear();
	}

	public Key getFirsKey() throws NoKeyException {
		if (this.keys.isEmpty()) {
			throw new NoKeyException();
		}
		return this.keys.getFirstElement();
	}


	public void removeKey(Key instrumentKey) {
		try {
			this.keys.remove(instrumentKey);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(
					"The element has not time and it has been already set when it was inserted: " + ex.toString());
		}
	}

	public Key getActiveKeyAtBar(Measure bar) {
		try {
			return keys.getValueAtTime(bar.getTime());
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(ex);
		}
	}

	public Key getActiveKeyAtTime(Time time) {
		try {
			return keys.getValueAtTime(time);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(ex);
		}
	}*/

	/**
	 *
	 * @param time
	 * @return null if not found
	 */
	/*public Key getKeyWithOnsetOrNull(Time time) {
		return keys.getElementWithTime(time);
	}*/

	/**
	 *
	 * @param time
	 * @return The instrumentKey or null of not exists
	 */
	/*public Key getActiveKeyAtTimeOrNull(Time time) {
		return keys.getValueAtTimeOrNull(time);
	}*/

	/**
	 *
	 *
	 * /** It returns the instrumentKey active at a given time
	 *
	 * @param time
	 * @return
	 */
	/*public Key getKeyActiveAtTime(Time time) {
		try {
			return keys.getValueAtTime(time);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException("No instrumentKey active at time " + time, ex);
		}
	}*/

	/**
	 * It returns the instrumentKey active at a given time
	 *
	 * @param time
	 * @return
	 * @throws IM3Exception
	 */
	/*public Key getKeyActiveAtTimeOrNull(Time time) throws IM3Exception {
		return keys.getValueAtTimeOrNull(time);
	}
	
	public void moveKeys(Time offset) throws IM3Exception {
		ArrayList<Key> ks = new ArrayList<>(getKeys());
		clearKeys();
		for (Key h : ks) { // move the harmonies
			addKey(h.getTime().add(offset), h);
		}
	}*/


	/**
	 * It adds the instrumentKey and updates automatically adds instrumentKey signatures to the
	 * present staves
	 * 
	 * @param time
	 * @param instrumentKey
	 * @return The new added instrumentKey signatures
	 * @throws IM3Exception
	 */
	/*public List<KeySignature> addKey(Time time, Key instrumentKey) throws IM3Exception {
		Key previousKey = getKeyWithOnsetOrNull(time); 
		
		if (previousKey != null) {
			if (previousKey.getFifths() != instrumentKey.getFifths() || previousKey.getMode() != instrumentKey.getMode()) {
				throw new IM3Exception("Cannot insert two different keys (previous = " 
							+ previousKey + "), (new = " + instrumentKey + ")  at time " + time);
			}
			return null;
		} else {
			instrumentKey.setTime(time);
			instrumentKey.setSong(this);
			this.keys.addValue(instrumentKey);
			idManager.assignNextID(instrumentKey);
			// now the instrumentKey signature is added to all staves
			ArrayList<KeySignature> result = new ArrayList<>();
			for (Staff staff : staves) {
				if (staff.isPitched()) {
					KeySignature ts = instrumentKey.addNotation(staff.getNotationType(), staff);
					if (ts != null) { // if not existed
						staff.addKeySignature(ts);
						result.add(ts);
					}
				}
			}
			return result;
		}
	}*/
	
}
