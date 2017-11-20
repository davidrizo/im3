package es.ua.dlsi.im3.core.score;

// TODO: 7/10/17 ¿Sería mejor que Measure tuviera MarkBarline?
public class DashedBarlineAcrossStaves extends Connector {
    private final Time time;

    public DashedBarlineAcrossStaves(Time time, Staff from, Staff to) {
        super(from, to);
        this.time = time;
    }

    public Time getTime() {
        return time;
    }
}
