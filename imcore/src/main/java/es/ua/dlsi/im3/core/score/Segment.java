package es.ua.dlsi.im3.core.score;

import java.util.HashMap;

import es.ua.dlsi.im3.core.IDGenerator;


/**
 * A grouping of notes belonging to all parts in the score (vertical slice of the score) in a time range 
@author drizo
@date 03/06/2011
 **/
public class Segment implements Cloneable {
	long ID;
	Time from;
	/**
	 * Not included
	 */
	Time to;
	String name;
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Decorations added at runtime <decoration kind, tempo>
	 */
	@SuppressWarnings("rawtypes")
	HashMap<Class, ISegmentDecoration> decorations;
	
	/**
	 * @param name
	 * @param time
	 * @param to (not included)
	 */
	public Segment(String name, Time time, Time to) {
		this.from = time;
		this.to = to;
		decorations = new HashMap<>();
		this.name = name;
		ID = IDGenerator.getID();		
	}
	
	/**
	 * 
	 * @param time
	 * @param to (not included)
	 */
	public Segment(Time time, Time to) {
		ID = IDGenerator.getID();
		this.from = time;
		this.to = to;
		decorations = new HashMap<>();
		
	}
	/**
	 * @return the from
	 */
	public final Time getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public final Time getTo() {
		return to;
	}

	public final void setFrom(Time time) {
	    this.from = time;
	}
	
	public final void setTo(Time time) {
	    this.to = time;
	}
		
	/**
	 * 
	 * @param decoration
	 */
	public void addDecoration(ISegmentDecoration decoration) {
		this.decorations.put(decoration.getClass(), decoration);
	}
	/**
	 * Get decorations given a class name
	 * @param aClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ISegmentDecoration getDecoration(Class aClass) {
		return decorations.get(aClass);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Segment ID=" + ID + " " + (name!=null?name:"") + " [from=" + from + ", to=" + to + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
	
	public Time getDuration() {
		return this.to.substract(this.from);
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long id) {
		this.ID = id;
	}
	
	/**
	 * Merge both consecutive segments 
	 */
	/*public void mergeWith(Segment s2) throws IM3Exception {
		if (getTo().getTime() != s2.getFrom().getTime()) {
			throw new IM3Exception("Cannot merge not consecutive segments, first ends at " 
					+ getTo().getTime() + " and second begins at " + s2.getFrom().getTime());
		}
		this.to = s2.getTo();
	}*/	
	
	@Override
	public Segment clone() {
		Segment s = new Segment(from, to);
		s.setID(ID);
		s.setName(name);
		return s;		
	}
	
	public boolean contains(Time time) {
		return time.compareTo(this.getFrom())>=0  && time.compareTo(this.getTo()) < 0;
	}
}
