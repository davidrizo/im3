package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Clef;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class ClefState extends OMRState {
    public ClefState(int number) {
        super(number, "clef");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction)  {
        if (!(token.getSymbol() instanceof Clef)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an clef and found a " + token.getSymbol());
        }

        Clef symbol = (Clef) token.getSymbol();

        if (symbol.getClefNote() == null) {
            throw new IM3RuntimeException("Value of clef is null");
        }

        // TODO: 3/10/17 NotationType
        es.ua.dlsi.im3.core.score.Clef clef = null; // TODO: 3/10/17 Octave change
        try {
            clef = ImportFactories.createClef(transduction.getStaff().getNotationType(), symbol.getClefNote().name(), token.getPositionInStaff().getLine(), 0);
        } catch (ImportException e) {
            transduction.setZeroProbability();
        }

        // TODO: 3/10/17 Cálculo de la probabilidad - ej. que para G2 esté en la línea 5

        try {
            transduction.getStaff().addClef(clef);

        } catch (IM3Exception e) {
            throw new IM3RuntimeException(e);
        }
    }
}
