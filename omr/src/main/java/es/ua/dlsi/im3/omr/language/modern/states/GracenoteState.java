package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class GracenoteState extends OMRState {

    public GracenoteState(int number, String name) {
        super(number, "Gracenote");
    }
    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {

    }
}
