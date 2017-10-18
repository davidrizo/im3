package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

public class TimeSignatureState extends OMRState {
    public TimeSignatureState(int number) {
        super(number, "keySig");
    }
    String text;

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (!token.getSymbol().equals(GraphicalSymbol.metersign)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an accidental and found a " + token.getSymbol());
        }

        // TODO: 5/10/17 C3 escrito como C 3 ... Quizás habrá que hacer varios estados del autómata
        text = token.getValue();
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (text == null) {
            throw new IM3RuntimeException("Token value cannot be null");
        }
        TimeSignature ts = null;
        switch (text) {
            case "C":
                ts = new TimeSignatureCommonTime(NotationType.eMensural);
                break;
            // TODO: 5/10/17 Los demás compases 
            default:
                transduction.setZeroProbability();
        }
        if (ts != null) {
            try {
                transduction.getStaff().addTimeSignature(ts);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException();
            }
        }
    }
}
