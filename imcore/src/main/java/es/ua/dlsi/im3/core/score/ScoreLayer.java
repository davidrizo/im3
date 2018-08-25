package es.ua.dlsi.im3.core.score;

import java.util.*;

import org.apache.commons.lang3.math.Fraction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 * This layer may contain atoms that belong to other staff
 * @author drizo
 */
public class ScoreLayer implements Comparable<ScoreLayer>, IUniqueIDObject {
	String ID;
	int number;
	/**
	 * Cannot be null
	 */
	ScorePart part;
	/**
	 * It may be null
	 */
	Staff staff;
	
	/**
	 * This is the main list of elements
	 */
	List<Atom> atoms;

    DurationEvaluator durationEvaluator;

	/**
	 * They can only be created by a part, this is why this is just package visible
	 * @param part
	 * @param number
	 */
	ScoreLayer(ScorePart part, int number, DurationEvaluator durationEvaluator) {
		this.part = part;
		this.number = number; //TODO Comprobar que es un número único
		atoms = new ArrayList<>();
		this.durationEvaluator = durationEvaluator;
	}

    /**
     * Used to replace the way durations are evaluated, mainly used in MensuralNotation
     * @param durationEvaluator
     */
	public void setDurationEvaluator(DurationEvaluator durationEvaluator) {
	    this.durationEvaluator = durationEvaluator;
    }

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	/**
	 * It updates the onsets from the atom fromAtom (not included) to the end of
	 * the list of atoms
	 * 
	 * @param fromAtom
	 * @throws IM3Exception
	 */
	private void updateOnsets(Atom fromAtom) throws IM3Exception {
		int index = atoms.indexOf(fromAtom);
		if (index < 0) {
			throw new IM3Exception("Cannot find referenced atom");
		}
		// correct onset times
		updateOnsets(index);
	}

	/**
	 * It updates the onsets from the atom fromAtom (not included) to the end of
	 * the list of atoms
	 * 
	 * @throws IM3Exception
	 */
	private void updateOnsets(int fromAtomIndex) {
		// correct onset times
		Atom lastAtom = atoms.get(fromAtomIndex);
		for (int i = fromAtomIndex + 1; i < atoms.size(); i++) {
			atoms.get(i).setTime(lastAtom.getOffset());
		}
	}

	public void add(Atom atom) throws IM3Exception {
		//try {
			atom.setTime(getDuration());
			atom.setLayer(this);
        /*} catch (IM3Exception e) {
			throw new IM3RuntimeException("The onset should have been set for all atoms in a voice");
		}*/
        evaluateDurationBeforeAdd(atom, atoms.size());
		atoms.add(atom);
	}
	
	//TODO añadir con huecos, he quitado el VoiceGap
	public void add(Time time, Atom atom) throws IM3Exception {
		atom.setTime(time);
		atom.setLayer(this); //drizo 20180302
        evaluateDurationBeforeAdd(atom, atoms.size());
		atoms.add(atom);
	}

	
	// TODO Test unitario
	public void addAfter(Atom referenceAtom, Atom newAtom) throws IM3Exception {
		int index = atoms.indexOf(referenceAtom);
		if (index < 0) {
			throw new IM3Exception("Cannot find referenced atom");
		}
        evaluateDurationBeforeAdd(newAtom, index+1);
		atoms.add(index + 1, newAtom);

		// correct onset times
		updateOnsets(index + 1);
	}

    private void evaluateDurationBeforeAdd(Atom newAtom, int index) throws IM3Exception {
        durationEvaluator.changeDurationIfRequired(newAtom,this, index);
    }

    // TODO Test unitario
	public void addBefore(Atom referenceAtom, Atom newAtom) throws IM3Exception {
		int index = atoms.indexOf(referenceAtom);
		if (index < 0) {
			throw new IM3Exception("Cannot find referenced atom");
		}
        evaluateDurationBeforeAdd(newAtom, index);
		atoms.add(index, newAtom);

		// correct onset times
		updateOnsets(index);
	}
	
	public void remove(Atom atom) throws IM3Exception {
		int index = atoms.indexOf(atom);
		if (index < 0) {
			throw new IM3Exception("Cannot find referenced atom");
		}
		atoms.remove(index);
		if (atoms.size() > index) { // if not last
			updateOnsets(index);	
		}
	}

	
	

	/**
	 * Sequence of ordered notes (first time, next pitch) that should be played
	 * @return
	 * @throws IM3Exception 
	 */
	public List<PlayedScoreNote> getPlayedNotes() throws IM3Exception {
		List<PlayedScoreNote> result = new LinkedList<>();
		
		for (Atom atom : atoms) {
			List<PlayedScoreNote> pn = atom.computePlayedNotes();
			if (pn != null) {
				result.addAll(pn);
			}
		}
		Collections.sort(result);
		return result;
	}
	/**
	 * Sequence of (non necessarily ordered) onset and continuation pitches 
	 * @return
	 */
	public List<AtomPitch> getAtomPitches() {
		List<AtomPitch> result = new LinkedList<>();
		for (Atom atom : atoms) {
			List<AtomPitch> atomPitches = atom.getAtomPitches();
			if (atomPitches != null) {
				for (AtomPitch atomPitch: atomPitches) {
					if (atomPitch.getStaff() == staff) { // no staff change
						result.add(atomPitch);
					}
				}
			}
		}
		return result;
	}

	
	/**
	 * Sequence of figures with their absolute onset times
	 * 
	 * @return
	 */
	public List<AtomFigure> getAtomFigures() {
		List<AtomFigure> result = new LinkedList<>();

		for (Atom atom : atoms) {
			result.addAll(atom.getAtomFigures());
		}
		return result;
	}

	/**
	 * 
	 * @return Num of atoms
	 */
	public int size() {
		return atoms.size();
	}

	public Atom getAtom(int index) {
		return atoms.get(index);
	}

	public Time getDuration() {
		/*
		 * Fraction duration = Fraction.ZERO; for (Atom atom: atoms) { duration
		 * = duration.add(atom.getExactDuration()); } return new Time(duration);
		 */
		if (atoms.isEmpty()) {
			return new Time(Fraction.ZERO);
		} else {
		    Time result = atoms.get(atoms.size() - 1).getOffset();
			return result;
		}
	}

	public void onAtomDurationChanged(Atom atom) throws IM3Exception {
		updateOnsets(atom);

	}

	// ----------------------------------------------------------------------
	// ----------------------- General information
	// --------------------------------
	// ----------------------------------------------------------------------

	@Override
	public String toString() {
		return "ScoreLayer [number=" + number + "]";
	}

	public ScorePart getPart() {
		return part;
	}

	/**
	 * package visibility for reordering in ScorePart
	 *
	 * @param i
	 */
	void setNumber(int i) {
		this.number = i;
	}

	public int getNumber() {
		return this.number;
	}

	/**
	 * It does not contain atoms (notes, chords...)
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return atoms.isEmpty();
	}

	@Override
	public int compareTo(ScoreLayer o) {
		if (this.getPart() != o.getPart()) {
			return this.getPart().getNumber() - o.getPart().getNumber();
		} else {
			return this.number - o.number;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + this.number;
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
		final ScoreLayer other = (ScoreLayer) obj;
        return this.number == other.number;
    }

	public TreeSet<ScientificPitch> getScientificPitches() {
		TreeSet<ScientificPitch> result = new TreeSet<>();
		List<AtomPitch> pitchSeq = getAtomPitches();
		for (AtomPitch atomPitch : pitchSeq) {
			result.add(atomPitch.getScientificPitch());
		}
		return result;
	}

	//TODO Todos los métodos de bajo (within onset ...) son extremadamente ineficientes
	// Debemos usar los RangeTree .... del im2
	/*FRACCIONES public final List<AtomPitch> getAtomPitchesWithOnsetWithin(Measure bar) throws IM3Exception {
		return getAtomPitchesWithOnsetWithin(bar.getTime(), bar.getEndTime());
	}*/

	/*FRACCIONES public final List<AtomFigure> getAtomFiguresWithOnsetWithin(Measure bar) throws IM3Exception {
		return getAtomFiguresWithOnsetWithin(bar.getTime(), bar.getEndTime());
	}*/

	/**
	 * 
	 * @param fromTime Included
	 * @param toTime Not included
	 * @return
	 * @throws IM3Exception 
	 */
	public List<AtomFigure> getAtomFiguresWithOnsetWithin(Time fromTime, Time toTime)  {
		List<AtomFigure> result = new LinkedList<>();
		List<AtomFigure> figureSeq = getAtomFigures();
		for (AtomFigure atomFigure : figureSeq) {
			if (atomFigure.getTime().isContainedIn(fromTime, toTime)) {
				result.add(atomFigure);
			} 
		}
		return result;
	}


	public List<Atom> getAtomsWithOnsetWithin(Measure bar) throws IM3Exception {
		return getAtomsWithOnsetWithin(bar.getTime(), bar.getEndTime());
	}

	

	/**
	 * 
	 * @param fromTime Included
	 * @param toTime Not included
	 * @return
	 * @throws IM3Exception 
	 */	
	public List<Atom> getAtomsWithOnsetWithin(Time fromTime, Time toTime) {
		ArrayList<Atom> result = new ArrayList<>();
		for (Atom atom : atoms) {
			if (atom.getTime().isContainedIn(fromTime, toTime)) {
				result.add(atom);
			} 
		}
		return result;
	}

	//TODO Debemos asegurar que no hay dos atom con el mismo onset

	/**
     * It does not include subatoms
	 * @param time
	 * @return
	 */
	public Atom getAtomWithOnset(Time time) {
		for (Atom atom : atoms) {
			if (atom.getTime().equals(time)) {
				return atom;
			}
		}
		return null;
	}

    /**
     * It includes subatoms
     * @param time
     * @return
     */
	public Atom getAtomExpandedWithOnset(Time time) {
        for (Atom atom : atoms) {
            List<Atom> sa = atom.getAtoms();
            for (Atom a: sa) {
                if (a.getTime().equals(time)) {
                    return a;
                }
            }
        }
        return null;
    }

	public List<Atom> getAtoms() {
	    return atoms;
	}

	public TreeSet<Atom> getAtomsSortedByTime() {
		TreeSet<Atom> result = new TreeSet<>(new Comparator<Atom>() {
			@Override
			public int compare(Atom o1, Atom o2) {
				int diff = o1.getTime().compareTo(o2.getTime());
				if (diff == 0) {
					return o1.compareTo(o2);
				} else {
					return diff;
				}
			}
		});
		result.addAll(getAtoms());
		return result;
	}


	public Atom getLastAtom() throws IM3Exception {
		if (atoms.isEmpty()) {
			throw new IM3Exception("There are no atoms");
		}
		return atoms.get(atoms.size()-1);
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
		return "V";
	}

	public Staff getStaff() {
		return staff;
	}

    public TreeSet<AtomPitch> getAtomPitchesSortedByTime() {
		TreeSet<AtomPitch> result = new TreeSet<>(new Comparator<AtomPitch>() {
			@Override
			public int compare(AtomPitch o1, AtomPitch o2) {
				int diff = o1.getTime().compareTo(o2.getTime());
				if (diff == 0) {
					return o1.getScientificPitch().compareTo(o2.getScientificPitch());
				} else {
					return diff;
				}
			}
		});
		result.addAll(getAtomPitches());
		return result;
    }

    //TODO Hacer una estructura externa auxiliar para hacer estos cálculos - mantenerlos en un treeset
    public AtomPitch getAtomPitchesWithOnset(Time time) {
	    List<AtomPitch> atomPitches = this.getAtomPitches(); //TODO Esto está haciendo el mismo cálculo muchas veces
        for (AtomPitch ap: atomPitches) {
            if (ap.getTime().equals(time)) {
                return ap;
            }
        }
	    return null;
    }

    public List<AtomPitch> getAtomPitchesWithOnsetWithin(Segment segment) {
        List<AtomPitch> result = new LinkedList<>();
        List<AtomPitch> atomPitches = this.getAtomPitches(); //TODO Esto está haciendo el mismo cálculo muchas veces
        for (AtomPitch ap: atomPitches) {
            if (segment.contains(ap.getTime())) {
                result.add(ap);
            }
        }
        return result;
    }

    /**
     *
     * It creates the beaming between eight or shorter notes that form groups
     * See AutoBeaming in Renz, K. (2002). Algorithms and Data Structures for a
     * Music Notation System based on G, 1–163.
     */
	public void createBeaming() throws IM3Exception {
        Measure lastMeasure = null;
        ArrayList<SingleFigureAtom> group = null;
        SingleFigureAtom lastFigureAtom = null;
        int lastNoteBeat = -1;
        for (Atom atom: getAtomsSortedByTime()) {
            if (atom instanceof SingleFigureAtom) {
                SingleFigureAtom singleFigureAtom = (SingleFigureAtom) atom;
                TimeSignature ts = staff.getRunningTimeSignatureAt(singleFigureAtom.getTime());
                int beat = ts.getIntegerBeat(singleFigureAtom.getTime());
                Measure measure = staff.getScoreSong().getMeasureActiveAtTime(singleFigureAtom.getTime());
                if (lastFigureAtom != null
                        && lastMeasure == measure // same pointer
                        //&& lastFigureAtom.getAtomFigure().getDuration().equals(singleFigureAtom.getAtomFigure().getDuration())
                        && beat == lastNoteBeat) {
                    group.add(singleFigureAtom);
                } else {
                    if (group != null) { // not first note
                        constructBeamIfRequired(group);
                    }
                    group = new ArrayList<>();
                    group.add(singleFigureAtom);
                    lastFigureAtom = singleFigureAtom;
                    lastNoteBeat = beat;
                }
                lastMeasure = measure;

            }
        }
        if (group != null) {
            constructBeamIfRequired(group);
        }
    }

    private void constructBeamIfRequired(ArrayList<SingleFigureAtom> group) {
	    if (group != null && group.size() > 1) {
	        BeamGroup beamedGroup = new BeamGroup(true);
	        for (SingleFigureAtom atom: group) {
	            beamedGroup.add(atom);
            }
        }
    }

    public void clear() {
	    this.atoms.clear();
    }


}
