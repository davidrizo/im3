package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class NotesState extends OMRState {
    private static final String BEAMED = "BEAMED";

    public NotesState(int number, String name) {
        super(number, "notes");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        Accidentals accidental = null;
        if (previousState instanceof AccNoteState) {
            accidental = ((AccNoteState)previousState).getAccidental(); //Ojo, si en el mismo compas hay otra nota alterada no lo ve
        }

        // TODO: 5/10/17 Dots en la gramática - no aqui 
       if ((token.getSymbol() == GraphicalSymbol.note)) {
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

            try {
                SimpleNote note = new SimpleNote(parseFigure(token.getValue()), 0, pitch);
                transduction.getStaff().addCoreSymbol(note);
                transduction.getLayer().add(note);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

        /*} else {
            throw new IM3RuntimeException("Symbol should be note");*/
        }
        else {
           if (token.getSymbol() == GraphicalSymbol.dot && previousState.toString() == "notes") { //TODO como lo agrego

               /*System.out.println("El estado anterior es:");
               System.out.println(previousState.toString());
               Staff staff = transduction.getStaff();
               Clef clef = transduction.getStaff().getLastClef();
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
               }*/

               //TODO dobles puntillos ¿Otro estado?
           }
           else {
               throw new IM3RuntimeException("Symbol should be note or dot");
           }
       }
    }

    private ScientificPitch parsePitch(Staff staff, Clef clef, PositionInStaff positionInStaff, Accidentals accidental) throws IM3Exception {
        ScientificPitch sp = staff.computeScientificPitch(clef, positionInStaff);
        if (accidental != null) {
            sp.getPitchClass().setAccidental(accidental);
        }
        return sp;
    }

    private Figures parseFigure(String value) throws IM3Exception {
        String upperCaseValue = value.toUpperCase();
        if (upperCaseValue.startsWith(BEAMED)) { // it is a beam
            String beams = value.substring(value.length()-1); // we'll not have more than 9 beams
            int nbeams;
            try {
                nbeams = Integer.parseInt(beams);
            } catch (Throwable t) {
                throw new IM3RuntimeException("Invalid beam number: '" + beams + "' for value " + value);
            }
            return Figures.findFigureWithFlags(nbeams, NotationType.eModern);
        } else { // it is a figure
            return Figures.valueOf(upperCaseValue);
        }
    }
}
