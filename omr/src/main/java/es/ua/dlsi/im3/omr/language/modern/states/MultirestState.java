package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class MultirestState extends OMRState {
    public MultirestState(int number, String name) {
        super(number, "multirest");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        //TODO 13/12/17 extraer de MultirestDigitState numero de compases de espera e introducirlos en la partitura
    }
}
