package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.BigFraction;

public interface ITransductionFactory<TransductionType extends Transduction> {
    TransductionType create(BigFraction initialProbability) throws IM3Exception;
}
