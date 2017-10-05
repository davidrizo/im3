package es.ua.dlsi.im3.omr.language.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

public class NotesState extends OMRState {
    public NotesState(int number, String name) {
        super(number, "notes");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        Accidentals accidental = null;
        if (previousState instanceof AccNoteState) {
            accidental = ((AccNoteState)previousState).getAccidental();
        }

        // TODO: 5/10/17 Dots en la gramática - no aqui 
        if (token.getSymbol() == GraphicalSymbol.rest) {
            SimpleRest rest = new SimpleRest(parseFigure(token.getValue()), 0);
            try {
                transduction.getStaff().addCoreSymbol(rest);
                transduction.getLayer().add(rest);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
        } else if (token.getSymbol() == GraphicalSymbol.note) {
            // TODO: 5/10/17 Chords
            Staff staff = transduction.getStaff();
            Clef clef = transduction.getStaff().getLastClef();
            if (clef == null) {
                throw new IM3RuntimeException("No clef found to determine pitch");
            }
            ScientificPitch pitch = null;
            try {
                pitch = parsePitch(staff, clef, token.getPositionInStaff(), accidental);
            } catch (IM3Exception e) {
                transduction.setZeroProbability();
                return;
            }

            SimpleNote note = new SimpleNote(parseFigure(token.getValue()), 0, pitch);
            try {
                transduction.getStaff().addCoreSymbol(note);
                transduction.getLayer().add(note);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

        } else {
            throw new IM3RuntimeException("Symbol should be rest or note");
        }
    }

    private ScientificPitch parsePitch(Staff staff, Clef clef, PositionInStaff positionInStaff, Accidentals accidental) throws IM3Exception {
        ScientificPitch sp = staff.computeScientificPitch(clef, positionInStaff);
        if (accidental != null) {
            sp.getPitchClass().setAccidental(accidental);
        }
        return sp;
    }

    private Figures parseFigure(String value) {
        // TODO: 5/10/17 Valores válidos
        return Figures.valueOf(value.toUpperCase());
    }
}
