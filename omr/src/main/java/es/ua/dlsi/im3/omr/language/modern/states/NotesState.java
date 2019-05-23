package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class NotesState extends OMRState {
    private static final String BEAMED = "BEAMED";

    public NotesState(int number, String name) {
        super(number, "notes");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        Accidentals accidental = null;
        if (previousState instanceof AccNoteState) {
            accidental = ((AccNoteState)previousState).getAccidental(); //Ojo, si en el mismo compas hay otra nota alterada no lo ve
        }

        // TODO: 5/10/17 Dots en la gramática - no aqui
        if (token.getSymbol() instanceof Note) {
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
                Note value = ((Note) token.getSymbol());
                SimpleNote note = new SimpleNote(parseFigure(value.getDurationSpecification()), 0, pitch);
                //20190522 transduction.getStaff().addElementWithoutLayer(note);
                transduction.getLayer().add(note);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

        /*} else {
            throw new IM3RuntimeException("Symbol should be note");*/
        }
        else if (token.getSymbol() instanceof Dot && previousState.getName().equals("notes")) {
               //TODO En futuras implementaciones se cambiara la forma de añadir notas a partituras
        }
        else {
               throw new IM3RuntimeException("Symbol should be note or dot from previous state 'notes', and it is a " + token.getSymbol() +
               " coming from state " + previousState.getName());
       }
    }

    private ScientificPitch parsePitch(Staff staff, Clef clef, PositionInStaff positionInStaff, Accidentals accidental) throws IM3Exception {
        ScientificPitch sp = staff.computeScientificPitch(clef, positionInStaff);
        if (accidental != null) {
            sp.getPitchClass().setAccidental(accidental);
        }
        return sp;
    }

    private Figures parseFigure(INoteDurationSpecification durationSpecification) throws IM3Exception {
        if (durationSpecification instanceof Beam) {
            Beam beam = (Beam) durationSpecification;
            return Figures.findFigureWithFlags(beam.getBeams(), NotationType.eModern);
        } else if (durationSpecification instanceof NoteFigures) {
            NoteFigures noteFigures = (NoteFigures) durationSpecification;
            return convert(noteFigures);
        } else {
            throw new IM3Exception("Unsupported durationSpecification: " + durationSpecification);
        }

        /*String upperCaseValue = value.toUpperCase();
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
        }*/
    }

    private Figures convert(NoteFigures noteFigures) {
        switch (noteFigures) {
            case hundredTwentyEighth:
                return Figures.HUNDRED_TWENTY_EIGHTH;
            case sixtyFourth:
                return Figures.SIXTY_FOURTH;
            case thirtySecond:
                return Figures.THIRTY_SECOND;
            case twoHundredFiftySix:
                return Figures.TWO_HUNDRED_FIFTY_SIX;
            default:
                return Figures.valueOf(noteFigures.toAgnosticString().toUpperCase());
    }
    }
}
