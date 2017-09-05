package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;

/**
 * Convenience class, not a really ITimedElementInStaff. It is used by the MusicXML importer
 * @author drizo
 *
 */
public class MusicXMLBackup implements ITimedElementInStaff{
	Time time;

	/**
	 * The time to backup
	 * @param time
	 */
	public MusicXMLBackup(Time time) {
		super();
		this.time = time;
	}

	@Override
	public Time getTime() {
		return time;
	}

	@Override
	public Staff getStaff() {
		return null;
	}

	@Override
	public void setStaff(Staff staff) {
	}

}
