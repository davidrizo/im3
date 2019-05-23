package es.ua.dlsi.im3.core.score;

import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Base class for all musical events (chords, notes, rests, tuplets, beamed groups...). It does not contain
 * explicit graphical information such as coordinates. It is able to report the explicit notes (onset, pitch, duration)
 * in it for being used for analysis tasks: e.g. the actual notes in a tremolo 
 * It may contain more pitches (e.g. chords) or the same figure for several pitches (e.g. torculus neume), 
 * or several pitches with several figures (e.g. tuplets).
 * This is why everything is private and/or final
 * 
 * In order to be used as a base class for single elements such as SimpleNote, the operations that handle the internal
 * structure are protected
 * @author drizo
 */
public abstract class Atom implements Comparable<Atom>, IUniqueIDObject, ITimedElementInStaff, IStaffElementInLayer {
	private Time onset;
	/**
	 * It is usually set from the figure(s) in the atom, but it may be different in the case of the
	 * atoms in a tuplet
	 */
	private Time duration;
	private String ID;
	/**
	 * The staff all contents of atom belongs to. It is possible to change elements from staff and layer
	 * If this atom belongs to a compound atom, this compound atom will have the staff information
	 * and this field will be taken as a staff change
	 */
	private Staff staff;
	/**
	 * The staff all contents of atom belongs to. It is possible to change elements from staff and layer 
	 */
	private ScoreLayer layer;
	/**
	 * When it belongs to a compound atom such as a tuplet
	 */
	private CompoundAtom parentAtom;		
	
	public Atom() {
		duration = new Time(Fraction.ZERO); 
		onset = new Time(Fraction.ZERO);
	}
	
	/**
	 * Package visibility. Used by layer on the addition of the atom to the layer
	 * @param layer
	 */
	void setLayer(ScoreLayer layer) {
		this.layer = layer;
	}


    /**
     * It returns the atom staff or the one of the its parent atom
     * @return
     */
	@Override
	public Staff getStaff() {
		if (staff != null) {
			return staff;
		} else if (parentAtom != null) {
			return parentAtom.getStaff();
		} else if (layer != null) {
			return layer.getStaff();
		} else {
			return null;
		}
	}

	public Staff getAtomSpecificStaff() {
	    return staff;
    }

	@Override
	public void setStaff(Staff staff) throws IM3Exception {
	    if (this.staff != null && this.staff != staff) {
	        this.staff.remove(this);
        }
		this.staff = staff;
	}
	
	
	public final ScoreLayer getLayer() {
		if (layer != null) {
			return layer;
		} else if (parentAtom != null) {
			return parentAtom.getLayer();
		} else {
			return null;
		}
	}

	@Override
	public final Time getDuration() {
		return duration;
	}
	
	public double getQuarterRatioDuration() {
		return this.duration.getComputedTime();
	}

	/**
	 * @deprecated Use getEndTime()
	 * Onset + duration
	 * @return
	 * @throws IM3Exception 
	 */
	public Time getOffset() {
		return onset.add(duration);
	}

	public void setTime(Time onset) {
		this.onset = onset;		
	}

	@Override	
	public Time getTime()  {
		if (this.parentAtom == null) {
			return this.onset;
		} else {
			return this.parentAtom.getTime().add(this.onset);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((layer == null) ? 0 : layer.hashCode());
		result = prime * result + ((staff == null) ? 0 : staff.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((onset == null) ? 0 : onset.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (layer == null) {
			if (other.layer != null)
				return false;
		} else if (!layer.equals(other.layer))
			return false;
		if (staff == null) {
			if (other.staff != null)
				return false;
		} else if (!staff.equals(other.staff))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (onset == null) {
			if (other.onset != null)
				return false;
		} else if (!onset.equals(other.onset))
			return false;
		return true;
	}

	@Override
	public int compareTo(Atom o) {
		int diff = onset.compareTo(o.onset);
		if (diff == 0) {
			diff = duration.compareTo(o.duration);
			if (diff == 0) {
				if (staff != null && o.staff != null) {
					diff = staff.compareTo(o.staff);
				}
				if (diff == 0) {
					if (layer != null && o.layer != null) {
						diff = layer.compareTo(o.layer);
					}
				}
			}
		}
		if (diff == 0) {
			diff = hashCode() - o.hashCode(); //TODO Test
		}
		return diff;
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
		return "A";
	}
	
	protected void addDuration(Time duration) {
		this.duration = this.duration.add(duration);
	}
	protected void substractDuration(Time duration) {
		this.duration = this.duration.substract(duration);
	}
	/**
	 * Compute all notes (pitch + onset + duration) in the atom. Tied notes are merged into the first one
	 * as the sum of the durations
	 * @return null if no played notes (e.g. rests or tied notes)
	 * @throws IM3Exception 
	 */
	public abstract List<PlayedScoreNote> computePlayedNotes() throws IM3Exception;
	

	@Override
	public String toString() {
		return "onset=" + onset + ", duration=" + duration + ", ID=" + ID;
	}

	public void onFigureDurationChanged(Time oldDuration, Time newDuration, boolean notifyChangesToLayer) throws IM3Exception {
		duration = new Time(duration.getExactTime().subtract(oldDuration.getExactTime()).add(newDuration.getExactTime()));
		if (notifyChangesToLayer) {
            notifyDurationChange();
        }
	}
	
	private void notifyDurationChange() throws IM3Exception {
		if (layer != null) {
			layer.onAtomDurationChanged(this);
		}
	}
	
	public void setParentAtom(CompoundAtom parentAtom) {
		this.parentAtom = parentAtom;
	}

	/**
	 * May be null
	 * @return
	 */
	public abstract List<AtomPitch> getAtomPitches();

	public abstract List<AtomFigure> getAtomFigures();
	/**
	 * Expanded list of atoms, i.e., a list of the note for single figure notes, and a list with the contents of the
	 * notes in a tuplet for a tuple  
	 * @return
	 */
	public abstract List<Atom> getAtoms();

	public final CompoundAtom getParentAtom() {
		return parentAtom;
	}

	/**
	 * The duration is usually set from the figure(s) in the atom, but it may be different in the case of the
	 * atoms in a tuplet. Use with care
	 */
	public void setDuration(Time duration) {
		this.duration = duration;
	}

    // TODO: 16/4/18 Test unitario
    public void move(Time offset) {
	    setTime(getTime().add(offset));
    }

    public Time getEndTime() {
		return getTime().add(getDuration());
    }
}

