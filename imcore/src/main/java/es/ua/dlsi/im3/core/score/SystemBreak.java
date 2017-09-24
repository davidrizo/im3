package es.ua.dlsi.im3.core.score;

/**
 * An explicit system break
 */
public class SystemBreak implements ITimedElement {
    private final Time time;

    public SystemBreak(Time time) {
        this.time = time;
    }

    @Override
    public Time getTime() {
        return time;
    }
}
