package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * An explicit system break
 */
public class SystemBreak implements ITimedElementInStaff {
    Staff staff;

    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public SystemBreak(Time time, boolean manual) {
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
