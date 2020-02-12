package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Used to things such as serving as waiting for a plain chant to finish
 */
public class Space extends SimpleRest {

    public Space(Time time, Figures figure, int dots) {
        super(figure, dots);
        this.setTime(time);
    }

    public Space(Figures figure, int dots, Time alteredDuration) {
        super(figure, dots, alteredDuration);
    }

    public Space(SimpleRest simpleRest) throws IM3Exception {
        super(simpleRest);
    }
}
