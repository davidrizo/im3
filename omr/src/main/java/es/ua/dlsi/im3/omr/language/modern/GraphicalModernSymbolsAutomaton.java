package es.ua.dlsi.im3.omr.language.modern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.DeterministicProbabilisticAutomaton;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.adt.dfa.Transition;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolAlphabet;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolsAutomaton;
//import es.ua.dlsi.im3.omr.language.mensural.states.EndBarState;
import es.ua.dlsi.im3.omr.language.modern.states.*;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Modified by Jorge Aracil
 * Versión 1
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

        HashSet<Transition<State, GraphicalSymbol>> transitions = new HashSet<>();

        transitions.add(new Transition<>(start, GraphicalSymbol.clef, clef));

        transitions.add(new Transition<>(clef, GraphicalSymbol.metersign, timesig));

        transitions.add(new Transition<>(clef, GraphicalSymbol.accidental, keysig));

        transitions.add(new Transition<>(clef, GraphicalSymbol.digit, digittimesignature));

        transitions.add(new Transition<>(keysig, GraphicalSymbol.accidental, keysig));
        transitions.add(new Transition<>(keysig, GraphicalSymbol.digit, digittimesignature));
        transitions.add(new Transition<>(keysig, GraphicalSymbol.metersign, timesig));

        transitions.add(new Transition<>(digittimesignature,GraphicalSymbol.digit, timesig));


        transitions.add(new Transition<>(timesig, GraphicalSymbol.rest, rest));
        transitions.add(new Transition<>(timesig, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(timesig, GraphicalSymbol.accidental, noteacc));

        transitions.add(new Transition<>(timesig, GraphicalSymbol.digit, multirestdigit));
        transitions.add(new Transition<>(multirestdigit, GraphicalSymbol.digit, multirestdigit));
        transitions.add(new Transition<>(multirestdigit,GraphicalSymbol.multirest, multirest));
        transitions.add(new Transition<>(multirest, GraphicalSymbol.barline, endbar));

        transitions.add(new Transition<>(notes, GraphicalSymbol.rest, rest));
        transitions.add(new Transition<>(notes, GraphicalSymbol.note, notes));

        transitions.add(new Transition<>(notes, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(notes, GraphicalSymbol.dot, notes));

        transitions.add(new Transition<>(notes,GraphicalSymbol.slur, slurnote));
        transitions.add(new Transition<>(slurnote, GraphicalSymbol.slur, slurnoteend));
        transitions.add(new Transition<>(slurnoteend,GraphicalSymbol.note,notes));
        transitions.add(new Transition<>(slurnote, GraphicalSymbol.barline, slurbarlinenote));

        transitions.add(new Transition<>(slurbarlinenote, GraphicalSymbol.slur, slurnoteend));


        transitions.add(new Transition<>(noteacc, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(rest, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.accidental, noteacc));

        transitions.add(new Transition<>(rest, GraphicalSymbol.rest, rest));
        transitions.add(new Transition<>(rest, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(rest, GraphicalSymbol.barline, endbar));
        transitions.add(new Transition<>(rest, GraphicalSymbol.dot, rest));
        transitions.add(new Transition<>(rest, GraphicalSymbol.accidental, noteacc));

        transitions.add(new Transition<>(notes, GraphicalSymbol.barline, endbar));

        transitions.add(new Transition<>(timesig, GraphicalSymbol.fermata, fermata));
        transitions.add(new Transition<>(rest, GraphicalSymbol.fermata, fermata));
        transitions.add(new Transition<>(notes, GraphicalSymbol.fermata, fermata));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.fermata, fermata));
        transitions.add(new Transition<>(fermata, GraphicalSymbol.barline, endbar));
        transitions.add(new Transition<>(fermata, GraphicalSymbol.rest, rest));
        transitions.add(new Transition<>(fermata, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(fermata, GraphicalSymbol.accidental, noteacc));

        transitions.add(new Transition<>(trill, GraphicalSymbol.accidental, noteacc));
        transitions.add(new Transition<>(trill, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.trill, trill));
        transitions.add(new Transition<>(rest, GraphicalSymbol.trill, trill));
        transitions.add(new Transition<>(notes, GraphicalSymbol.trill, trill));
        transitions.add(new Transition<>(timesig, GraphicalSymbol.trill, trill));
        transitions.add(new Transition<>(noteacc,GraphicalSymbol.trill, trill));

        transitions.add(new Transition<>(timesig,GraphicalSymbol.gracenote, gracenote));
        transitions.add(new Transition<>(notes, GraphicalSymbol.gracenote, gracenote));
        transitions.add(new Transition<>(rest, GraphicalSymbol.gracenote, gracenote));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.gracenote, gracenote));
        transitions.add(new Transition<>(gracenote, GraphicalSymbol.note, notes ));

        transitions.add(new Transition<>(endbar, GraphicalSymbol.rest, rest));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.note, notes));
        transitions.add(new Transition<>(endbar, GraphicalSymbol.digit, multirestdigit));


        GraphicalSymbolAlphabet alphabet = new GraphicalSymbolAlphabet();
        dpa = new DeterministicProbabilisticAutomaton(states, start,endStates, alphabet, transitions);
        dpa.normalizeProbabilities();
    }
}
