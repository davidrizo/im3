package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MEI2GraphicSymbols {
    static final PositionInStaff CENTER_LINE = PositionInStaff.fromLine(3);
    public static final char SEPARATOR = '\t';
    /**
     *
     * @param inputFile MEI File
     * @param outputFile
     */
    public void convert(File inputFile, File outputFile) throws ImportException, ExportException {
        MEISongImporter importer = new MEISongImporter();
        ScoreSong scoreSong = importer.importSong(inputFile);

        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            List<GraphicalToken> graphicalTokens = convert(scoreSong);
            for (int i = 0; i< graphicalTokens.size(); i++) {
                if (i>0) {
                    bw.write(SEPARATOR);
                }
                bw.write(graphicalTokens.get(i).toString());
            }
            bw.close();
        } catch (IOException | IM3Exception e) {
            throw new ExportException(e);
        }
    }

    public List<GraphicalToken> convert(ScoreSong scoreSong) throws IM3Exception {
        ArrayList<GraphicalToken> graphicalTokens = new ArrayList<>();
        if (scoreSong.getStaves().size() != 1) {
            //Note we don't have information in the MEI file about line breaking
            throw new ExportException("Currently only one staff is supported in the export format");
        }

        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        for (ITimedElementInStaff symbol : staff.getCoreSymbolsOrdered()) {

            Measure measure = null;
            if (scoreSong.hasMeasures()) {
                measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
            }
            if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1));
            }

            convert(graphicalTokens, symbol, drawnAccidentals);
            lastMeasure = measure;
        }
        graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PossitionsInStaff.LINE_1));
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/

        return graphicalTokens;
    }

    private void convertDuration(StringBuilder sb, AtomFigure figure) {
        sb.append(figure.getFigure().name().toLowerCase());

    }

    private void convertDots(List<GraphicalToken> graphicalTokens, AtomFigure figure, PositionInStaff positionInStaff) {
        for (int i=0; i<figure.getDots(); i++) {
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.dot, null, positionInStaff));
        }

    }
    private void convert(ArrayList<GraphicalToken> graphicalTokens, ITimedElementInStaff symbol, HashMap<AtomPitch, Accidentals> drawnAccidentals) throws IM3Exception {
        if (symbol instanceof Clef) {
            PositionInStaff positionInStaff = PositionInStaff.fromLine(((Clef) symbol).getLine());
            Clef clef = (Clef) symbol;
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.clef, clef.getNote().name(), positionInStaff));
        } else if (symbol instanceof KeySignature) {
            KeySignature ks = (KeySignature) symbol;
            PositionInStaff [] positions = ks.computePositionsOfAccidentals();
            if (positions != null) {
                boolean first = true;
                for (PositionInStaff position: positions) {
                    graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, ks.getAccidental().getAbbrName(), position));
                }
            }
        } else if (symbol instanceof TimeSignature) {
            if (symbol instanceof SignTimeSignature) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.text, ((SignTimeSignature)symbol).getSignString(), PossitionsInStaff.LINE_3));
            } else if (symbol instanceof FractionalTimeSignature ){
                FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.text, Integer.toString(ts.getNumerator()), PossitionsInStaff.LINE_4));
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.text, Integer.toString(ts.getDenominator()), PossitionsInStaff.LINE_2));
            } else {
                throw new ExportException("Unsupported time signature" + symbol.getClass());
            }

        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;
            PositionInStaff positionInStaff = symbol.getStaff().computePositionInStaff(note.getTime(),
                    note.getPitch().getPitchClass().getNoteName(), note.getPitch().getOctave());
            Accidentals accidentalToDraw = drawnAccidentals.get(note.getAtomPitch());
            if (accidentalToDraw != null) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, accidentalToDraw.getAbbrName(), positionInStaff));
            }
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.note, note.getAtomFigure().getFigure().toString(), positionInStaff));

            if (positionInStaff.laysOnLine()) {
                positionInStaff = positionInStaff.move(1);
            }
            convertDots(graphicalTokens, note.getAtomFigure(), positionInStaff);
        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.rest, rest.getAtomFigure().getFigure().toString(), PossitionsInStaff.LINE_3));
            convertDots(graphicalTokens, rest.getAtomFigure(), PossitionsInStaff.LINE_3);
        } else {
            throw new ExportException("Unsupported symbol conversion of: " + symbol.getClass());
        }
    }
}
