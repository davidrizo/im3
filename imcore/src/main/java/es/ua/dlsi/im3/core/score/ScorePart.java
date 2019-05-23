package es.ua.dlsi.im3.core.score;

import java.util.*;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @author drizo
 */
public class ScorePart implements Comparable<ScorePart>, IUniqueIDObject, IStaffContainer {

	protected LinkedHashMap<Integer, ScoreLayer> layers;
	// TODO Garantizar que es único
	/**
	 * Order in its parent
	 */
	int number;
	/**
	 * Name of the part
	 */
	String name;
	protected ScoreSong scoreSong;

	/**
	 * List of staves where the part is present, note a staff may contain
	 * several parts
	 */
	List<Staff> staves;

	/**
	 * Page and system beginnings for just this part
	 */
	PageSystemBeginnings pageSystemBeginnings;

	String ID;

	/**
	 * Use with caution. Used just by some converter of the library, such as MensImporter
	 * @param number
	 */
	public ScorePart(int number) {
		this.scoreSong = null;
		this.number = number;
		this.layers = new LinkedHashMap<>();
		this.staves = new ArrayList<>();
		this.pageSystemBeginnings = new PageSystemBeginnings();
	}

	public ScorePart(ScoreSong scoreSong, int number) {
		this.scoreSong = scoreSong;
		this.number = number;
		this.layers = new LinkedHashMap<>();
		this.staves = new ArrayList<>();
		this.pageSystemBeginnings = new PageSystemBeginnings();
	}

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
		return "ScorePart [name=" + name + ", number = " + number + "]";
	}

	public Time computeScoreDuration() {
		Time max = Time.TIME_ZERO;
		for (ScoreLayer voice : layers.values()) {
			Time dur = voice.getDuration();
			max = Time.max(max, dur);
		}
		return max;
	}

	public void setNumber(int i) {
		this.number = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ScoreSong getScoreSong() {
		return scoreSong;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public PageSystemBeginnings getPageSystemBeginnings() {
		return pageSystemBeginnings;
	}

	/**
	 * It does not contain notes
	 *
	 * @return
	 */
	public boolean isEmpty() {
		for (ScoreLayer voice : layers.values()) {
			if (!voice.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public int size() {
		int n = 0;
		for (ScoreLayer v : layers.values()) {
			n += v.size();
		}
		return n;

	}

	/**
	 * @return True if at least a note or chord is found
	 */
	public boolean containsNonRests() {
		for (ScoreLayer voice : layers.values()) {
			TreeSet<ScientificPitch> pitches = voice.getScientificPitches();
			if (!pitches.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.number;
		hash = 97 * hash + Objects.hashCode(this.name);
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
		final ScorePart other = (ScorePart) obj;
		if (this.number != other.number) {
			return false;
		}
        return Objects.equals(this.name, other.name);
    }

	@Override
	public int compareTo(ScorePart o) {
		int diff = this.number - o.number;
		if (diff == 0) {
			if (name == null && o.name == null) {
				return 0;
			} else if (name == null) {
				return 1;
			} else if (o.name == null) {
				return -1;
			} else {
				return name.compareTo(o.name);
			}
		} else {
			return diff;
		}
	}

	// ----------------------------------------------------------------------
	// ----------------------- Voices information
	// --------------------------------
	// ----------------------------------------------------------------------

	/**
	 * Add a new voice with number = maximum voice number found + 1, thus, by
	 * default, the first voice number is 1
	 * Layers can only be created by the part
	 *
	 * @return
	 * @throws IM3Exception
	 */
	public ScoreLayer addScoreLayer(Staff staff) throws IM3Exception {
		int max = 0;
		for (ScoreLayer v : layers.values()) {
			max = Math.max(max, v.getNumber());
		}
		ScoreLayer v = new ScoreLayer(this, max + 1, getScoreSong().getDurationEvaluator());
		if (staff != null) {
			staff.addLayer(v);
		}
		addScoreLayer(v);
		return v;
	}

	/**
	 * Add a new voice with number = maximum voice number found + 1, thus, by
	 * default, the first voice number is 1
	 * Layers can only be created by the part
	 *
	 * @return
	 * @throws IM3Exception
	 */
	public ScoreLayer addScoreLayer() throws IM3Exception {
		return addScoreLayer((Staff)null);
	}

	/**
	 * Layers can only be created by the part
	 *
	 * @param voice
	 * @throws IM3Exception
	 */
	private void addScoreLayer(ScoreLayer voice) throws IM3Exception {
		if (layers.get(voice.getNumber()) != null) {
			throw new IM3Exception("The voice with number " + voice.getNumber() + " already exists");
		}
		layers.put(voice.getNumber(), voice);
		this.scoreSong.getIdManager().assignNextID(voice);
	}

	public void removeScoreLayer(ScoreLayer voice) throws IM3Exception {
		if (layers.get(voice.getNumber()) != voice) {
			throw new IM3Exception("The voice with number " + voice.getNumber()
					+ " does not correspond to the one trying to be removed");
		}
		this.layers.remove(voice.getNumber());
	}

	public boolean existsScoreLayer(int nvoice) {
		return layers.containsKey(nvoice);
	}

	/**
	 * @param nvoice Usually they begin from 1
	 * @return
	 * @throws IM3Exception
	 */
	public ScoreLayer getVoice(int nvoice) throws IM3Exception {
		ScoreLayer result = layers.get(nvoice);
		if (result == null) {
			throw new IM3Exception("The voice with number " + nvoice + " does not exist");
		}
		return result;
	}

	public Collection<ScoreLayer> getLayers() {
		return layers.values();
	}

	public boolean hasLayers() {
		return layers != null && !layers.isEmpty();
	}

	/**
	 * Conveniency method, this part is supposed to have just a voice. If not an
	 * exception is thrown
	 *
	 * @return
	 */
	public ScoreLayer getUniqueVoice() throws IM3Exception {
		if (layers.size() != 1) {
			throw new IM3Exception("This part has not just one voice, it has " + layers.size() + " layers");
		}
		return layers.values().iterator().next();
	}

	public TreeSet<ScoreLayer> getVoicesSortedByNumber() {
		TreeSet<ScoreLayer> sortedVoices = new TreeSet<>();
		sortedVoices.addAll(layers.values());
		return sortedVoices;
	}

	public void invertVoiceNumbering() {
		ArrayList<ScoreLayer> sv = new ArrayList<>();
		sv.addAll(this.layers.values());
		Collections.sort(sv, new Comparator<ScoreLayer>() {
			@Override
			public int compare(ScoreLayer o1, ScoreLayer o2) {
				int diff = o2.getNumber() - o1.getNumber();
				if (diff == 0) {
					return o1.hashCode() - o2.hashCode();
				} else {
					return diff;
				}
			}
		});
		this.layers.clear();
		for (int i = 0; i < sv.size(); i++) {
			ScoreLayer v = sv.get(i);
			v.setNumber(i + 1);
			this.layers.put(i + 1, v);
		}
	}

	// ----------------------------------------------------------------------
	// ----------------------- Information retrieval ------------------------
	// ----------------------------------------------------------------------
	public Collection<ScientificPitch> getScientificPitches() {
		TreeSet<ScientificPitch> result = new TreeSet<>();
		for (ScoreLayer voice : layers.values()) {
			result.addAll(voice.getScientificPitches());
		}
		return result;
	}

	/**
	 * @param fromTime inclusive
	 * @param toTime   exclusive
	 * @return A sorted list
	 */
	public List<AtomFigure> getAtomFiguresWithOnsetWithin(Time fromTime, Time toTime) {
		ArrayList<AtomFigure> result = new ArrayList<>();
		for (ScoreLayer voice : layers.values()) {
			result.addAll(voice.getAtomFiguresWithOnsetWithin(fromTime, toTime));
		}

		Collections.sort(result);
		return result;
	}

	/**
	 *
	 * @param bar
	 * @return sorted array by time
	 * @throws IM3Exception
	 */
	public List<AtomFigure> getAtomFiguresWithOnsetWithin(Measure bar) throws IM3Exception {
		return getAtomFiguresWithOnsetWithin(bar.getTime(), bar.getEndTime());
	}

	/**
	 * @return A sorted list
	 */
	public List<AtomPitch> getAtomPitches() {
		List<AtomPitch> result = new LinkedList<>();
		for (ScoreLayer voice : layers.values()) {
			result.addAll(voice.getAtomPitches());
		}

		Collections.sort(result);
		return result;
	}

    public List<AtomPitch> getAtomPitchesWithOnsetWithin(Segment segment) {
        List<AtomPitch> result = new LinkedList<>();
        for (ScoreLayer voice : layers.values()) {
            result.addAll(voice.getAtomPitchesWithOnsetWithin(segment));
        }

        return result;
    }



    /**
	 * @return A sorted list
	 */
	public List<AtomFigure> getAtomFigures() {
        List<AtomFigure> result = new LinkedList<>();
		for (ScoreLayer voice : layers.values()) {
			result.addAll(voice.getAtomFigures());
		}

		Collections.sort(result);
		return result;
	}

	// ----------------------------------------------------------------------
	// ----------------------- Staff information
	// --------------------------------
	// ----------------------------------------------------------------------
	public void addStaff(Staff staff) {
	    staff.addPart(this);
		this.staves.add(staff);
		this.scoreSong.getIdManager().assignNextID(staff);
	}

    public void addStaffAt(int index, Staff staff) {
        this.staves.add(index, staff);
        this.scoreSong.getIdManager().assignNextID(staff);
    }


    @Override
	public List<Staff> getStaves() {
		return staves;
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
	}

	/*
	 * public int getNumStaves() { return staves.size(); } public
	 * TreeSet<ScoreStaff> getStavesSortedByNumber() { TreeSet<ScoreStaff>
	 * sortedStaves = new TreeSet<>(); sortedStaves.addAll(staves); return
	 * sortedStaves; }
	 */

	/*
	 * public List<ScoreStaff> getStaves() { return this.staves; }
	 * 
	 * public ScoreStaff getStaff(int nstaff) { for (ScoreStaff scoreStaff :
	 * staves) { if (scoreStaff.getNumber() == nstaff) { return scoreStaff; //
	 * the use of arrays for small sets is efficient, no need to use other data
	 * structures } } return null; }
	 * 
	 * public boolean existsStaff(int nst) { for (ScoreStaff scoreStaff :
	 * staves) { if (scoreStaff.getNumber() == nst) { return true; // the use of
	 * arrays for small sets is efficient, no need to use other data structures
	 * } } return false; }
	 */

	/**
	 * @return Array of symbols sorted first by time, then by note content
	 */
	/*
	 * public final ArrayList getDurationalSymbolsAsSortedVector() { ArrayList
	 * result = new ArrayList<>(); for (ScoreLayer voice: layers.values()) {
	 * result.addAll(voice.getScoreSoundingElementsAsVector()); //TODO Sort }
	 * 
	 * }
	 */
	@Override
	public String __getIDPrefix() {
		return "PART";
	}

	public List<Atom> getAtomsSortedByTime() {
		List<Atom> atoms = new LinkedList<>();
		for (ScoreLayer layer : layers.values()) {
			atoms.addAll(layer.getAtoms());
		}
		Collections.sort(atoms);
		return atoms;
	}

    /**
     * Use with caution. Used just by some converter of the library
     */
    public void setSong(ScoreSong song) {
        this.scoreSong = song;
    }

    public void addSystemBeginning(SystemBeginning sb0) throws IM3Exception {
    	pageSystemBeginnings.addSystemBeginning(sb0);
    }

    public void addPageBeginning(PageBeginning pb0) throws IM3Exception {
		pageSystemBeginnings.addPageBeginning(pb0);
	}
}
