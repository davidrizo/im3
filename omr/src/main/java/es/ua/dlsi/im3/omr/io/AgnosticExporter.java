package es.ua.dlsi.im3.omr.io;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.SemanticSymbol;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;
import es.ua.dlsi.im3.omr.primus.conversions.ScoreGraphicalDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgnosticExporter {
    static final PositionInStaff CENTER_LINE = PositionInStaff.fromLine(3);
    private static final String START = "start";
    private static final String END = "end";
    private static final String ABOVE = "above";
    private static final String BELOW = "below";
    private static final String TRILL = "trill";
    private static final String FERMATA = "fermata";
    private static final PositionInStaff FERMATA_POSITION_ABOVE  = PositionsInStaff.SPACE_6;
    private static final PositionInStaff FERMATA_POSITION_BELOW = PositionsInStaff.SPACE_MINUS_1;
    private StringBuilder sb;

    public AgnosticExporter() {
    }

    public ScoreGraphicalDescription convert(ScoreSong scoreSong) throws IM3Exception {
        sb = new StringBuilder();
        ArrayList<GraphicalToken> graphicalTokens = new ArrayList<GraphicalToken>();
        ArrayList<SemanticToken> semanticTokens = new ArrayList<SemanticToken>();
        if (scoreSong.getStaves().size() != 1) {
            //Note we don't have information in the MEI file about line breaking
            throw new ExportException("Currently only one staff is supported in the export format");
        }

        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        List<ITimedElementInStaff> coreSymbolsOrdered = staff.getCoreSymbolsOrdered();
        Time lastEndTime = null;
        for (ITimedElementInStaff symbol : coreSymbolsOrdered) {
            Measure measure = null;
            if (scoreSong.hasMeasures()) {
                measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
            }
            if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
                semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
            }

            convert(graphicalTokens, semanticTokens, symbol, drawnAccidentals);

            if (symbol instanceof Atom) {
                lastEndTime = ((Atom) symbol).getOffset();
            }

            lastMeasure = measure;
        }

        if (lastEndTime == null) {
            throw new IM3Exception("Song without notes");
        }

        Time measureEndTime = staff.getRunningTimeSignatureAt(lastMeasure).getDuration().add(lastMeasure.getTime());
        if (lastEndTime.equals(measureEndTime)) {
            // add bar line just if the measure is complete
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
            semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
        }
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/

        return new ScoreGraphicalDescription(graphicalTokens, semanticTokens);
    }

    private void convertDuration(StringBuilder sb, AtomFigure figure) {
        sb.append(figure.getFigure().name().toLowerCase());

    }

    private void convertDots(List<GraphicalToken> graphicalTokens, AtomFigure figure, PositionInStaff positionInStaff) {
        for (int i = 0; i < figure.getDots(); i++) {
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.dot, null, positionInStaff));
        }
    }

    private void convert(ArrayList<GraphicalToken> graphicalTokens, ArrayList<SemanticToken> semanticTokens, ITimedElementInStaff symbol, HashMap<AtomPitch, Accidentals> drawnAccidentals) throws IM3Exception {
        if (symbol instanceof Clef) {
            PositionInStaff positionInStaff = PositionInStaff.fromLine(((Clef) symbol).getLine());
            Clef clef = (Clef) symbol;
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.clef, clef.getNote().name(), positionInStaff));
            semanticTokens.add(new SemanticToken(SemanticSymbol.clef, clef.getNote() + "" + clef.getLine()));
        } else if (symbol instanceof KeySignature) {
            KeySignature ks = (KeySignature) symbol;
            PositionInStaff[] positions = ks.computePositionsOfAccidentals();
            if (positions != null) {
                boolean first = true;
                for (PositionInStaff position : positions) {
                    graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, ks.getAccidental().name().toLowerCase(), position));
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ks.getConcertPitchKey().getPitchClass().toString());
            if (ks.getConcertPitchKey().getMode() != null && ks.getConcertPitchKey().getMode() != Mode.UNKNOWN) {
                sb.append(ks.getConcertPitchKey().getMode().getName());
            }
            semanticTokens.add(new SemanticToken(SemanticSymbol.keySignature, sb.toString()));

        } else if (symbol instanceof TimeSignature) {
            StringBuilder sb = new StringBuilder();
            if (symbol instanceof SignTimeSignature) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.metersign, ((SignTimeSignature) symbol).getSignString(), PositionsInStaff.LINE_3));
                sb.append(((SignTimeSignature) symbol).getSignString());
            } else if (symbol instanceof FractionalTimeSignature) {
                FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getNumerator()), PositionsInStaff.LINE_4));
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getDenominator()), PositionsInStaff.LINE_2));
                sb.append(ts.getNumerator());
                sb.append('/');
                sb.append(ts.getDenominator());
            } else {
                throw new ExportException("Unsupported time signature" + symbol.getClass());
            }
            semanticTokens.add(new SemanticToken(SemanticSymbol.timeSignature, sb.toString()));
        } else if (symbol instanceof SimpleChord) {
            throw new IM3Exception("Unsupported chords");
        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;

            if (note.getAtomFigure().getFermata() != null && (note.getAtomFigure().getFermata().getPosition() == null || note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, "above", PositionsInStaff.SPACE_6));
            }

            PositionInStaff positionInStaff = symbol.getStaff().computePositionInStaff(note.getTime(),
                    note.getPitch().getPitchClass().getNoteName(), note.getPitch().getOctave());

            if (note.getAtomPitch().isTiedFromPrevious()) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, END, positionInStaff));
            }

            Accidentals accidentalToDraw = drawnAccidentals.get(note.getAtomPitch());
            if (accidentalToDraw != null) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, accidentalToDraw.name().toLowerCase(), positionInStaff));
            }

            String figureString = generateFigureString(note);

            GraphicalSymbol graphicalSymbol;
            if (note.isGrace()) {
                graphicalSymbol = GraphicalSymbol.gracenote;
            } else {
                graphicalSymbol = GraphicalSymbol.note;
            }

            // TODO: 18/10/17 Otras marcas
            if (note.getMarks() != null) {
                for (StaffMark mark : note.getMarks()) {
                    if (mark instanceof Trill) {
                        graphicalTokens.add(new GraphicalToken(GraphicalSymbol.trill, null, FERMATA_POSITION_ABOVE));
                    } else {
                        throw new IM3Exception("Unsupported mark: " + mark.getClass());
                    }
                }
            }

            graphicalTokens.add(new GraphicalToken(graphicalSymbol, figureString, positionInStaff));

            if (note.getAtomFigure().getFermata() != null && note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPosition().toString().toLowerCase(),
                        FERMATA_POSITION_BELOW));
            }

            PositionInStaff dotPositionInStaff;
            if (positionInStaff.laysOnLine()) {
                dotPositionInStaff = positionInStaff.move(1);
            } else {
                dotPositionInStaff = positionInStaff;
            }
            convertDots(graphicalTokens, note.getAtomFigure(), dotPositionInStaff);

            if (note.getAtomPitch().isTiedToNext()) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, START, positionInStaff));
            }

            StringBuilder sb = new StringBuilder();
            sb.append(note.getPitch().toString());
            sb.append(SemanticToken.SUBVALUE_SEPARATOR);
            fillSingleFigureAtom(sb, note);
            SemanticSymbol semanticSymbol;
            if (note.isGrace()) {
                semanticSymbol = SemanticSymbol.gracenote;
            } else {
                semanticSymbol = SemanticSymbol.note;
            }

            semanticTokens.add(new SemanticToken(semanticSymbol, sb.toString()));

            if (note.getAtomPitch().isTiedToNext()) {
                semanticTokens.add(new SemanticToken(SemanticSymbol.tie));
            }

        } else if (symbol instanceof SimpleMultiMeasureRest) {
            SimpleMultiMeasureRest multiMeasureRest = (SimpleMultiMeasureRest) symbol;
            int n = multiMeasureRest.getNumMeasures();
            ArrayList<Integer> digits = new ArrayList<Integer>();
            while (n > 0) {
                int digit = n % 10;
                n /= 10;
                digits.add(0, digit);
            }
            for (Integer digit : digits) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(digit), PositionsInStaff.SPACE_5));
            }
            if (multiMeasureRest.getAtomFigure().getFermata() != null && multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
            }

            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.multirest, null, PositionsInStaff.LINE_3));

            if (multiMeasureRest.getAtomFigure().getFermata() != null && (multiMeasureRest.getAtomFigure().getFermata().getPosition() == null || multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW)) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), FERMATA_POSITION_BELOW));
            }

            semanticTokens.add(new SemanticToken(SemanticSymbol.multirest, Integer.toString(multiMeasureRest.getNumMeasures())));


        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            if (rest.getAtomFigure().getFermata() != null && (rest.getAtomFigure().getFermata().getPosition() == null || rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
            }

            PositionInStaff positionsInStaff;
            if (rest.getAtomFigure().getFigure() == Figures.WHOLE) {
                positionsInStaff = PositionsInStaff.LINE_4;
            } else {
                positionsInStaff = PositionsInStaff.LINE_3;
            }
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.rest, rest.getAtomFigure().getFigure().toString().toLowerCase(), positionsInStaff));

            if (rest.getAtomFigure().getFermata() != null && rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), FERMATA_POSITION_BELOW));
            }


            convertDots(graphicalTokens, rest.getAtomFigure(), PositionsInStaff.SPACE_3);

            StringBuilder sb = new StringBuilder();
            fillSingleFigureAtom(sb, rest);
            semanticTokens.add(new SemanticToken(SemanticSymbol.rest, sb.toString()));
        } else {
            throw new ExportException("Unsupported symbol conversion of: " + symbol.getClass());
        }
    }

    private void fillSingleFigureAtom(StringBuilder sb, SingleFigureAtom atom) throws IM3Exception {
        sb.append(atom.getAtomFigure().getFigure().name().toLowerCase());
        for (int i = 0; i < atom.getAtomFigure().getDots(); i++) {
            sb.append('.');
        }
        if (atom.getAtomFigure().getFermata() != null) {
            sb.append(SemanticToken.SUBVALUE_SEPARATOR);
            sb.append(FERMATA);
        }
        // TODO: 10/11/17 Otras marcas
        if (atom.getMarks() != null) {
            for (StaffMark mark : atom.getMarks()) {
                if (mark instanceof Trill) {
                    sb.append(SemanticToken.SUBVALUE_SEPARATOR);
                    sb.append(TRILL);
                } else {
                    throw new IM3Exception("Unsupported mark: " + mark.getClass());
                }
            }
        }
    }

    private String generateFigureString(SimpleNote note) {
        BeamGroup beam = note.getBelongsToBeam();
        if (beam != null) {
            int flags = note.getAtomFigure().getFigure().getNumFlags();
            if (beam.getFirstFigure() == note) {
                return "beamedRight" + flags;
            } else if (beam.getLastFigure() == note) {
                return "beamedLeft" + flags;
            } else {
                return "beamedBoth" + flags;
            }
        } else {
            return note.getAtomFigure().getFigure().toString().toLowerCase();
        }
    }



}