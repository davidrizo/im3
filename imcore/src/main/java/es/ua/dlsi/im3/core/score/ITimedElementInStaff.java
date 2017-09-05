package es.ua.dlsi.im3.core.score;

public interface ITimedElementInStaff extends ITimedElement {
	Staff getStaff();
	void setStaff(Staff staff);
}
