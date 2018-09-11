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
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.utils.CollectionsUtils;

/**
 * It is just a visual holder for elements. The note, chord and rest sequences are stored in ScoreLayer,
 * the meters in song. The instrumentKey signatures, for being able to represent transposing instruments are stored
 * here
 *
 * @author drizo
 */
public abstract class Staff extends VerticalScoreDivision implements ISymbolWithConnectors {
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
     * Used to be able to quick locate custos
     */
    TreeMap<Time, Custos> custos;
	/**
	 * This is the only place there the instrumentKey signatures are stored
	 */
	TreeMap<Time, KeySignature> keySignatures;
	/**
	 * There cannot be a common meter for all the staves in the song because there may be
	 * different staves with different time signatures (e.g. Douce/Garison, Philippe de Vitry) 
	 */
	TreeMap<Time, TimeSignature> timeSignatures;
    TreeMap<Time, MarkBarline> markBarlines;
	
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
	private final ArrayList<AttachmentInStaff<?>> attachments;
	
	private boolean ossia;

	/**
	 * Voices it contains. Note that when we have an 4 octaves scale that has 2 scales in one staff 
	 * and other 2 in other one, it is split in two layers. 
	 * For occasional staff changes of one note N, N is maintained in the voice and its value staffChange modified     
	 */
	private final ArrayList<ScoreLayer> layers;

	protected ConnectorCollection connectorCollection;

    // TODO: 17/11/17 A system
    /**
     * Explicit system breaks
     */
    HashMap<Time, SystemBreak> systemBreaks;
    // TODO: 17/11/17 A system
    /**
     * Explicit system breaks
     */
    HashMap<Time, PageBreak> pageBreaks;

    /**
     * Parts represented in this staff, usually it will be just 1 part
     */
    List<ScorePart> parts;
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
        parts = new LinkedList<>();
		fermate = new TreeMap<>();
		this.marks = new ArrayList<>();
		this.attachments = new ArrayList<>();
		ossia = false;
		layers = new ArrayList<>();
		coreSymbols = new ArrayList<>();
        connectorCollection = new ConnectorCollection();
		// mainLayer = this.addLayer();
	}

	private void init(int nlines) {
		this.lines = nlines;
		this.clefs = new TreeMap<>();
		this.timeSignatures = new TreeMap<>();
		this.keySignatures = new TreeMap<>();
		this.markBarlines = new TreeMap<>();
        this.systemBreaks = new HashMap<>();
        this.pageBreaks = new HashMap<>();
        this.custos = new TreeMap<>();
    }

    public void addPart(ScorePart part) {
	    if (!parts.contains(part)) {
            parts.add(part);
        }
    }

    public List<ScorePart> getParts() {
        return parts;
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

    // TODO: 7/4/18 ¿Esta suposición de poner el calderón según la clave está bien?
    /**
     * It adds the fermata depending on the clef
     * @param snr
     * @throws IM3Exception
     */
    public void addFermata(AtomFigure snr) throws IM3Exception {
        Clef clef = null;
        try {
            clef = this.getRunningClefAt(snr); // harmony spines...
        } catch (IM3Exception e) {
            //no-op
        }
        if (clef == null || clef.getNoteOctave() >= 4) {
            addFermata(snr, PositionAboveBelow.ABOVE);
        } else {
            addFermata(snr, PositionAboveBelow.BELOW);
        }
    }

    /**
     * Add a specific fermata
     * @param snr
     * @param position
     * @throws IM3Exception
     */
	public void addFermata(AtomFigure snr, PositionAboveBelow position) throws IM3Exception {
	    if (position == PositionAboveBelow.UNDEFINED) {
	        addFermata(snr);
        }

		Fermate f = fermate.get(snr.getTime());
		if (f == null) {
			f = new Fermate(getNotationType(), this, snr, position);
			fermate.put(snr.getTime(), f);
			this.addMark(f);
		}
		f.addDurationalSymbol(snr, position);
	}

	// ----------------------------------------------------------------------
	// ----------------------- Ledger lines --------------------------------
	// ----------------------------------------------------------------------
	public int getLineCount() {
		return this.lines;
	}

	public static int computeNumberLedgerLinesNeeded(PositionInStaff positionInStaff, int lines) {
        int lineSpace = positionInStaff.getLineSpace();
        if (lineSpace < 0) {
            return -lineSpace / 2;
        } else if (lineSpace > (lines - 1) * 2) {
            return -(lineSpace - (lines - 1) * 2) / 2;
        } else {
            return 0;
        }
    }

	public int computeNumberLedgerLinesNeeded(PositionInStaff positionInStaff) {
	    return computeNumberLedgerLinesNeeded(positionInStaff, lines);
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
					"There is no clef set at symbol " + symbol.toString() + " at time  " + symbol.getTime() + " in staff " + this);
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

	public Clef getRunningClefAtOrNull(Time time) {
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

    public Collection<Custos> getCustos() {
        return this.custos.values();
    }

    public void addCustos(Custos custos) {
	    this.custos.put(custos.getTime(), custos);
        this.coreSymbols.add(custos);
        custos.setStaff(this);
    }

	public Collection<TimeSignature> getTimeSignatures() {
		return this.timeSignatures.values();
	}

	public Collection<KeySignature> getKeySignatures() {
		return this.keySignatures.values();
	}

    public Collection<MarkBarline> getMarkBarLines() {
        return this.markBarlines.values();
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
    public MarkBarline getMarkBarLineWithOnset(Time time) {
        return this.markBarlines.get(time);
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
			throw new IM3Exception("There is no instrumentKey signature set at time " + time + " in staff " + toString());
		}
		return c.getValue();
	}

	public KeySignature getRunningKeySignatureOrNullAt(Time time) {
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
        this.addCoreSymbol(clef);
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
		this.addCoreSymbol(ts);
		//this.coreSymbols.add(ts);
		//ts.setStaff(this);
	}

    /*public void removeTimeSignature(TimeSignature meter) {
	    this.coreSymbols.remove(meter);
	    this.timeSignatures.remove(meter.getTime());
    }*/


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
        this.addCoreSymbol(ts);
	}

    public void addMarkBarline(MarkBarline ts) throws IM3Exception {
        MarkBarline prev = markBarlines.get(ts.getTime());
        if (prev != null) {
            if (prev.equals(ts)) {
                throw new IM3Exception("Inserting twice the same mark bar line " + ts);
            } else {
                this.coreSymbols.remove(prev);
            }
        }
        this.markBarlines.put(ts.getTime(), ts);
        this.addCoreSymbol(ts);
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
	public List<ITimedElementInStaff> getCoreSymbolsOrderedWithOnsets(Time fromTime, Time toTime) {
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

    public List<ITimedElementInStaff> getCoreSymbols() {
	    return coreSymbols;
    }

	public void addMark(StaffMark mark) {
		this.marks.add(mark);
	}

	public void addAttachment(AttachmentInStaff<?> attachment) {
	    this.attachments.add(attachment);
	    this.coreSymbols.add(attachment);
	}

	public ArrayList<StaffMark> getMarks() {
		return marks;
	}

    public ArrayList<StaffMark> getMarksOrderedByTime() {
	    ArrayList<StaffMark> ordered = new ArrayList<>(marks);
	    ordered.sort(new Comparator<StaffMark>() {
            @Override
            public int compare(StaffMark o1, StaffMark o2) {
                int diff = o1.getTime().substract(o2.getTime()).intValue();
                if (diff == 0) {
                    diff = o1.hashCode() - o2.hashCode();
                }
                return diff;
            }
        });
        return ordered;
    }

	public ArrayList<AttachmentInStaff<?>> getAttachments() {
		return attachments;
	}
	
	public void addCoreSymbol(ITimedElementInStaff e) {
		e.setStaff(this);
        this.coreSymbols.add(e);
        /*System.out.println(name + " ADDED " + e);
        for (ITimedElementInStaff ee: coreSymbols) {
            System.out.println("\t" + ee);
        }*/
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
	public PositionInStaff computePositionInStaff(Clef clef, DiatonicPitch noteName, int octave) {
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
    public ScientificPitch computeScientificPitch(Clef clef, PositionInStaff positionInStaff) {
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
	
	public List<AtomPitch> getAtomPitches() {
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

    // TODO: 15/3/18 Test unitario
    /**
     * @param from
     * @param to Excluded
     * @return
     * @throws IM3Exception
     */
    public List<AtomPitch> getAtomPitchesWithOnsetWithin(Time from, Time to) {
        ArrayList<AtomPitch> result = new ArrayList<>();

        for (ITimedElementInStaff symbol: coreSymbols) {
            if (symbol instanceof Atom) {
                Atom atom = (Atom) symbol;
                List<AtomPitch> aps = atom.getAtomPitches();
                if (aps != null) {
                    for (AtomPitch ap: aps) {
                        if (ap.getStaff() == this && ap.getTime().compareTo(from) >= 0 && ap.getTime().compareTo(to) < 0) {
                            result.add(ap);
                        }
                    }
                }
            } else if (symbol instanceof AtomPitch &&  symbol.getTime().compareTo(from) >= 0 && symbol.getTime().compareTo(to) < 0) {
                result.add((AtomPitch) symbol);
            }
        }
        return result;
    }


	public List<Atom> getAtoms() {
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
        //TODO Algo más elegante
        if (element instanceof Custos) {
            custos.remove(element.getTime());
        }

        if (element instanceof KeySignature) {
            keySignatures.remove(element.getTime());
        }

        if (element instanceof TimeSignature) {
            timeSignatures.remove(element.getTime());
        }

        if (element instanceof MarkBarline) {
            markBarlines.remove(element.getTime());
        }

        if (element instanceof Fermate) {
            markBarlines.remove(element.getTime());
        }
    }

	/**
	 * It tells the accidental that must be drawn for each note in the given range
	 * @return A map with each note and the accidental to be drawn
	 * @throws IM3Exception
	 */
    /*//20180208 public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow() throws IM3Exception {
	    return createNoteAccidentalsToShow(Time.TIME_ZERO, Time.TIME_MAX);
	}*/

    /**
     * It tells the accidental that must be drawn for each note in the given range
     * @return A map with each note and the accidental to be drawn
     * @throws IM3Exception
     */
	//20180208 This cannot be computed fromTime - toTime to take into account previous context - public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow(Time fromTime, Time toTime) throws IM3Exception {
    public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow() throws IM3Exception {
		TreeMap<DiatonicPitchAndOctave, PitchClass> alteredDiatonicPitchInBar = new TreeMap<>();
		TreeMap<DiatonicPitch, PitchClass> alteredDiatonicPitchInKeySignature = new TreeMap<>();
        HashMap<AtomPitch, Accidentals> result = new HashMap<>();
		KeySignature currentKeySignature = null; // getRunningKeySignatureAt(fromTime);
        //20180208 List<ITimedElementInStaff> symbols = this.getCoreSymbolsOrderedWithOnsets(fromTime, toTime);
        List<ITimedElementInStaff> symbols = this.getCoreSymbolsOrdered();
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

            if (symbol instanceof MarkBarline) {
                alteredDiatonicPitchInBar.clear();
            } else  if (symbol instanceof KeySignature) {
                alteredDiatonicPitchInKeySignature = ((KeySignature)symbol).getAlteredDiatonicPitchSet();
            } else if (symbol instanceof SingleFigureAtom) {
                SingleFigureAtom singleFigureAtom = (SingleFigureAtom) symbol;
                List<AtomPitch> atomPitches = singleFigureAtom.getAtomPitches();
                if (atomPitches != null) {
                    for (AtomPitch atomPitch : atomPitches) {
                        computeRequiredAccidentalsForPitch(alteredDiatonicPitchInBar, alteredDiatonicPitchInKeySignature,
                                result, atomPitch);
                        if (atomPitch.getWrittenExplicitAccidental() != null) {
                            result.put(atomPitch, atomPitch.getWrittenExplicitAccidental());
                        }
                    }
                }
            }
        }
        return result;
	}

    void computeRequiredAccidentalsForPitch(TreeMap<DiatonicPitchAndOctave, PitchClass> alteredNoteNamesInBar,
											TreeMap<DiatonicPitch, PitchClass> alteredNoteNamesInKeySignature,
                                            HashMap<AtomPitch, Accidentals> result, AtomPitch ps) {

	    ScientificPitch pc = ps.getScientificPitch();
        DiatonicPitchAndOctave diatonicPitchAndOctave = new DiatonicPitchAndOctave(pc.getPitchClass().getNoteName(), pc.getOctave());

	    PitchClass alteredNoteInBar = alteredNoteNamesInBar.get(diatonicPitchAndOctave);
	    if (alteredNoteInBar == null) {
	        // check key signature
            Accidentals requiredAccidental = computeRequiredAccidental(alteredNoteNamesInKeySignature,
                    pc.getPitchClass());

            if (requiredAccidental != null) {
                result.put(ps, requiredAccidental);
                alteredNoteNamesInBar.put(diatonicPitchAndOctave, pc.getPitchClass());
            }
        } else {
            if (!alteredNoteInBar.equals(pc.getPitchClass())) {
                // TODO: 5/12/17 Mensural # is a natural of previous b
                Accidentals realAccidental = pc.getPitchClass().getAccidental(); // the one that will sound, not the represented
                Accidentals writtenAccidental;
                if (realAccidental == null || realAccidental == Accidentals.NATURAL) {
                    writtenAccidental = Accidentals.NATURAL;
                } else {
                    writtenAccidental = realAccidental;
                }
                result.put(ps, writtenAccidental);
                alteredNoteNamesInBar.put(diatonicPitchAndOctave, pc.getPitchClass()); // if will change the previous value
            }
        }
	}

    /**
     *
     * @param alteredSet
     * @param pc
     * @return null if no explicit accidental is required
     */
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

    //20180208 This cannot be computed fromTime - toTime to take into account previous context - public HashMap<AtomPitch, Accidentals> createNoteAccidentalsToShow(Time fromTime, Time toTime) throws IM3Exception {
    public Accidentals findCurrentKeySignatureAccidental(Time time, DiatonicPitch diatonicPitch) {
	    try {
	        KeySignature keySignature = getRunningKeySignatureAt(time);
            return keySignature.getAccidentalOf(diatonicPitch);
        } catch (IM3Exception e) {
	        return null; // no key signature
        }

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


    @Override
    public Collection<Connector> getConnectors() {
        if (connectorCollection == null) {
            return null;
        } else {
            return connectorCollection.getConnectors();
        }
    }

    @Override
    public void addConnector(Connector connector) {
        if (connectorCollection == null) {
            connectorCollection = new ConnectorCollection();
        }
        connectorCollection.add(connector);
    }

    @Override
    public boolean containsConnectorFrom(ISymbolWithConnectors fromSymbol) {
        if (connectorCollection == null) {
            return false;
        } else {
            return connectorCollection.containsConnectorFrom(fromSymbol);
        }

    }

    @Override
    public boolean containsConnectorTo(ISymbolWithConnectors fromSymbol) {
        if (connectorCollection == null) {
            return false;
        } else {
            return connectorCollection.containsConnectorTo(fromSymbol);
        }
    }

    public void clear() {
        this.timeSignatures.clear();
        this.coreSymbols.clear();
        this.keySignatures.clear();
        this.clefs.clear();
        this.marks.clear();
        this.connectorCollection.clear();
    }

    /**
     *
     * @param time
     * @return null if not exists
     */
    public Fermate getFermateWithOnset(Time time) {
        return fermate.get(time);
    }


    public void addSystemBreak(SystemBreak sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("System break has not time set");
        }
        systemBreaks.put(sb.getTime(), sb);
        sb.setStaff(this);
        this.coreSymbols.add(sb);
    }

    public HashMap<Time, SystemBreak> getSystemBreaks() {
        return systemBreaks;
    }

    public boolean hasSystemBreak(Time time) {
        return systemBreaks.containsKey(time);
    }

    public void addPageBreak(PageBreak sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("Page break has not time set");
        }
        pageBreaks.put(sb.getTime(), sb);
    }

    public HashMap<Time, PageBreak> getPageBreaks() {
        return pageBreaks;
    }

    public boolean hasPageBreak(Time time) {
        return pageBreaks.containsKey(time);
    }

    public Atom getAtomWithOnset(Time time) {
        for (ITimedElementInStaff symbol: coreSymbols) {
            if (symbol instanceof Atom && symbol.getTime().equals(time)) {
                return (Atom) symbol;
            }
        }
        return null;
    }

    /**
     * It changes the old clef with the new one
     * @param oldClef
     * @param newClef
     * @param changePitches If true, notes are true note pitches are changed according to the change, e-g-  
     *                      repositioned to reflect the clef change,
     *
     * @throws IM3Exception
     */
    public void replaceClef(Clef oldClef, Clef newClef, boolean changePitches) throws IM3Exception {
        newClef.setTime(oldClef.getTime());
        newClef.setStaff(oldClef.getStaff());

        CollectionsUtils.replace(coreSymbols, oldClef, newClef);

        if (!clefs.replace(oldClef.getTime(), oldClef, newClef)) {
            throw new IM3Exception("Cannot replace the clef " + oldClef + " for " + newClef + " at time " + oldClef.getTime());
        }

        if (changePitches) {
            Time nextClefTime = clefs.higherKey(oldClef.getTime());

            // TODO: 15/3/18 ¿Se debe incluir el octave change?
            ScientificPitch fromPitch = new ScientificPitch(new PitchClass(oldClef.getBottomLineDiatonicPitch()), oldClef.getNoteOctave());
            ScientificPitch toPitch = new ScientificPitch(new PitchClass(newClef.getBottomLineDiatonicPitch()), newClef.getNoteOctave());
            Interval interval = Interval.compute(fromPitch, toPitch);

            if (nextClefTime == null) {
                nextClefTime = Time.TIME_MAX;
                Logger.getLogger(Staff.class.getName()).log(Level.INFO, "Changing pitches from time " + oldClef.getTime() + " till the end the interval " + interval);
            } else {
                Logger.getLogger(Staff.class.getName()).log(Level.INFO, "Changing pitches from time " + oldClef.getTime() + " to " + nextClefTime + " the interval " + interval);
            }

            List<AtomPitch> pitches = this.getAtomPitchesWithOnsetWithin(oldClef.getTime(), nextClefTime);
            for (AtomPitch pitch: this.getAtomPitches()) {
                pitch.transpose(interval);
            }
        }
    }

    @Override
    public String toString() {
        if (name != null) {
            return "#" + getNumberIdentifier() + " (" + name + ")";
        } else {
            return "#" + getNumberIdentifier();
        }
    }

    public void moveFermate(Time oldTime, Time newTime) {
        Fermate oldFermate = fermate.remove(oldTime);
        if (oldFermate == null) {
            throw new IM3RuntimeException("Cannot find a previous fermate at time " + oldTime);
        }
        this.fermate.put(newTime, oldFermate);
    }

}
