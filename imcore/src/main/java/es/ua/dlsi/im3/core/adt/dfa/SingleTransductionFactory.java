package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;

public class SingleTransductionFactory implements ITransductionFactory<Transduction> {
    @Override
    public Transduction create(BigFraction initialProbability) {
        return new Transduction(initialProbability);
    }
}
