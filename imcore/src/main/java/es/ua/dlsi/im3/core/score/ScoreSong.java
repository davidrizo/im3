package es.ua.dlsi.im3.core.score;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.TimedElementCollection;
import es.ua.dlsi.im3.core.metadata.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.staves.AnalysisStaff;

/**
 * A song is organized as a set of parts (instruments - e.g. piano, violin, soprano...).
 * Each part may contain several layers or melodic lines (not to be confused with texture
 * separation or visual staff layers). A layer may be the right hand of the piano, or the
 * violin 1 in a divisi.
 * 
 * A layer is usually allocated in a staff, but a note may be in other staff (cross-staff layers).
 * The same staff may contain different layers from several parts (typical SATB layout in two staves).
 * 
 * Do not confuse ScoreLayer with instrument. When we have an 4 octaves scale that has 2 scales in one staff 
 * and other 2 in other one, it is split in two layers. 
 * (Cross-staff layers): For occasional staff changes of one note N, N is maintained in the layer and its value staffChange modified     
 * 
 * Note: when an anacrusis is found, just the note or rest begins at an offset
 * time, keys, meters and clefs start at 0
 * 
 * The song does not store meters for allowing different simultaneous meters in staves (seen in early notations)
 *
 * @author drizo
 */
public class ScoreSong {
	protected IDManager idManager;

	/**
	 * Meter changes
	 */
	//private final TimedElementCollection<Meter> meters;
	/**
	 * Tempo changes
	 */
	private final TimedElementCollection<Tempo> tempoChanges;
	/**
	 * Key changes
	 */
	//private final TimedElementCollection<Key> keys;
	/**
	 * @deprecated
	 * Chord names
	 */
	private final TimedElementCollection<Harmony> harmonies;
	/**
	 * Measures
	 */
	private final TimedElementCollection<Measure> measures;	
	
	protected ArrayList<ScorePart> parts;
	private ScoreAnalysisPart analysisPart;
	Staff analysisStaff;
	Metadata metadata;

    /**
     * Explicit system breaks
     */
    HashMap<Time, SystemBreak> systemBreaks;
	List<Staff> staves;
	List<StaffGroup> staffGroups;
	/**
	 * Connectors are ordered in order to be drawn properly
	 */
	//TreeSet<Connector<?,?>> connectors;

	private Time anacrusisOffset;

    TimedElementCollection<Harm> harms;

	//FRACTIONS private final String PART_ID_PREFIX = "P";

	public ScoreSong() {
		//meters = new TimedElementCollection<>();
		//keys = new TimedElementCollection<>();
        harms = null;
        systemBreaks = new HashMap<>();
		harmonies = new TimedElementCollection<>();
		tempoChanges = new TimedElementCollection<>();
		measures = new TimedElementCollection<>();
		idManager = new IDManager(this);
		
		parts = new ArrayList<>();
		staves = new ArrayList<>();
		staffGroups = new ArrayList<StaffGroup>();
		/*connectors = new TreeSet<>(new Comparator<Connector<?,?>>() {

			@Override
			public int compare(Connector<?,?> o1, Connector<?,?> o2) {
				int diff = compareConnectorClasses(o1, o2);
				if (diff == 0) {
					diff = o1.toString().compareTo(o2.toString()); // any
																	// ordering
				}
				return diff;
			}

		});*/
	}

	/*private static int compareConnectorClasses(Connector<?,?> a, Connector<?,?> b) {
		return getConnectorClassOrder(a) - getConnectorClassOrder(b);
	}

	private static int getConnectorClassOrder(Connector<?,?> o) {
		//if (o instanceof BeamGroup) {
		//	return 0;
		//} else if (o instanceof Slur) {
			return 2;
		} else if (o instanceof Wedge) {
			return 2;
		} else {
			return 4;
		}
	}*/

	// ----------------------------------------------------------------------
	// ----------------------- General information
	// --------------------------------
	// ----------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScoreSong [" + getTitle() + ", parts=" + parts + "]";
	}

	public String getTitle() {
		if (metadata != null && metadata.getDescription() != null && metadata.getDescription().getHeader() != null) {
			// only that without "type"
			String result = null;
			for (Title t : metadata.getDescription().getHeader().getTitles()) {
				if (t.getType() == null || t.getType().isEmpty()) {
					if (result == null) {
						result = t.getValue();
					} else {
						result = result + ". " + t.getValue();
					}
				}
			}

			return result;
		} else {
			return "";
		}
	}

    public String getComposer() {
        if (metadata != null && metadata.getDescription() != null && metadata.getDescription().getHeader() != null) {
            return metadata.getDescription().getHeader().getPerson(PersonRoles.COMPOSER);
        } else {
            return "";
        }
    }

	public String getTitleAndSubtitles() {
		if (metadata != null && metadata.getDescription() != null && metadata.getDescription().getHeader() != null) {
			return metadata.getDescription().getHeader().getTitleContatenated(". ");
		} else {
			return "";
		}
	}
	
	public String getPerson(String role) {
		if (metadata != null && metadata.getDescription() != null && metadata.getDescription().getHeader() != null) {
			return metadata.getDescription().getHeader().getPerson(role);
		}
		return null;
	}
	

	public List<Person> getPersons() {
		if (metadata != null && metadata.getDescription() != null && metadata.getDescription().getHeader() != null) {
			return metadata.getDescription().getHeader().getPersons();
		}
		return null;
	}
	
	

	public Time getSongDuration() throws IM3Exception {
		Time dur = Time.TIME_ZERO;
		for (ScorePart part : parts) {
			dur = Time.max(dur, part.computeScoreDuration());
		}
		return dur;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parts == null) ? 0 : parts.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScoreSong other = (ScoreSong) obj;
		if (parts == null) {
			if (other.parts != null) {
				return false;
			}
		} else if (!parts.equals(other.parts)) {
			return false;
		}
		return true;
	}

	/**
	 * It does not contain notes in any part
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		for (ScorePart part : parts) {
			if (!part.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/*FRACTIONS private int getClassOrder(Object o) {
		if (o instanceof Measure) {
			return 0;
		} else if (o instanceof Key) {
			return 2;
		} else if (o instanceof Meter) {
			return 3;
		} else {
			return 4;
		}
	}*/

	// compare by time. As we have several classes in the same collection we
	// order them, the order of classes will be:
	// first clefs, next instrumentKey signature, then time signature, finally sounding
	// symbols
	/*FRACTIONS private int compareClasses(LayoutCoreSymbol o1, LayoutCoreSymbol o2) {
		int order1 = getClassOrder(o1);
		int order2 = getClassOrder(o2);

		if (order1 == order2) {
			return o1.getTime().compareTo(o2.getTime());
		} else {
			return order1 - order2;
		}
	}*/

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	// ----------------------------------------------------------------------
	// ----------------------- Content retrieval information
	// --------------------------------
	// ----------------------------------------------------------------------
	/*FRACTIONS public ArrayList<ScoreDurationalSymbol> getElementsWithRhythmSortedByTime() throws IM3Exception {
		ArrayList<ScoreDurationalSymbol> res = new ArrayList<>();
		for (ScorePart part : parts) {
			res.addAll(part.getDurationalSymbolsSortedByTime());
		}
		DurationalTimedElementSetCollection.sortByTime(res);
		return res;
	}*/

	public int getNumDurationalSymbols() throws IM3Exception {
		int n = 0;
		for (ScorePart part : parts) {
			n += part.size();
		}
		return n;
	}

	/*FRACCIONES public List<AtomFigure> getAtomFiguresWithOnsetWithin(Measure bar) throws IM3Exception {
		ArrayList<AtomFigure> result = new ArrayList<>();
		for (ScorePart part : parts) {
			result.addAll(part.getAtomFiguresWithOnsetWithin(bar));
		}
		return result;
	}*/

	
	/**
	 * It collects all score notes in all parts of the song that are sounding
	 * inside the given range
	 *
	 * @param fromTime
	 * @param toTime
	 *            (not included)???TODO
	 * @return Pointers to the notes contained in the different parts in the
	 *         specified range
	 */
	/*FRACTIONS  public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsActiveWithin(Time fromTime, Time toTime) {
		ArrayList result = new ArrayList<>();
		for (ScorePart part : parts) {
			ArrayList presult = part.getDurationalSymbolsActiveWithin(fromTime, toTime);
			result.addAll(presult);
		}
		return result;
	}

	public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsWithOnsetWithin(Measure bar) throws IM3Exception {
		Meter ts = getActiveMeterAtBar(bar);
		return ScoreSong.this.getDurationalSymbolsWithOnsetWithin(bar.getTime(),
				bar.getTime() + ts.getMeasureDurationAsTicks());
	}

	public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsWithOnsetWithin(Time fromTime, Time toTime)
			throws IM3Exception {
		ArrayList result = new ArrayList<>();
		for (ScorePart part : parts) {
			// ArrayList presult =
			// part.getDurationalSymbolsActiveWithin(fromTime, toTime);
			ArrayList presult = part.getDurationalSymbolsWithOnsetWithin(fromTime, toTime);
			result.addAll(presult);
		}
		return result;
	}

	public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsActiveWithin(Measure bar) throws IM3Exception {
		Meter ts = getActiveMeterAtBar(bar);
		return ScoreSong.this.getDurationalSymbolsActiveWithin(bar.getTime(),
				bar.getTime() + ts.getMeasureDurationAsTicks());
	}

	public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsWithOnsetWithin(Segment segment) throws IM3Exception {
		return ScoreSong.this.getDurationalSymbolsWithOnsetWithin(segment.getFrom().getTime(),
				segment.getTo().getTime());
	}

	public ArrayList<ScoreDurationalSymbol> getDurationalSymbolsActiveWithin(Segment segment) {
		return ScoreSong.this.getDurationalSymbolsActiveWithin(segment.getFrom().getTime(), segment.getTo().getTime());
	}*/

	/**
	 * It returns scorenotes alone or score notes inside chords
	 *
	 * @return
	 * @throws IM3Exception
	 */
	/*FRACTIONS  public TreeSet<ScientificPitch> getAllPitchesWithOnsetWithin(Measure bar) throws IM3Exception {
		TreeSet<ScientificPitch> result = new TreeSet<>();
		if (bar == null) {
			throw new IM3Exception("Cannot get the notes of a null bar");
		}

		ArrayList<ScoreDurationalSymbol> ses = this.getDurationalSymbolsWithOnsetWithin(bar);
		for (ScoreDurationalSymbol scoreSoundingElement : ses) {
			result.addAll(scoreSoundingElement.getScientificPitches());
		}
		return result;
	}*/

	public TreeSet<ScientificPitch> getAllPitches() throws IM3Exception {
		TreeSet<ScientificPitch> result = new TreeSet<>();
		for (ScorePart part : this.parts) {
			Collection<ScoreLayer> voices = part.getLayers();
			for (ScoreLayer v : voices) {
				result.addAll(v.getScientificPitches());
			}
		}
		return result;
	}

	/**
	 * It returns scorenotes alone or score notes inside chords
	 *
	 * @return
	 * @throws IM3Exception
	 */
	/*FRACTIONS  public TreeSet<ScientificPitch> getPitchesWithOnsetWithin(Segment segment) throws IM3Exception {
		TreeSet<ScientificPitch> result = new TreeSet<>();
		if (segment == null) {
			throw new IM3Exception("Cannot get the notes of a null segment");
		}
		ArrayList<ScoreDurationalSymbol> ses = this.getDurationalSymbolsWithOnsetWithin(segment);
		for (ScoreDurationalSymbol scoreSoundingElement : ses) {
			result.addAll(scoreSoundingElement.getScientificPitches());
		}
		return result;
	}*/

	public ScoreLayer getBottomVoice() throws IM3Exception {
		if (this.parts.isEmpty()) {
			throw new IM3Exception("The score has no parts");
		}
		ArrayList<ScorePart> ps = this.getPartsSortedByNumberAsc();
		for (int i = ps.size() - 1; i >= 0; i--) {
			if (!ps.get(i).getLayers().isEmpty()) {
				return ps.get(i).getVoicesSortedByNumber().last();
			}
		}
		throw new IM3Exception("No voice found");
	}

	// ----------------------------------------------------------------------
	// ----------------------- Part information --------------------------------
	// ----------------------------------------------------------------------

	/**
	 *
	 * @param partIndex
	 *            0 to numberofparts-1
	 * @return
	 * @throws IM3Exception
	 */
	public ScorePart getPart(int partIndex) throws IM3Exception {
		if (partIndex < 0) {
			throw new IM3Exception("The part number cannot be <0, it is " + partIndex);
		}
		if (partIndex >= parts.size()) {
			throw new IM3Exception("The part index cannot be >=" + parts.size() + ", it is " + partIndex);
		}
		return parts.get(partIndex);
	}

	/**
	 * Adds a part at the end of the parts
	 *
	 * @return
	 */
	public ScorePart addPart() {
		int max = 0;
		for (ScorePart v : parts) {
			max = Math.max(max, v.getNumber());
		}
		ScorePart v = new ScorePart(this, max + 1);
		idManager.assignNextID(v);
		parts.add(v);
		return v;
	}

	public boolean remove(ScorePart part) {
		return this.parts.remove(part);
	}

	/**
	 * @return the parts
	 */
	public final ArrayList<ScorePart> getParts() {
		return parts;
	}

	/**
	 * @return the parts
	 */
	public final ArrayList<ScorePart> getPartsSortedByNumberAsc() {
		ArrayList<ScorePart> res = new ArrayList<>();
		res.addAll(this.parts);
		Collections.sort(res, new Comparator<ScorePart>() {
			@Override
			public int compare(ScorePart o1, ScorePart o2) {
				int diff = o1.getNumber() - o2.getNumber();
				if (diff == 0) {
					return o1.hashCode() - o2.hashCode();
				} else {
					return diff;
				}
			}
		});
		return res;
	}

	/**
	 * Creates a part and inserts in into the song
	 *
	 * @param part
	 * @throws IM3Exception
	 */
	public void addPart(ScorePart part) throws IM3Exception {
		if (part.getNumber() < parts.size() && parts.get(part.getNumber()) != null) {
			throw new IM3Exception("Part with number " + part.getNumber() + " already exists");
		}
		parts.add(part);
		this.idManager.assignNextID(part);
	}

	/**
	 * It inverts the number and possition in the list of the layers and the
	 * score parts
	 *
	 * @throws IM3Exception
	 */
	public void invertPartAndVoiceNumbering() throws IM3Exception {
		ArrayList<ScorePart> p = new ArrayList<>();
		int number = 1;
		for (int i = this.parts.size() - 1; i >= 0; i--) {
			ScorePart part = parts.get(i);
			part.setNumber(number++);
			part.invertVoiceNumbering();
			p.add(part);
		}
		p.sort(new Comparator<ScorePart>() {

			@Override
			public int compare(ScorePart o1, ScorePart o2) {
				return o1.getNumber() - o2.getNumber();
			}
		});
		this.parts = p;
	}

	// ----------------------------------------------------------------------
	// ----------------------- Analysis information
	// --------------------------------
	// ----------------------------------------------------------------------
	public void moveAnalysisPartToBottom() {
		if (analysisPart != null) {
			this.parts.remove(analysisPart);
			this.parts.add(analysisPart);
			for (int i = 0; i < parts.size(); i++) {
				this.parts.get(i).setNumber(i + 1);
			}
		}
	}

	public ScoreAnalysisPart addAnalysisPart() throws IM3Exception {
		return addAnalysisPart("Analysis");
	}

	/**
	 * I adds the analysis parts, it does not add a staff
	 * @param name
	 *            The name of the part
	 * @return
	 * @throws IM3Exception
	 */
	public ScoreAnalysisPart addAnalysisPart(String name) throws IM3Exception {
		int max = 0;
		for (ScorePart v : parts) {
			max = Math.max(max, v.getNumber());
		}
		ScoreAnalysisPart part = new ScoreAnalysisPart(this, max + 1);
		this.parts.add(part);
		this.idManager.assignNextID(part);
		part.setName(name);
		this.analysisPart = part;
		if (analysisStaff == null) {
			analysisStaff = new AnalysisStaff(this, "9999", 90000); //TODO URGENT
		}
		this.analysisPart.addScoreLayer(analysisStaff);
		// ScoreAnalysisStaff staff = new ScoreAnalysisStaff(this.analysisPart);
		/*
		 * ScoreAnalysisStaff staff = new ScoreAnalysisStaff();
		 * staff.setNumber(1); this.analysisPart.addStaff(staff);
		 */
		return part;
	}
	/*FRACTIONS public void createAnalysisPartAndStaff() throws IM3Exception {
		createAnalysisPartAndStaff(false);
	}*/
	/**
	 * It adds the analysis staff, the layer and the corresponding staff. It always uses modern notation
	 * @throws IM3Exception
	 */
	/*FRACTIONS public void createAnalysisPartAndStaff(boolean createAnalysisHooks) throws IM3Exception {
		ScorePart part = addAnalysisPart();
		AnalysisStaff staff = new AnalysisStaff(part, "", 989898); //TODO 10000? - puesto a piñón - debería ser el último valor disponible
		ClefNone clef = new ClefNone();
		clef.setTime(Time.TIME_ZERO);
		staff.addClef(clef);
		part.addStaff(staff);
		addStaff(staff);
		if (createAnalysisHooks) {
			this.createAnalysisHooks(staff.getHooksLayer());
		}
	}*/

	

	public ScoreAnalysisPart getAnalysisPart() {
		return analysisPart;
	}

	public ScoreLayer getAnalysisVoice() throws IM3Exception {
		if (analysisPart == null) {
			throw new IM3Exception("The song has not any analysis part");
		}
		if (analysisPart.getLayers().isEmpty()) {
			throw new IM3Exception("The analysis part of the song has not any voice");
		}
		if (analysisPart.getLayers().size() > 1) {
			throw new IM3Exception("The analysis part of the song has more than one voice");
		}
		return analysisPart.getLayers().iterator().next();
	}

	public AnalysisStaff getAnalysisStaff() throws IM3Exception {
		if (analysisPart == null) {
			throw new IM3Exception("The song has not any analysis part");
		}
		if (analysisPart.getStaves().isEmpty()) {
			throw new IM3Exception("The analysis part of the song has not any staff");
		}
		if (analysisPart.getStaves().size() > 1) {
			throw new IM3Exception("The analysis part of the song has more than one staff");
		}

		Staff s = analysisPart.getStaves().iterator().next();
		if (!(s instanceof AnalysisStaff)) {
			throw new IM3Exception("The staff is not an analysis staff");
		}
		return (AnalysisStaff) s;
	}
	

	public boolean hasAnalysisStaff() {
		if (analysisPart == null) {
			return false;
		}
		if (analysisPart.getStaves().isEmpty()) {
			return false;
		}
		if (analysisPart.getStaves().size() > 1) {
			return false;
		}

		Staff s = analysisPart.getStaves().iterator().next();
        return s instanceof AnalysisStaff;
    }
	
	

	/*
	 * public ScoreStaff getBottomStaff() throws IM3Exception { if
	 * (this.parts.isEmpty()) { throw new
	 * IM3Exception("The score has no parts"); }
	 * 
	 * ArrayList<ScorePart> ps = this.getPartsSortedByNumberAsc(); for (int
	 * i=ps.size()-1; i>=0; i--) { if (!ps.get(i).getStaves().isEmpty()) {
	 * return ps.get(i).getStavesSortedByNumber().last(); } } throw new
	 * IM3Exception("No staff found"); }
	 */


	////////////////// - NOTATION PART ---
	/**
	 * 
	 * @return
	 * @throws RuntimeException
	 *             When actual sizes is null
	 */
	/*
	 * public IViewActualSizes getActualSizes() { if (actualSizes == null) {
	 * throw new RuntimeException("Actual sizes object not set"); } return
	 * actualSizes; }
	 */

	/*
	 * public boolean isActualSizesEmpty() { return actualSizes == null; }
	 */

	// ----------------------------------------------------------------------
	// ----------------------- Page information --------------------------------
	// ----------------------------------------------------------------------

	/*FRACCIONES public void addPages(int n) {
		for (int i = 0; i < n; i++) {
			pages.add(new Page(this, i + 1));
		}
	}

	public List<Page> getPages() {
		return pages;
	}*/

	// ----------------------------------------------------------------------
	// ----------------------- Staff and staff group information
	// --------------------------------
	// ----------------------------------------------------------------------
	public List<Staff> getStaves() {
		return staves;
	}
	

	public void addStaff(Staff staff) throws IM3Exception {
		for (Staff s : staves) {
			if (s.getHierarchicalOrder().equals(staff.getHierarchicalOrder())
					|| s.getNumberIdentifier() == staff.getNumberIdentifier()) {
				throw new IM3Exception("There exists another staff with hierarchical order " + s.getHierarchicalOrder()
						+ " or number identifier " + staff.getNumberIdentifier());
			}
		}
		staves.add(staff);
		/*for (Meter meter : getMeters()) {
			TimeSignature ts = meter.addNotation(staff.getNotationType(), staff);
			if (ts != null) {
				staff.addTimeSignature(ts);
			}
		}

		for (Key instrumentKey : getKeys()) {
			KeySignature ks = instrumentKey.addNotation(staff.getNotationType(), staff);
			if (ks != null) {
				staff.addKeySignature(ks);
			}
		}*/

		// important to be below meter creation because the bar time computing
		// depends on it
		/*for (Measure bar : getMeasures()) {
			Barline b = bar.addNotation(staff.getNotationType(), staff);
			if (b != null) {
				staff.addBarline(b);
			}
		}*/

	}

	public void addStaffAt(int position, Staff staff) throws IM3Exception {
		for (Staff s : staves) {
			if (s.getHierarchicalOrder().equals(staff.getHierarchicalOrder())
					|| s.getNumberIdentifier() == staff.getNumberIdentifier()) {
				throw new IM3Exception("There exists another staff with hierarchical order " + s.getHierarchicalOrder()
						+ " or number identifier " + staff.getNumberIdentifier());
			}
		}
		staves.add(position, staff);
		/*for (Meter meter : getMeters()) {
			TimeSignature ts = meter.addNotation(staff.getNotationType(), staff);
			if (ts != null) {
				staff.addTimeSignature(ts);
			}
		}

		for (Key instrumentKey : getKeys()) {
			KeySignature ks = instrumentKey.addNotation(staff.getNotationType(), staff);
			if (ks != null) {
				staff.addKeySignature(ks);
			}
		}*/

		// important to be below meter creation because the bar time computing
		// depends on it
		/*for (Measure bar : getMeasures()) {
			Barline b = bar.addNotation(staff.getNotationType(), staff);
			if (b != null) {
				staff.addBarline(b);
			}
		}*/

	}
	public void removeStaff(Staff staff) {
		staves.remove(staff);
		/*Collection<KeySignature> kss = staff.getKeySignatures();
		for (KeySignature k : kss) {
			k.getKey().removeNotation(staff);
		}
		Collection<TimeSignature> tss = staff.getTimeSignatures();
		for (TimeSignature t : tss) {
			t.getMeter().removeNotation(staff);
		}
		Collection<Barline> bs = staff.getBarlines();
		for (Barline b : bs) {
			b.getBar().removeNotation(staff);
		}*/
	}

	public void addStaffSystem(StaffGroup staffGroup) {
		staffGroups.add(staffGroup);
	}

	public List<StaffGroup> getStaffGroups() {
		return staffGroups;
	}

	// ----------------------------------------------------------------------
	// ----------------------- Notation / visual
	// --------------------------------
	// ----------------------------------------------------------------------

	/*public void addConnector(Connector<?,?> c) {
		this.connectors.add(c);
	}

	public void removeConnector(Connector<?,?> c) {
		this.connectors.remove(c);
	}

	public Collection<Connector<?,?>> getConnectors() {
		return connectors;
	}*/

	public void createVisualElementsIfNeeded() throws IM3Exception {
		throw new UnsupportedOperationException("FRACTIONS");
		/*FRACTIONS for (StaffSystem staffGroup : this.getStaffGroups()) {
			staffGroup.createVisualElementsIfNeeded();
		}

		for (ScorePart part : parts) {
			for (ScoreLayer voice : part.getVoices()) {
				for (LayoutCoreSymbol symbol : voice.getDurationalSymbolsAsSortedVector()) {
					symbol.createVisualElementsIfNeeded();
				}
			}
		}

		// Staves: ledger lines for simultaneous notes
		// instrumentKey signatures, changes...
		// accidentals for notes
		for (Staff staff : this.getStaves()) {
			staff.createNoteAccidentals(0, Time.MAX_VALUE);

		}

		this.createBeaming();
		// this.createTies();
		this.createAttachments();*/
	}


	/**
	 * It adds the bar
	 * staves
	 * 
	 * @param time
	 * @param bar
	 * @return The new added barlines
	 * @throws IM3Exception
	 */
	public void addMeasure(Time time, Measure bar) throws IM3Exception {
		bar.setTime(time);
		bar.setSong(this);
		this.measures.addValue(bar);
		idManager.assignNextID(bar);
		
		// now the meter is added to all staves
		/*ArrayList<Barline> result = new ArrayList<>();
		for (Staff staff : staves) {
			Barline ts = bar.addNotation(staff.getNotationType(), staff);
			if (ts != null && staff.getNotationType() == NotationType.eModern) { // if
																					// not
																					// existed
				staff.addBarline(ts);
				result.add(ts);
			}
		}*/
		//return result;
	}

    public void removeMeasure(Measure m) throws IM3Exception {
	    this.measures.remove(m);
    }

	/**
	 * It creates the symbols only used for notation, such as staves and clefs
	 */
	/*FRACTIONS public void completeNotation(NotationType notationType) throws IM3Exception {
		int i = 1;
		ScientificPitch A3 = new ScientificPitch(PitchClasses.A.getPitchClass(), 3);
		for (ScorePart part : parts) {
			if (part.getStaves().isEmpty()) {
				// add at least one staff
				Staff staff = new Pentagram(notationType, part, Integer.toString(i), i);
				part.addStaff(staff);
				this.addStaff(staff);

				i++;

				int j = 1;
				int notesUnderA3 = 0;
				int notesAboveA3 = 0;
				for (ScoreLayer voice : part.getVoices()) {
					StaffLayer layer = new StaffLayer(staff, j);
					staff.addLayer(layer);

					for (ScoreDurationalSymbol sse : voice.getDurationalSymbolsAsSortedVector()) {
						sse.setLayer(layer);
						layer.addSymbol(sse);
						if (sse instanceof ScoreNonRest) {
							Collection<NotePitchSymbolElement> ps = ((ScoreNonRest) sse).getPitchSymbols();
							for (NotePitchSymbolElement p : ps) {
								if (p.getScorePitch().getPitch().compareTo(A3) < 0) {
									notesUnderA3++;
								} else {
									notesAboveA3++;
								}
							}
						}
					}

					j++;
				}
				Clef clef;
				if (notesAboveA3 > notesUnderA3) {
					clef = new ClefG2(staff);
				} else {
					clef = new ClefF4(staff);
				}
				clef.setTime(0);
				staff.addClef(clef);
			}
		}
		if (this.getBarCount() == 0) {
			this.createBars();
		}
	}*/

	public int getNumVoices() {
		int n = 0;
		for (ScorePart part : parts) {
			n += part.getLayers().size();
		}
		return n;

	}

	/**
	 * It returns the symbols active (sounding) while the parameter snc is
	 * active
	 * 
	 * @param snc
	 * @return
	 */
	/*FRACTIONS public List<ScoreDurationalSymbol> getSonority(ScoreDurationalSymbol snc) throws IM3Exception {
		Time fromTime = snc.getTime();
		Time toTime = snc.getTime() + snc.getDurationInTicks();

		return this.getDurationalSymbolsActiveWithin(fromTime, toTime);
	}*/

	//FRACTIONS private static final int DEFAULT_MIDI_VELOCITY = 80;
	/**
	 * It computes the dynamics (volumes or MIDI velocity) of notes given the dynamics marks in the score
	 * @throws IM3Exception 
	 */
	/*FRACTIONS public void computeDynamics() throws IM3Exception {
		TreeMap<Time, Integer> velocityChanges = new TreeMap<>();
		for (Staff staff: staves) {
			for (StaffMark mark: staff.getMarks()) {
				if (mark instanceof DynamicMark) {
					DynamicMark dm = (DynamicMark) mark;
					velocityChanges.put(dm.getTime(), dm.getMidiVelocity());
				}
			}
			for (ScoreDurationalSymbol ssd: staff.getDurationalElementsSortedByTime()) {
				if (ssd instanceof ScoreNote) {
					Entry<Time, Integer> entry = velocityChanges.floorEntry(ssd.getTime());
					int velocity;
					if (entry == null) {
						velocity = DEFAULT_MIDI_VELOCITY;
					} else {
						velocity = entry.getValue();
					}
					((ScoreNote)ssd).setComputedMIDIVelocity(velocity);
					System.err.println("TODO: crescendo!!!!!!!!!!!!!!!!!!!");
				}
			}
		}
	}*/
	public void clearMeasures() {
		this.measures.clear();
	}
	public final int getMeaureCount() throws IM3Exception {
		return measures.getCount();
	}

	public Measure getFirstMeasure() throws IM3Exception {
		if (measures.isEmpty()) {
			throw new IM3Exception("There are no measures");
		}
		return measures.getFirstElement();
	}

    public Measure getLastMeasure() throws IM3Exception {
        if (measures.isEmpty()) {
            throw new IM3Exception("There are no measures");
        }
        return measures.getLastValue();
    }

	public boolean isLastMeasure(Measure bar) {
		if (this.measures.isEmpty()) {
			return false;
		} else {
			return this.measures.getLastValue().equals(bar);
		}
	}

	/**
	 * It returns the bar active at a given time
	 *
	 * @param time
	 * @return
	 * @throws IM3Exception
	 */
	public Measure getMeasureActiveAtTime(Time time) throws IM3Exception {
		try {
			return measures.getValueAtTime(time);
		} catch (IM3Exception e) {
			throw new IM3Exception("No active bar at time " + time);
		}
	}

	public ArrayList<Measure> getMeasuresSortedAsArray() throws IM3Exception {
		if (measures == null || measures.isEmpty()) {
			throw new IM3Exception("ScoreSong has no measures");
		}
		ArrayList<Measure> r = new ArrayList<>();
		r.addAll(measures.getValues()); // they are sorted
		return r;
	}
	
	public final Collection<Measure> getMeasures() {
		return measures.getValues();
	}

	/**
	 *
	 * @param time
	 * @return null if not found
	 */
	public Measure getMeasureWithOnset(Time time) {
		return measures.getElementWithTime(time);
	}

	// ----------------------------------------------------------------------
	// ----------------------- Harmonies information
	// ------------------------------
	// ----------------------------------------------------------------------
	// ----------------------------------------------------------------------
	// ----------------------- Harmony related information
	// --------------------------------
	// ----------------------------------------------------------------------
	public void replace(Harmony oldValue, Harmony newValue) throws IM3Exception {
		throw new UnsupportedOperationException("TO-DO After fractions");
		/*if (oldValue != null) {
			this.removeHarmony(oldValue);
		}
		this.addHarmony(newValue.getTime(), newValue);

		if (analysisPart != null && analysisPart.getStaves() != null && !analysisPart.getStaves().isEmpty()) {
			AnalysisStaff analysisStaff = getAnalysisStaff();

			ScoreAnalysisHook analysisHook = analysisStaff.findAnalysisHookWithOnset(newValue.getTime());
			if (analysisHook == null) {
				throw new IM3Exception("Not found any analysis hook for time " + newValue.getTime());
			}
			analysisHook.setHarmony(newValue);
		}*/
	}
	
	/**
	 *
	 *            if null it is removed
	 * @throws IM3Exception
	 */
	/*FRACTIONS public void replace(Harmony oldValue, Harmony newValue) throws IM3Exception {
		if (newValue == null) {
			this.harmonies.remove(oldValue);
		} else if (oldValue == null) {
			this.harmonies.addValue(newValue);
			idManager.assignNextID(newValue);
			newValue.setSong(this);
		} else {
			newValue.setSong(this);
			idManager.assignNextID(newValue);
			this.harmonies.replace(oldValue, newValue);
		}

		// now change the previous harmony where required
		updateHarmoniesAfter(newValue);
	}*/
	

	/*FRACTIONS  public void putHarmoniesinAnalysisHooks(AnalysisStaff analysisStaff) throws IM3Exception {
		for (Harmony h : this.getHarmonies()) {
			Time harmonyTime = h.getTime();
			if (analysisStaff == null) {
				throw new NotationException("The analysis staff is null");
			}
			ScoreAnalysisHook analysisHook = analysisStaff.findAnalysisHookWithOnset(harmonyTime);
			analysisHook.setHarmony(h);
		}
	}
	public void moveHarmomies(Time offset) throws IM3Exception {
		ArrayList<Harmony> harmonies = new ArrayList<>(getHarmonies());
		clearHarmonies();
		for (Harmony h : harmonies) { // move the harmonies
			addHarmony(h.getTime() + offset, h);
		}
	}*/
	

	public void clearHarmonies() {
		this.harmonies.clear();
	}

	/**
	 * @return the harmonies
	 */
	public final Collection<Harmony> getHarmonies() {
		return harmonies.getValues();
	}

	/*FRACCIONES public final List<Harmony> getHarmoniesInBar(Measure bar) throws IM3Exception {
		return harmonies.getOrderedValuesWithOnsetInRange(bar.getTime(), bar.getEndTime());
	}*/

	/**
	 * @return the harmonies
	 */
	public final ArrayList<Harmony> getHarmoniesSortedByTime() {
		return harmonies.getOrderedValues();
	}

	/**
	 * @param time
	 * @param harmony
	 * @throws IM3Exception
	 */
	public void addHarmony(Time time, Harmony harmony) throws IM3Exception {
		harmony.setTime(time);
		harmony.setSong(this);
		try {
			this.harmonies.addValue(harmony);
			idManager.assignNextID(harmony);
			updateHarmoniesAfter(harmony);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(ex.getMessage());
		}
	}

	// TODO No es coherente que el remove quite el harmony del analysis hook se
	// quite
	// con removeHarmony y no se ponga con addHarmony - el problema es que deben
	// estar
	// calculados los analysisHooks (véase ScoreSong.replace(Harmony ...

	/**
	 * @param h
	 * @para
	 */
	public void removeHarmony(Harmony h) throws IM3Exception {
		try {
			this.harmonies.remove(h);
			/*FRACTIONS if (h.getAnalysisHook() != null) {
				h.getAnalysisHook().removeHarmony(h);
			}*/
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3Exception(ex);
		}
	}

	/*
	 * It returns the harmony with onset at a given time or null if not present
	 *
	 * @param time
	 * 
	 * @return
	 */
	public Harmony getHarmonyWithOnsetOrNull(Time time) {
		return harmonies.getElementWithTime(time);
	}

	/**
	 * It returns the harmony active at a given time
	 *
	 * @param time
	 * @return
	 */
	public Harmony getHarmonyActiveAtTime(Time time) {
		try {
			return harmonies.getValueAtTime(time);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException("No harmony active at time " + time, ex);
		}
	}

	/**
	 * It returns the harmony active at a given time
	 *
	 * @param time
	 * @return
	 */
	public Harmony getHarmonyActiveAtTimeOrNull(Time time) {
		return harmonies.getValueAtTimeOrNull(time);
	}

	/**
	 * It obtains the harmony figureAndDots by computing the figureAndDots from a given
	 * harmony to its successor
	 *
	 * @param h
	 * @return
	 */
	public Time computeHarmonyDuration(Harmony h) {
		try {
			return harmonies.computeElementDurationFromIOI(h, getSongDuration());
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(
					"The element has not time and it has been already set when it was inserted: " + ex.toString());
		}
	}

	// TODo Ver cómo trabajamos con alt keys
	/**
	 * It returns the active harmony instrumentKey at a given time
	 *
	 * @param time
	 * @return
	 */
	public Key getHarmonyKeyActiveAtTimeOrNull(Time time) {
		NavigableMap<Time, Harmony> hs = harmonies.getOrderdValuesWithTimeLowerOrEqualThan(time);
		if (hs != null) {
			for (Iterator<Map.Entry<Time, Harmony>> iter = hs.descendingMap().entrySet().iterator(); iter.hasNext();) {
				Harmony h = iter.next().getValue();
				if (h.getActiveKey() != null) {
					return h.getActiveKey();
				}
			}
		}
		return null;
	}
	
	/**
	 * It browses the song backwards trying to find the last instrumentKey in a harmony
	 *
	 * @param time
	 * @return null if none found
	 */
	/*public Key findCurrentKeyInHarmony(Time time) {
		for (Map.Entry<Time, Harmony> entry : this.harmonies.getOrderdValuesWithTimeLowerOrEqualThan(time)
				.descendingMap().entrySet()) {
			if (entry.getValue().getActiveKey() != null) {
				return entry.getValue().getActiveKey();
			}
		}
		return null;
	}	*/

	// TODO Test unitario
	/**
	 * It returns the active harmony instrumentKey at a given time
	 *
	 * @param time
	 * @return
	 */
	public Degree getHarmonyDegreeActiveAtTimeOrNull(Time time) throws IM3Exception {
		Harmony h = harmonies.getValueAtTimeOrNull(time);
		if (h == null) {
			return null;
		} else {
			return h.getDegree().getActualChord().getDegree().getDegree();
		}
		/*
		 * NavigableMap<Time, Harmony> hs =
		 * harmonies.getOrderdValuesWithTimeLowerOrEqualThan(time); for
		 * (Iterator<Map.Entry<Time, Harmony>> iter =
		 * hs.descendingMap().entrySet().iterator(); iter.hasNext(); ) { Harmony
		 * h = iter.next().getValue(); if (h.getDegree()!= null) { return
		 * h.getDegree(); } } return null;
		 */
	}



	/**
	 * It updates the previousKey tempo of the harmonies after this one
	 *
	 * @param harmony
	 */
	private void updateHarmoniesAfter(Harmony harmony) throws IM3Exception {
		if (harmonies != null) {
			NavigableMap<Time, Harmony> hs = harmonies.getOrderdValuesWithTimeHigherThan(harmony.getTime());
			hs.firstEntry().getValue().setPreviousHarmony(harmony);
			/*for (Map.Entry<Time, Harmony> entrySet : hs.entrySet()) {
				//Time instrumentKey = entrySet.getKey();
				Harmony value = entrySet.getValue();
				if (!value.isKey()) {
					value.setPreviousKey(harmony.getActiveKey());
				} else {
					break; // finished
				}
			}*/
		}
	}



	// ----------------------------------------------------------------------
	// ----------------------- Tempo information ------------------------------
	// ----------------------------------------------------------------------
	/*FRACTIONS  public void replace(Tempo oldValue, Tempo newValue) throws IM3Exception {
		if (newValue == null) {
			this.tempoChanges.remove(oldValue);
			idManager.assignNextID(newValue);
		} else if (oldValue == null) {
			this.tempoChanges.addValue(newValue);
			idManager.assignNextID(newValue);
			newValue.setSong(this);
		} else {
			idManager.assignNextID(newValue);
			newValue.setSong(this);
			this.tempoChanges.replace(oldValue, newValue);
		}
	}*/

	/**
	 * It allows the override of values, some MIDI files contain this kind of
	 * tempo changes
	 *
	 * @param time
	 * @param t
	 */
	public void addTempoChange(Time time, Tempo t) {
		t.setTime(time);
		try {
			this.tempoChanges.addValueOverride(t);
			idManager.assignNextID(t);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(ex);
		}
	}

	public void removeTempoChange(Tempo t) {
		try {
			this.tempoChanges.remove(t);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(
					"The element has not time and it has been already set when it was inserted: " + ex.toString());
		}
	}

	/**
	 * @return the tempoChanges
	 */
	public final Collection<Tempo> getTempoChanges() {
		return tempoChanges.getValues();
	}

	/**
	 * It returns the tempo active at a given time, throwing an exception if not
	 * found
	 *
	 * @param time
	 * @return
	 */
	public Tempo getTempoAtTime(Time time) {
		try {
			return tempoChanges.getValueAtTime(time);
		} catch (IM3Exception ex) {
			Logger.getLogger(ScoreSong.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException("No tempo active at time " + time, ex);
		}
	}

	/**
	 * It returns the bar active at a given time
	 *
	 * @param time
	 * @return
	 */
	public Tempo getTempoAtTimeOrNull(Time time) {
		return tempoChanges.getValueAtTimeOrNull(time);
	}

	public IDManager getIdManager() {
		return idManager;
	}

	public void setAnacrusisOffset(Time anacrusisOffset) {
		this.anacrusisOffset = anacrusisOffset;		
	}
	
	public boolean isAnacrusis() {
		return this.anacrusisOffset != null;
	}

	public final Time getAnacrusisOffset() {
		return anacrusisOffset;
	}

	/**
	 * Cannot create measures when not using a modern meter (e.g. a mensural meter)
	 * @param createIncompleteBars If false, the measures that are not complete are not created
	 * @param songDuration
	 * @return new created barlines
	 * @throws IM3Exception
	 */
	/*FRACCIONES public void createBars(Time songDuration, boolean createIncompleteBars) throws IM3Exception {
		//ArrayList<Measure> barlines = new ArrayList<>();
		if (!songDuration.isZero()) { 
			ArrayList<Meter> mts = new ArrayList<>();
			mts.addAll(this.getMeters());
			
			if (mts.isEmpty() || !(mts.get(0) instanceof ModernMeter)) {
				return; // mensural meters or empty song
			}
			
			int barNum = 1;
			int n = mts.size();
			Time t = Time.TIME_ZERO;
			for (int i = 0; i < n; i++) {
				Time toTime;
				if (i == n - 1) {
					toTime = songDuration;
				} else {
					toTime = mts.get(i + 1).getTime();
				}
				Meter meter = mts.get(i);
				if (!(meter instanceof ModernMeter)) {
					throw new IM3Exception("Cannot create measures with non modern meters: " + meter.getClass());
				}
				
				Time dur = ((ModernMeter)meter).getMeasureDuration();
				int nbars;
				double diffInTime = toTime.substract(t).getComputedTime();
				if (createIncompleteBars) { //TODO ¿mejor usando fracciones también?
					nbars = (int) Math.ceil(diffInTime / dur.getComputedTime());
				} else {
					nbars = (int) Math.floor(diffInTime / dur.getComputedTime());
				}
				for (int ib = 0; ib < nbars; ib++) {
					if (this.getBarWithOnset(t) == null) { // if not existed
						Measure bar = new Measure(this, barNum);
						bar.setTime(t);
						this.addMeasure(bar.getTime(), bar);
					}
					t = t.add(dur);
					barNum++;
				}
			}
		}
	}*/

	/**
	 * It creates all missing measures
	 * 
	 *            If false, the measures that are not complete are not created
	 */
	/*FRACCIONES public void createBars(boolean createIncompleteBars) throws IM3Exception {
		createBars(this.getSongDuration(), createIncompleteBars);
	}

	public void createBars() throws IM3Exception {
		createBars(this.getSongDuration(), true);
	}*/

	public boolean hasMeasures() {
		return this.measures != null && !this.measures.isEmpty();
	}

	private void ensureHeader() {
		if (metadata == null) {
			metadata = new Metadata();			
		}
		if (metadata.getDescription() == null) {
			FileDescription description = new FileDescription();
			metadata.setDescription(description);
		}
		if (metadata.getDescription().getHeader() == null) {
			Header header = new Header();
			metadata.getDescription().setHeader(header);
		}		
	}
	public void addTitle(String title) {
		ensureHeader();
		metadata.getDescription().getHeader().addTitle(new Title(title));
	}

	public void addPerson(String role, String person) {
		ensureHeader();
		metadata.getDescription().getHeader().addPerson(new Person(role, person));
	}

	public void addPerson(PersonRoles role, String person) {
		ensureHeader();
		metadata.getDescription().getHeader().addPerson(new Person(role, person));
	}

	public int getNumMeasures() {
		if (measures == null) {
			return 0;
		} else {
			return measures.size();
		}
	}

	/**
	 * It returns the common instrumentKey for all staves at that time. For transposing instruments the 
	 * concert pitch key will be used.
	 * @param time
	 * @return null if not found
	 * @exception IM3Exception If several concert keys are found for the same time in different staves
	 */
	public Key getUniqueKeyWithOnset(Time time) throws IM3Exception {
		Key key = null;
		for (Staff staff: staves) {
			KeySignature ks = staff.getKeySignatureWithOnset(time);
			if (key == null && ks != null) {
				key = ks.getConcertPitchKey();
			} else if (ks != null && !ks.getConcertPitchKey().equals(key)) {
				throw new IM3Exception("Two different concert pitch keys in different staves: " + key + " and " + ks.getConcertPitchKey() + " in time " + time);
			} // else it is the same
		}
		return key;
	}

	/**
	 * It returns the common instrumentKey for all staves at that time. For transposing instruments the
	 * concert pitch key will be used.
	 * @param time
	 * @return
	 */
	public Key getUniqueKeyActiveAtTime(Time time) throws IM3Exception {
		Key key = null;
		for (Staff staff: staves) {
			KeySignature ks = staff.getRunningKeySignatureAt(time);
			if (key == null && ks != null) {
				key = ks.getConcertPitchKey();
			} else if (ks != null && !ks.getConcertPitchKey().equals(key)) {
				throw new IM3Exception("Two different concert pitch keys in different staves: " + key + " and " + ks.getConcertPitchKey() + " in time " + time);
			} // else it is the same
		}
		return key;
	}

	/**
	 * It returns the common time signature for all staves at that time when possible
	 * @param time
	 * @return null if not found
	 * @exception IM3Exception If several time signatures are found for the same time in different staves
	 */
	public TimeSignature getUniqueMeterWithOnset(Time time) throws IM3Exception {
		TimeSignature meter = null;
		for (Staff staff: staves) {
			TimeSignature ts = staff.getTimeSignatureWithOnset(time);
			if (meter == null && ts != null) {
				meter = ts;
			} else if (ts != null && !ts.equals(meter)) {
				throw new IM3Exception("Two different meters in different staves: " + meter + " and " + ts + " in time " + time);
			} // else it is the same
		}
		return meter;
	}

    public TreeSet<AtomFigure> getAtomFiguresSortedByTime() {
		TreeSet<AtomFigure> result = new TreeSet<>();
		for (ScorePart part: parts) {
			result.addAll(part.getAtomFigures());
		}
		return result;
	}

    public TreeSet<AtomPitch> getAtomPitchesSortedByTimeStaffAndPitch() {
		TreeSet<AtomPitch> ts = new TreeSet<>(AtomPitch.TIME_COMPARATOR);


		for (ScorePart part: parts) {
			ts.addAll(part.getAtomPitches());
		}
		return ts;
    }

    public ArrayList<AtomPitch> getAtomPitches() {
		ArrayList<AtomPitch> result = new ArrayList<>();
		for (ScorePart part: parts) {
			result.addAll(part.getAtomPitches());
		}
		return result;
    }

    /// Harmonies
    public void addHarm(Harm harm) throws IM3Exception {
        if (harms == null) {
            harms = new TimedElementCollection<>();
        }
        if (harm.getTime() == null) {
            throw new IM3Exception("The harm element " + harm + " has not time set");
        }
        harms.addValue(harm);
    }

    /**
     * May return null
     * @return
     */
    public ArrayList<Harm> getOrderedHarms() {
	    if (harms == null) {
	        return null;
        } else {
            return harms.getOrderedValues();
        }
    }

    public boolean hasHarms() {
	    return harms != null && !harms.isEmpty();
    }

	public void clearHarms() {
		this.harms.clear();
	}

	public Harm getHarmWithOnsetOrNull(Time time) {
	    if (harms == null) {
	        return null;
        }

	    return harms.getElementWithTime(time);
    }

    public ArrayList<Harm> getHarmsWithOnsetWithin(Measure bar) throws IM3Exception {
        if (harms == null) {
            return null;
        }

        return harms.getOrderedValuesWithOnsetInRange(bar.getTime(), bar.getEndTime());
    }

    public Harm getHarmActiveAtTimeOrNull(Time time) {
    	if (harms == null) {
    		return null;
		}

		return harms.getValueAtTimeOrNull(time);
    }

    public void addSystemBreak(SystemBreak sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("System break has not time set");
        }
    	systemBreaks.put(sb.getTime(), sb);
    }

    public HashMap<Time, SystemBreak> getSystemBreaks() {
        return systemBreaks;
    }

    public boolean hasSystemBreak(Time time) {
        return systemBreaks.containsKey(time);
    }


}
