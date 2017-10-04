package es.ua.dlsi.im3.omr.language.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

import java.util.List;

public class ClefState extends OMRState {
    public ClefState(int number) {
        super(number, "clef");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) throws IM3Exception {
        if (!token.getSymbol().equals(GraphicalSymbol.clef)) {
            throw new IM3Exception("Expected a clef and found a " + token.getSymbol());
        }

        if (token.getValue() == null) {
            throw new IM3Exception("Value of clef is null");
        }

        // TODO: 3/10/17 NotationType
        Clef clef = ImportFactories.createClef(NotationType.eModern, token.getValue().toUpperCase(), token.getPositionInStaff().getLine(), 0); // TODO: 3/10/17 Octave change

        // TODO: 3/10/17 Cálculo de la probabilidad - ej. que para G2 esté en la línea 5

        transduction.getStaff().addClef(clef);
    }
}
