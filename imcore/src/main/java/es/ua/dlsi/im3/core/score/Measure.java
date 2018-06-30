package es.ua.dlsi.im3.core.score;


import es.ua.dlsi.im3.core.IM3Exception;

/**
 * It is just a horizontal division of the song It may be computed of
 * specifically added to the song
 *
 * @author drizo
 * @date 03/06/2011
 *
 */
public class Measure implements Comparable<Measure>, ITimedElement, IUniqueIDObject {
    boolean repeatBackwards; //TODO
    boolean repeatForward;//TODO

	/**
	 * Measure number
	 */
	private Integer number;
	private ScoreSong song;
	private Time time;
	private String ID;
	private Time endTime;
	private Time duration;
	//private final HashSet<VerticalScoreDivision> stavesAndGroups;

	/**
	 * @param imeasureNumber
	 */
	public Measure(ScoreSong song, Integer imeasureNumber) {
		this.number = imeasureNumber;
		this.song = song;
		this.time = new Time();
		//stavesAndGroups = new HashSet<>();
	}

	public Measure(ScoreSong song) {
		this.song = song;
		this.time = new Time();
		//stavesAndGroups = new HashSet<>();
	}
	
	/*public void addStaffOrGroup(VerticalScoreDivision v) {
		stavesAndGroups.add(v);
	}
	
	public boolean contains(VerticalScoreDivision v)  {
		return stavesAndGroups.contains(v);
	}*/
	/**
	 * @param imeasureNumber
	 * @param imeasureNumber
	 */
	/*
	 * public Measure(long time, int imeasureNumber) , Meter ts, Key ks) {
	 * super(time); this.number = imeasureNumber;
	 * this.timeSignature = ts; this.keySignature = ks; }
	 */
	/**
	 * @return the number
	 */
	public final Integer getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public final void setNumber(Integer number) {
		this.number = number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		/*
		 * return "Measure [number=" + number + ", timeSignature=" + timeSignature +
		 * ", keySignature=" + keySignature + ", time=" + time + "]";
		 */
		return "Measure [number=" + number + "]";
	}

	// TODO Test
	/**
	 *
	 * @return End time in ticks
	 * @throws IM3Exception
	 */
	/*FRACCIONES public Time getDuration(Staff staff) throws IM3Exception {
		TimeSignature ts = staff.getActiveMeterAtBar(this);
		if (ts instanceof ModernMeter) {
			return ((ModernMeter)ts).getMeasureDuration();
		} else {
			throw new IM3Exception("Cannot compute duration with non modern meters");
		}
	}*/

	// TODO Test
	/**
	 *
	 * @return ScoreDuration in ticks. This time is not included in the
	 *         bar, it belongs to the next one
	 * @throws IM3Exception
	 */
	/*FRACCIONES public Time getEndTime() throws IM3Exception {
		if (time == null) {
			throw new IM3Exception("The time has not been set yet");
		}
		if (song == null) {
			throw new IM3Exception("The song is nulll, cannot compute the end time");
		}
		Meter ts = song.getActiveMeterAtBar(this);
		Time meterDuration;
		if (ts instanceof ModernMeter) {
			meterDuration =  ((ModernMeter)ts).getMeasureDuration();
		} else {
			throw new IM3Exception("Cannot compute duration with non modern meters");
		}
		
		return getTime().add(meterDuration);
	}*/

	/*
	 * @Override public void move(long offset) throws IM3Exception { if (time ==
	 * null) { throw new IM3Exception("The time has not been set yet"); }
	 * setTime(this.time.getTime() + offset); }
	 */

	@Override
	public int compareTo(Measure b) {
		return number - b.number;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measure measure = (Measure) o;

        if (number != null ? !number.equals(measure.number) : measure.number != null) return false;
        return time != null ? time.equals(measure.time) : measure.time == null;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    /**
	 * If the onset of the note lies inside the time span of the bar
	 * 
	 * @param element
	 * @return
	 * @throws IM3Exception
	 *             When the time of the element is not defined
	 */
	/*FRACCIONES public boolean containsInTime(ITimedElement element) throws IM3Exception {
		return element.getTime().compareTo(this.getTime()) >= 0
				&& element.getTime().compareTo(this.getEndTime()) < 0;
	}*/

	public final void setSong(ScoreSong song) {
		this.song = song;
	}

	
	public final ScoreSong getSong() {
		return song;
	}

	@Override
	public String __getIDPrefix() {
		return "M";
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		ID = id;
		
	}

	@Override
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.duration = null;
		this.time = time;		
	}

    @Override
    public void move(Time offset) {
	    this.duration = null;
        this.time = time.add(offset);
    }
	
	/**
	 * Several staves may contain different meters
	 * @param staff
	 * @return
	 * @throws IM3Exception 
	 */
	/*public Time getEndTimeBasedOnMeter(Staff staff) throws IM3Exception {
		TimeSignature ts = staff.getRunningTimeSignatureAt(time);
		if (ts == null) {
			throw new IM3Exception("There is not a time signature at time " + time + " in staff " + staff);
		}
		if (ts instanceof ITimeSignatureWithDuration) {
			return time.add(((ITimeSignatureWithDuration)ts).getMeasureDuration());
		} else {
			throw new IM3Exception("Cannot compute the end time of a measure with a non ITimeSignatureWithDuration time signature");
		}
	}*/

	public boolean hasEndTime() {
		return endTime != null;
	}
	public Time getEndTime() throws IM3Exception {
		if (endTime == null) {
			throw new IM3Exception("End time is not set");
		}
		return endTime;
	}
	
	public void setEndTime(Time etime) throws IM3Exception {
		this.duration = null;
		if (etime.compareTo(time) <= 0) {
			throw new IM3Exception("Cannot set an end time (" + etime + ") <= start time " + time);
		}
		this.endTime = etime;
	}
	/**
	 * Several staves may contain different meters
	 * @param staff
	 * @return
	 * @throws IM3Exception 
	 */
	/*public Time getDurationBasedOnMeter(Staff staff) throws IM3Exception {
		TimeSignature ts = staff.getRunningTimeSignatureAt(time);
		if (ts == null) {
			throw new IM3Exception("There is not a time signature at time " + time + " in staff " + staff);
		}
		if (ts instanceof ITimeSignatureWithDuration) {
			return ((ITimeSignatureWithDuration)ts).getMeasureDuration();
		} else {
			throw new IM3Exception("Cannot compute the end time of a measure with a non ITimeSignatureWithDuration time signature");
		}
	}*/

	/**
	 * Based on end time
	 * @return
	 * @throws IM3Exception When time or the end time has not been set
	 */
	public Time getDuration() throws IM3Exception {
		if (time == null) {
			throw new IM3Exception("The time has not been set");
		}
		if (endTime == null) {
			throw new IM3Exception("The end time has not been set");
		}
		if (duration == null) {
			duration = endTime.substract(time);
		}
		return duration;
	}

	public boolean isEndTimeSet() {
		return endTime != null;
	}
}
