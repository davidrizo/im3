package es.ua.dlsi.im3.omr.language.mensural;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.language.mensural.states.OMRState;

public class BarLineState extends OMRState {
    public BarLineState(int number) {
        super(number, "barline");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        //try {
            transduction.getStaff().addCoreSymbol(new MarkBarline(transduction.getLayer().getDuration()));
        /*} catch (IM3Exception e) {
            throw new IM3RuntimeException(e);
        }*/
    }
}
