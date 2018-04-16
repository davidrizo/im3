package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * An explicit system break
 */
public class PageBreak implements ITimedElement {
    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public PageBreak(Time time, boolean manual) {
        this.time = time;
        this.manual = manual;
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public void move(Time offset) throws IM3Exception {
        this.time = time.add(offset);
    }
    public boolean isManual() {
        return manual;
    }
}
