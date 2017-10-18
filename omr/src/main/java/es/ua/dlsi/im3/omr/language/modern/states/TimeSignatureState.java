package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

public class TimeSignatureState extends OMRState {
    // TODO: 4/10/17 C3, C, C/...
    Integer numerator;
    Integer denominator;

    public TimeSignatureState(int number) {
        super(number, "keySig");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (!token.getSymbol().equals(GraphicalSymbol.digit)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an accidental and found a " + token.getSymbol());
        }

        if (token.getPositionInStaff().getLine() == 4) {
            numerator = Integer.parseInt(token.getValue()); // TODO: 4/10/17 Comprobar que es un valor válido
        } else if (token.getPositionInStaff().getLine() == 2) {
            denominator = Integer.parseInt(token.getValue()); // TODO: 4/10/17 Comprobar que es un valor válido
        } else {
            throw new IM3RuntimeException("TO-DO: si es línea 3 podría ser un C"); // TODO: 4/10/17 CommonTime...
        }
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (isStateChange) {
            if (numerator == null || denominator == null) {
                throw new IM3RuntimeException("Invalid grammar for Time Signature, numerator or denominator are null"); //TODO C, C/..
            }

            TimeSignature timeSignature = new FractionalTimeSignature(numerator, denominator);
            try {
                transduction.getStaff().addTimeSignature(timeSignature);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

            denominator = null;
            numerator = null;
        }
    }
}
