package es.ua.dlsi.im3.core.score;

// TODO: 7/10/17 ¿Sería mejor que Measure tuviera MarkBarline?
public class DashedBarlineAcrossStaves extends Connector {
    private final Measure measure;


    public DashedBarlineAcrossStaves(Measure measure, Staff from, Staff to) {
        super(from, to);
        this.measure = measure;
    }

    public Measure getMeasure() {
        return measure;
    }
}
