package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;

import java.util.Objects;

/**
 * Used (usually in mensural notation) as a mark
 */
public class MarkBarline implements ITimedElementInStaff, ITimedElementWithSetter, IStaffElementWithoutLayer {
    Staff staff;
    Time time;
    boolean endBarline;

    public MarkBarline(Time time) {
        this.time = time;
    }

    public MarkBarline() {
    }

    public MarkBarline(MarkBarline markBarline) {
        this.staff = markBarline.staff;
        this.endBarline = markBarline.endBarline;
    }

    public MarkBarline clone() {
        return new MarkBarline(this);
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
            prevStaff.addElementWithoutLayer(this);
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

    @Override
    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isEndBarline() {
        return endBarline;
    }

    public void setEndBarline(boolean endBarline) {
        this.endBarline = endBarline;
    }
}
