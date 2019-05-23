package es.ua.dlsi.im3.core.score;

/**
 * An explicit page break
 */
public class SystemPageBreak implements ITimedElement {
    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public SystemPageBreak(Time time, boolean manual) {
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
}
