package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class SlurBarlineNoteState extends OMRState{

    public SlurBarlineNoteState(int number, String name) {
        super(number, "Slurbarlinenote");
    }
    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {

    }

}
