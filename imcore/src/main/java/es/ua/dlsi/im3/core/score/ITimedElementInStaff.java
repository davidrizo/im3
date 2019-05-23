package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

public interface ITimedElementInStaff extends ITimedElement {
	Staff getStaff();
	void setStaff(Staff staff) throws IM3Exception;
}
