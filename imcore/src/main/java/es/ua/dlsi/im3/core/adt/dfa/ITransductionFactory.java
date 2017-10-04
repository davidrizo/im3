package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;

public interface ITransductionFactory<TransductionType extends Transduction> {
    TransductionType create() throws IM3Exception;
}
