package es.ua.dlsi.im3.core.score;

/**
 * An explicit system break
 */
public class SystemBreak implements ITimedElement {
    private final Time time;
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

    public boolean isManual() {
        return manual;
    }
}