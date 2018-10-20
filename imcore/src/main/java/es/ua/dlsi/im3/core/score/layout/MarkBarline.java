package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;

import java.util.Objects;

/**
 * Used (usually in mensural notation) as a mark
 */
public class MarkBarline implements ITimedElementInStaff {
    Staff staff;
    Time time;

    public MarkBarline(Time time) {
        this.time = time;
    }

    public MarkBarline() {
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

    @Override
    public void move(Time offset) throws IM3Exception {
        if (time == null) {
            time = offset;
        } else {
            Staff prevStaff = staff;
            staff.remove(this);
            this.time = time.add(offset);
            prevStaff.addMarkBarline(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkBarline)) return false;
        MarkBarline that = (MarkBarline) o;
        return Objects.equals(staff, that.staff) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {

        return Objects.hash(staff, time);
    }

    public void setTime(Time time) {
        this.time = time;
    }


}
