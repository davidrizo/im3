package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.ITransductionFactory;
import es.ua.dlsi.im3.core.score.NotationType;
import org.apache.commons.math3.fraction.BigFraction;

public class OMRTransductionFactory implements ITransductionFactory<OMRTransduction> {
    private final NotationType notationType;

    public OMRTransductionFactory(NotationType notationType) {
        this.notationType = notationType;
    }

    @Override
    public OMRTransduction create(BigFraction initialProbability) throws IM3Exception {
        return new OMRTransduction(initialProbability, notationType);
    }
}
