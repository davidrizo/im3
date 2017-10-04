package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.ITransductionFactory;

public class OMRTransductionFactory implements ITransductionFactory<OMRTransduction> {
    @Override
    public OMRTransduction create() throws IM3Exception {
        return new OMRTransduction();
    }
}
