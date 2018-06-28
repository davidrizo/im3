package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class FermataState extends OMRState {
    public FermataState(int number, String name) {
        super(number, name);
    }

    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        //TODO Agregar a la partitura en futuras implementaciones
    }
}
