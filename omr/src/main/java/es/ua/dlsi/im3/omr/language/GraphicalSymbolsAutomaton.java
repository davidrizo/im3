package es.ua.dlsi.im3.omr.language;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.*;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.language.states.ClefState;
import es.ua.dlsi.im3.omr.language.states.KeySignatureState;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

/**
 * Deterministic probabilistic automaton that models a staff
 */
public class GraphicalSymbolsAutomaton {
    private final NotationType notationType;
    protected DeterministicProbabilisticAutomaton<State, GraphicalSymbol, OMRTransduction> dpa;

    public GraphicalSymbolsAutomaton(NotationType notationType) throws IM3Exception {
        this.notationType = notationType;
    }

    public DeterministicProbabilisticAutomaton<State, GraphicalSymbol, OMRTransduction> getDeterministicProbabilisticAutomaton() {
        return dpa;
    }

    public OMRTransduction probabilityOf(List<GraphicalToken> sequence) throws IM3Exception {
        return dpa.probabilityOf(sequence, new OMRTransductionFactory(notationType));
    }
}
