package es.ua.dlsi.im3.omr.language.modern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.DeterministicProbabilisticAutomaton;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.adt.dfa.Transition;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolAlphabet;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolsAutomaton;
//import es.ua.dlsi.im3.omr.language.mensural.states.EndBarState;
import es.ua.dlsi.im3.omr.language.modern.states.*;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Modified by Jorge Aracil
 * Versión depositada
 */
public class GraphicalModernSymbolsAutomaton extends GraphicalSymbolsAutomaton {
    public GraphicalModernSymbolsAutomaton() throws IM3Exception {
        super(NotationType.eModern);
        HashSet<State> states = new HashSet<>();
        State start = new State(1, "start");
        State clef = new ClefState(2);
        State keysig = new KeySignatureState(3);
        State timesig = new TimeSignatureState(4);
        State noteacc = new AccNoteState(6);
        State notes = new NotesState(7, "notes");
        State endbar = new EndBarState(8);
        State rest =  new RestState(9, "rest");
        State slurnote = new SlurNoteState(10, "slurnote");
        State slurnoteend = new SlurNoteEndState(11, "slurnoteend");
        State slurbarlinenote = new SlurBarlineNoteState(12, "slurbarlinenote");
        State digittimesignature = new DigitTimeSignatureState(13, "digittimesignature");
        State fermata = new FermataState(14, "fermata");
        State multirestdigit = new MultirestDigitState(15,"multirestdigit");
        State multirest = new MultirestState(16,"multirest");
        State trill = new TrillState(17,"trill");
        State gracenote = new GracenoteState(18, "gracenote");

        states.add(start);
        states.add(clef);
        states.add(keysig);
        states.add(timesig);
        states.add(noteacc);
        states.add(notes);
        states.add(endbar);
        states.add(rest);
        states.add(slurnote);
        states.add(slurnoteend);
        states.add(slurbarlinenote);
        states.add(digittimesignature);
        states.add(fermata);
        states.add(multirestdigit);
        states.add(multirest);
        states.add(trill);
        states.add(gracenote);

        HashMap<State, Fraction> endStates = new HashMap<>();
        endStates.put(notes, Fraction.ONE_THIRD);
        endStates.put(endbar, Fraction.TWO_THIRDS);

        HashSet<Transition<State, AgnosticSymbolType>> transitions = new HashSet<>();

        transitions.add(new Transition<>(start, new Clef(), clef));

        transitions.add(new Transition<>(clef, new MeterSign(), timesig));

        transitions.add(new Transition<>(clef, new Accidental(), keysig));

        transitions.add(new Transition<>(clef, new Digit(), digittimesignature));

        transitions.add(new Transition<>(keysig, new Accidental(), keysig));
        transitions.add(new Transition<>(keysig, new Digit(), digittimesignature));
        transitions.add(new Transition<>(keysig, new MeterSign(), timesig));

        transitions.add(new Transition<>(digittimesignature, new Digit(), timesig));


        transitions.add(new Transition<>(timesig, new Rest(), rest));
        transitions.add(new Transition<>(timesig, new Note(), notes));
        transitions.add(new Transition<>(timesig, new Accidental(), noteacc));

        transitions.add(new Transition<>(timesig, new Digit(), multirestdigit));
        transitions.add(new Transition<>(multirestdigit, new Digit(), multirestdigit));
        transitions.add(new Transition<>(multirestdigit, new Multirest(), multirest));
        transitions.add(new Transition<>(multirest, new VerticalLine(), endbar));

        transitions.add(new Transition<>(notes, new Rest(), rest));
        transitions.add(new Transition<>(notes, new Note(), notes));

        transitions.add(new Transition<>(notes, new Accidental(), noteacc));
        transitions.add(new Transition<>(notes, new Dot(), notes));

        transitions.add(new Transition<>(notes, new Slur(), slurnote));
        transitions.add(new Transition<>(slurnote, new Slur(), slurnoteend));
        transitions.add(new Transition<>(slurnoteend, new Note(),notes));
        transitions.add(new Transition<>(slurnote, new VerticalLine(), slurbarlinenote));

        transitions.add(new Transition<>(slurbarlinenote, new Slur(), slurnoteend));


        transitions.add(new Transition<>(noteacc, new Note(), notes));
        transitions.add(new Transition<>(rest, new Accidental(), noteacc));
        transitions.add(new Transition<>(endbar, new Accidental(), noteacc));

        transitions.add(new Transition<>(rest, new Rest(), rest));
        transitions.add(new Transition<>(rest, new Note(), notes));
        transitions.add(new Transition<>(rest, new VerticalLine(), endbar));
        transitions.add(new Transition<>(rest, new Dot(), rest));
        transitions.add(new Transition<>(rest, new Accidental(), noteacc));

        transitions.add(new Transition<>(notes, new VerticalLine(), endbar));

        transitions.add(new Transition<>(timesig, new Fermata(), fermata));
        transitions.add(new Transition<>(rest, new Fermata(), fermata));
        transitions.add(new Transition<>(notes, new Fermata(), fermata));
        transitions.add(new Transition<>(endbar, new Fermata(), fermata));
        transitions.add(new Transition<>(fermata, new VerticalLine(), endbar));
        transitions.add(new Transition<>(fermata, new Rest(), rest));
        transitions.add(new Transition<>(fermata, new Note(), notes));
        transitions.add(new Transition<>(fermata, new Accidental(), noteacc));
        transitions.add(new Transition<>(noteacc, new Accidental(), noteacc)); // for double flat

        transitions.add(new Transition<>(trill, new Accidental(), noteacc));
        transitions.add(new Transition<>(trill, new Note(), notes));
        transitions.add(new Transition<>(endbar, new Trill(), trill));
        transitions.add(new Transition<>(rest, new Trill(), trill));
        transitions.add(new Transition<>(notes, new Trill(), trill));
        transitions.add(new Transition<>(timesig, new Trill(), trill));
        transitions.add(new Transition<>(noteacc,new Trill(), trill));

        transitions.add(new Transition<>(timesig, new GraceNote(), gracenote));
        transitions.add(new Transition<>(notes, new GraceNote(), gracenote));
        transitions.add(new Transition<>(rest, new GraceNote(), gracenote));
        transitions.add(new Transition<>(endbar, new GraceNote(), gracenote));
        transitions.add(new Transition<>(gracenote, new Note(), notes ));

        transitions.add(new Transition<>(endbar, new Rest(), rest));
        transitions.add(new Transition<>(endbar, new Note(), notes));
        transitions.add(new Transition<>(endbar, new Digit(), multirestdigit));


        GraphicalSymbolAlphabet alphabet = new GraphicalSymbolAlphabet();
        dpa = new DeterministicProbabilisticAutomaton(states, start,endStates, alphabet, transitions);
        dpa.normalizeProbabilities();
    }
}
