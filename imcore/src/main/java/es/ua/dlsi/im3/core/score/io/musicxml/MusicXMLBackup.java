package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3Exception;
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
    Integer horizontalOrderInStaff;
    String ID;
	String facsimileElementID;

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
    public void move(Time offset) {
        this.time = time.add(offset);
    }
	@Override
	public Staff getStaff() {
		return null;
	}

	@Override
	public void setStaff(Staff staff) {
	}

	@Override
	public String getFacsimileElementID() {
		return facsimileElementID;
	}

	@Override
	public void setFacsimileElementID(String facsimileElementID) {
		this.facsimileElementID = facsimileElementID;
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
		return "musicxmlbackup";
	}
}
