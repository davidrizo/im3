package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.language.modern.states.OMRState;

public class AccNoteState extends OMRState {
    private Accidentals accidental;

    public AccNoteState(int number) {
        super(number, "accnote");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        super.onEnter(token, previousState, transduction);

        if (!(token.getSymbol() instanceof es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidental)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an accidental and found a " + token.getSymbol());
        }
        es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidental symbol = (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidental) token.getSymbol();

        if (symbol.getAccidentals() == null) {
            throw new IM3RuntimeException("Token value cannot be null");
        }
        // TODO: 2/3/18 Alteraciones que anulen las anteriores
        switch (symbol.getAccidentals()) {
            case flat: // double flats are encoded as two symbols
                accidental = Accidentals.FLAT;
                break;
            case sharp:
                accidental = Accidentals.SHARP;
                break;
            case double_sharp:
                accidental = Accidentals.DOUBLE_SHARP;
                break;
            case natural:
                accidental = Accidentals.NATURAL; //el becuadro no funciona ATENCION
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
