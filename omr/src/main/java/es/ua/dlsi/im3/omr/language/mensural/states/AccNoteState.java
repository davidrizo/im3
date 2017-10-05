package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

public class AccNoteState extends OMRState {
    private Accidentals accidental;

    public AccNoteState(int number) {
        super(number, "accnote");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        super.onEnter(token, previousState, transduction);

        if (token.getValue() == null) {
            throw new IM3RuntimeException("Token value cannot be null");
        }
        // TODO: 5/10/17 Que el mapa de valores sea común con el generador de PRIMUS
        switch (token.getValue()) {
            case "b":
                accidental = Accidentals.FLAT;
                break;
            case "#":
                accidental = Accidentals.SHARP;
                break;
            case "n":
                accidental = Accidentals.NATURAL;
                break;
            default:
                transduction.setZeroProbability();
                break;
        }
    }

    public Accidentals getAccidental() {
        return accidental;
    }
}
