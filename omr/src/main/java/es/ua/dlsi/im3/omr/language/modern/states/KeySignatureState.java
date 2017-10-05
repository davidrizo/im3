package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

import java.util.ArrayList;

public class KeySignatureState extends OMRState {
    ArrayList<PositionInStaff> positions;
    ArrayList<Accidentals> accidentals;

    public KeySignatureState(int number) {
        super(number, "keySig");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (!token.getSymbol().equals(GraphicalSymbol.accidental)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an accidental and found a " + token.getSymbol());
        }

        if (accidentals == null) {
            accidentals = new ArrayList<>();
            positions = new ArrayList<>();
        }

        if (token.getValue() == null) {
            throw new IM3RuntimeException("Value of accidental is null");
        }

        switch (token.getValue()) {
            case "b":
                accidentals.add(Accidentals.FLAT);
                break;
            case "#":
                accidentals.add(Accidentals.SHARP);
                break;
            default:
                // TODO: 4/10/17 Modern key signatures or key signature change may contain naturals
                transduction.setZeroProbability();
                //throw new IM3Exception("Cannot generate use this accidental in a key signature: " + token.getValue());
        }
        positions.add(token.getPositionInStaff());
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (accidentals == null) {
            // If no accidental has been found no key signature is indicated in the score
            // Not to be confused with the presence of a CM or Am key
            throw new IM3RuntimeException("Cannot generate a key signature without keys");
        }

        if (isStateChange) {
            Key key = null;

            // TODO: 4/10/17 Comprobar que las alteraciones (posiciones en líneas) son las correctas, si no dar p=0
            try {
                if (accidentals.get(0) == Accidentals.FLAT) {
                    key = new Key(-accidentals.size(), Mode.UNKNOWN);
                } else {
                    key = new Key(accidentals.size(), Mode.UNKNOWN);
                }
                transduction.getStaff().addKeySignature(new KeySignature(transduction.getStaff().getNotationType(), key));
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

            positions = null;
            accidentals = null;
        }
    }
}
