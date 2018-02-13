package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

// TODO: 5/10/17 Dos bb --> doble bemol También en el automáta
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
            case "flat":
                accidental = Accidentals.FLAT;
                break;
            case "sharp":
                accidental = Accidentals.SHARP;
                break;
            case "double_sharp":
                accidental = Accidentals.DOUBLE_SHARP;
                break;
            case "natural":
                accidental = Accidentals.NATURAL; //el becuadro no funciona ATENCION
                break;
            case "double_flat":
                accidental = Accidentals.DOUBLE_FLAT;
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
