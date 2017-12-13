package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class FermataState extends OMRState {
    public FermataState(int number, String name) {
        super(number, name);
    }

    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        //TODO Agregar a la partitura
    }
}
