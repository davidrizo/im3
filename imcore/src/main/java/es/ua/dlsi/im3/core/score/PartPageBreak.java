package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * An explicit page break
 */
public class PartPageBreak implements ITimedElementInStaff {
    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;
    private Staff staff;

    public PartPageBreak(Time time, boolean manual) {
        this.time = time;
        this.manual = manual;
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public void move(Time offset) {
        this.time = time.add(offset);
    }
    public boolean isManual() {
        return manual;
    }

    @Override
    public Staff getStaff() {
        return staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
