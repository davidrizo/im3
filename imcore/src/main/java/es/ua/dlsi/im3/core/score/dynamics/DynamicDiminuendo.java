package es.ua.dlsi.im3.core.score.dynamics;

import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.StaffTimedPlaceHolder;
import es.ua.dlsi.im3.core.score.Wedge;

public class DynamicDiminuendo extends Wedge {

	public DynamicDiminuendo(Staff staff, StaffTimedPlaceHolder from, StaffTimedPlaceHolder to) {
		super(staff, from, to);
	}
}
