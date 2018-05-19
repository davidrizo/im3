package es.ua.dlsi.im3.omr.language.mensural;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.DeterministicProbabilisticAutomaton;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.adt.dfa.Transition;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolAlphabet;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolsAutomaton;
import es.ua.dlsi.im3.omr.language.mensural.states.*;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.HashSet;

public class GraphicalMensuralSymbolsAutomaton extends GraphicalSymbolsAutomaton {
   // AgnosticVersion agnosticVersion = AgnosticVersion.v1;

    public GraphicalMensuralSymbolsAutomaton() throws IM3Exception {
        super(NotationType.eModern);
        HashSet<State> states = new HashSet<>();
        State start = new State(1, "start");
        State clef = new ClefState(2);
        State keysig = new KeySignatureState(3);
        State timesig = new TimeSignatureState(4);
        State noteacc = new AccNoteState(6);
        State notes = new NotesState(7, "notes");
        State barline = new BarLineState(8);
        states.add(start);
        states.add(clef);
        states.add(keysig);
        states.add(timesig);
        states.add(noteacc);
        states.add(notes);
        states.add(barline);

        HashMap<State, Fraction> endStates = new HashMap<>();
        endStates.put(notes, Fraction.ONE_THIRD);
        endStates.put(barline, Fraction.TWO_THIRDS);

        HashSet<Transition<State, AgnosticSymbolType>> transitions = new HashSet<>();
        transitions.add(new Transition<>(start, new Clef(), clef));
        transitions.add(new Transition<>(clef, new Accidental(), keysig));
        transitions.add(new Transition<>(keysig, new Accidental(), keysig));
        transitions.add(new Transition<>(clef, new MeterSign(), timesig));
        transitions.add(new Transition<>(keysig, new MeterSign(), timesig));

        transitions.add(new Transition<>(timesig, new Rest(), notes));

        transitions.add(new Transition<>(timesig, new Accidental(), noteacc));
        transitions.add(new Transition<>(notes, new Accidental(), noteacc));
        transitions.add(new Transition<>(barline, new Accidental(), noteacc));

        transitions.add(new Transition<>(notes, new Rest(), notes));
        transitions.add(new Transition<>(notes, new Note(), notes));
        transitions.add(new Transition<>(barline, new Note(), notes));
        transitions.add(new Transition<>(barline, new Rest(), notes));
        transitions.add(new Transition<>(noteacc, new Note(), notes));
        transitions.add(new Transition<>(notes, new VerticalLine(), barline));


        GraphicalSymbolAlphabet alphabet = new GraphicalSymbolAlphabet();
        dpa = new DeterministicProbabilisticAutomaton(states, start,endStates, alphabet, transitions);
        dpa.normalizeProbabilities();
    }
}
