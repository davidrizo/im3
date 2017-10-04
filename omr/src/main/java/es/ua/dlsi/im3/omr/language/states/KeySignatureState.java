package es.ua.dlsi.im3.omr.language.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.KeySignature;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

import java.util.ArrayList;
import java.util.List;

public class KeySignatureState extends OMRState {
    List<GraphicalToken> accidentals;

    public KeySignatureState(int number) {
        super(number, "keySig");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) throws IM3Exception {
        if (!token.getSymbol().equals(GraphicalSymbol.accidental)) {
            throw new IM3Exception("Expected an accidental and found a " + token.getSymbol());
        }

        if (accidentals == null) {
            accidentals = new ArrayList<>();
        }

        if (token.getValue() == null) {
            throw new IM3Exception("Value of accidental is null");
        }

        accidentals.add(token);
    }

    @Override
    public void onExit(State nextState, OMRTransduction transduction) throws IM3Exception {
        if (accidentals == null) {
            // If no accidental has been found no key signature is indicated in the score
            // Not to be confused with the presence of a CM or Am key
            throw new IM3Exception("Cannot generate a key signature without keys");
        }
    }
}
