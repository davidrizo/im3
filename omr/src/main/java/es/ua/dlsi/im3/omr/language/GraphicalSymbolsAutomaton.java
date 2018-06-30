package es.ua.dlsi.im3.omr.language;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.*;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;

import java.util.*;

/**
 * Deterministic probabilistic automaton that models a staff
 */
public class GraphicalSymbolsAutomaton {
    private final NotationType notationType;
    protected DeterministicProbabilisticAutomaton<State, AgnosticSymbolType, OMRTransduction> dpa;

    public GraphicalSymbolsAutomaton(NotationType notationType) {
        this.notationType = notationType;
    }

    public DeterministicProbabilisticAutomaton<State, AgnosticSymbolType, OMRTransduction> getDeterministicProbabilisticAutomaton() {
        return dpa;
    }

    public OMRTransduction probabilityOf(List<AgnosticToken> sequence, boolean debug) throws IM3Exception {
        dpa.setDebug(debug);
        return dpa.probabilityOf(sequence, new OMRTransductionFactory(notationType));
    }
    public OMRTransduction probabilityOf(List<AgnosticToken> sequence) throws IM3Exception {
        dpa.setDebug(false);
        return dpa.probabilityOf(sequence, new OMRTransductionFactory(notationType));
    }
}
