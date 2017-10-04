package es.ua.dlsi.im3.omr.language;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.*;
import es.ua.dlsi.im3.omr.language.states.ClefState;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

/**
 * Deterministic probabilistic automaton that models a staff
 */
public class GraphicalSymbolsAutomaton {
    DeterministicProbabilisticAutomaton<State, GraphicalSymbol> dpa;

    public GraphicalSymbolsAutomaton() throws IM3Exception {
        HashSet<State> states = new HashSet<>();
        State start = new State(1, "start");
        State clef = new ClefState(2);
        State keysig = new State(3, "keysig");
        State timesig1 = new State(4, "timesig1");
        State timesig2 = new State(5, "timesig2");
        State noteacc = new State(6, "accnote");
        State notes = new State(7, "notes");
        State endbar = new State(8, "endbar");
        states.add(start);
        states.add(clef);
        states.add(keysig);
        states.add(timesig1);
        states.add(timesig2);
        states.add(noteacc);
        states.add(notes);
        states.add(endbar);

        HashMap<State, Fraction> endStates = new HashMap<>();
        endStates.put(notes, Fraction.ONE_THIRD);
        endStates.put(endbar, Fraction.TWO_THIRDS);

        HashSet<Transition<State, GraphicalSymbol>> transitions = new HashSet<>();
        transitions.add(new Transition<>(start, GraphicalSymbol.clef, clef));
        transitions.add(new Transition<>(clef, GraphicalSymbol.accidental, keysig));
        transitions.add(new Transition<>(keysig, GraphicalSymbol.accidental, keysig));
        transitions.add(new Transition<>(clef, GraphicalSymbol.text, timesig1));
        transitions.add(new Transition<>(keysig, GraphicalSymbol.text, timesig1));
        transitions.add(new Transition<>(timesig1, GraphicalSymbol.text, timesig2));

        transitions.add(new Transition<>(timesig1, GraphicalSymbol.rest, notes));
        transitions.add(new Transition<>(timesig2, GraphicalSymbol.note, notes));

        transitions.add(new Transition<>(timesig1, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(timesig2, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(notes, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.accidental, noteacc));

        transitions.add(new Transition<>(notes, GraphicalSymbol.rest, notes));
        transitions.add(new Transition<>(notes, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.rest, notes));
        transitions.add(new Transition<>(noteacc, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(notes, GraphicalSymbol.barline, endbar));


        GraphicalSymbolAlphabet alphabet = new GraphicalSymbolAlphabet();
        dpa = new DeterministicProbabilisticAutomaton(states, start,endStates, alphabet, transitions);
        dpa.normalizeProbabilities();
    }

    public DeterministicProbabilisticAutomaton<State, GraphicalSymbol> getDeterministicProbabilisticAutomaton() {
        return dpa;
    }

    public Transduction probabilityOf(List<GraphicalToken> sequence) throws IM3Exception {
        return dpa.probabilityOf(sequence);
    }
}
