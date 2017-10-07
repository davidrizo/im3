package es.ua.dlsi.im3.core.score.mensural;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;

public class BinaryDurationEvaluator extends DurationEvaluator {
    /**
     * If the duration is equals to the duration specified in Figures set factor to 1, else use other factor
     */
    Time factor;

    public BinaryDurationEvaluator(Time factor) {
        this.factor = factor;
    }

    public Time getFactor() {
        return factor;
    }

    @Override
    public void changeDurationIfRequired(Atom newAtom, ScoreLayer scoreLayer, int index) throws IM3Exception {
        // TODO: 7/10/17 Ligatures
        if (!factor.isOne()) {
            if (newAtom instanceof SingleFigureAtom) {
                SingleFigureAtom singleFigureAtom = (SingleFigureAtom) newAtom;
                singleFigureAtom.setDuration(singleFigureAtom.getAtomFigure().getFigure().getDurationWithDots(singleFigureAtom.getAtomFigure().getDots()).multiplyBy(factor));
            } else {
                throw new IM3Exception("Atom " + newAtom.getClass() + " not implemented");
            }
        }
    }
}
