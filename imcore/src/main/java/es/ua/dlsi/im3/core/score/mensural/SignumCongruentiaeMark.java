package es.ua.dlsi.im3.core.score.mensural;

import es.ua.dlsi.im3.core.score.*;

public class SignumCongruentiaeMark extends StaffMark implements IStaffElementWithoutLayer, ITimedElementWithSetter {
	String facsimileID;

	public SignumCongruentiaeMark(Staff staff, Time time) {
		super(staff, time);
	}

	@Override
	public String getFacsimileElementID() {
		return facsimileID;
	}

	@Override
	public void setFacsimileElementID(String facsimileElementID) {
		this.facsimileID = facsimileID;
	}

	@Override
	public String __getID() {
		return facsimileID;
	}

	@Override
	public void __setID(String id) {
		this.facsimileID = id;
	}

	@Override
	public String __getIDPrefix() {
		return "SC";
	}


}
