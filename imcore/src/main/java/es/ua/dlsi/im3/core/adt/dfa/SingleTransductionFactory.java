package es.ua.dlsi.im3.core.adt.dfa;

public class SingleTransductionFactory implements ITransductionFactory<Transduction> {
    @Override
    public Transduction create() {
        return new Transduction();
    }
}
