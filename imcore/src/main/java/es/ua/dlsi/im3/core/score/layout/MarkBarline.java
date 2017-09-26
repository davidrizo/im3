package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;

/**
 * Used (usually in mensural notation) as a mark
 */
public class MarkBarline implements ITimedElementInStaff {
    Staff staff;
    Time time;

    public MarkBarline(Time time) {
        this.time = time;
    }

    @Override
    public Staff getStaff() {
        return staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public Time getTime() {
        return time;
    }
}
