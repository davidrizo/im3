package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.MeterSign;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class TimeSignatureState extends OMRState {
    public TimeSignatureState(int number) {
        super(number, "keySig");
    }
    MeterSigns meterSigns;

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        if (!(token.getSymbol() instanceof MeterSign)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected a metersign and found a " + token.getSymbol());
        }

        MeterSign symbol = (MeterSign) token.getSymbol();
        meterSigns = symbol.getMeterSigns();
        // TODO: 5/10/17 C3 escrito como C 3 ... Quizás habrá que hacer varios estados del autómata
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (meterSigns == null) {
            throw new IM3RuntimeException("Meter signs cannot be null");
        }
        TimeSignature ts = null;
        switch (meterSigns) {
            case C:
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
