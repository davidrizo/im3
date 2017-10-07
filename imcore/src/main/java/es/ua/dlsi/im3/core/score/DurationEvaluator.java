package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * It evaluates the duration of a note given its context. Maninly used by mensural notation
 */
public class DurationEvaluator {
    /**
     * Change it just if required
     * @param newAtom
     * @param scoreLayer
     * @param index
     * @throws IM3Exception
     */
    public void changeDurationIfRequired(Atom newAtom, ScoreLayer scoreLayer, int index) throws IM3Exception {
        // don't do anything by default
    }
}
