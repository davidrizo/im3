package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;

import java.io.*;
import java.util.HashMap;

public class MEI2GraphicSymbols {
    static final PositionInStaff CENTER_LINE = PositionInStaff.fromLine(3);
    public static final char SEPARATOR = '\t';
    public static final String LINE = "-L";
    public static final char LINE_SEPARATOR = '-';
    private static final char VALUE_SEPARATOR = '.';

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
            bw.write(convert(scoreSong));
            bw.close();
        } catch (IOException | IM3Exception e) {
            throw new ExportException(e);
        }
    }

    public String convert(ScoreSong scoreSong) throws IM3Exception {
        StringBuilder sb = null;

        if (scoreSong.getStaves().size() != 1) {
            //Note we don't have information in the MEI file about line breaking
            throw new ExportException("Currently only one staff is supported in the export format");
        }

        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        for (ITimedElementInStaff symbol : staff.getCoreSymbolsOrdered()) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(SEPARATOR);
            }

            Measure measure = null;
            if (scoreSong.hasMeasures()) {
                measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
            }
            if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                sb.append(GraphicalSymbol.barline);
                sb.append(LINE);
                sb.append(1);
                sb.append(SEPARATOR);
            }

            convert(sb, symbol, drawnAccidentals);
            lastMeasure = measure;
        }
        sb.append(SEPARATOR);
        sb.append(LINE);
        sb.append(1);
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/

        return sb.toString();
    }

    private void convertDuration(StringBuilder sb, AtomFigure figure) {
        sb.append(figure.getFigure().name().toLowerCase());

    }

    private void convertDots(StringBuilder sb, AtomFigure figure, PositionInStaff positionInStaff) {
        for (int i=0; i<figure.getDots(); i++) {
            if (i>0) {
                sb.append(SEPARATOR);
                sb.append(GraphicalSymbol.dot);
                sb.append(LINE_SEPARATOR);
                sb.append(positionInStaff.toString());
            }
        }

    }
    private void convert(StringBuilder sb, ITimedElementInStaff symbol, HashMap<AtomPitch, Accidentals> drawnAccidentals) throws IM3Exception {
        if (symbol instanceof Clef) {
            PositionInStaff positionInStaff = PositionInStaff.fromLine(((Clef) symbol).getLine());
            sb.append(GraphicalSymbol.clef);
            sb.append(LINE_SEPARATOR);
            sb.append(positionInStaff.toString());
        } else if (symbol instanceof KeySignature) {
            KeySignature ks = (KeySignature) symbol;
            PositionInStaff [] positions = ks.computePositionsOfAccidentals();
            if (positions != null) {
                boolean first = true;
                for (PositionInStaff position: positions) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(SEPARATOR);
                    }
                    sb.append(GraphicalSymbol.accidental);
                    sb.append(VALUE_SEPARATOR);
                    sb.append(ks.getAccidental().getAbbrName()); // FIXME: 24/9/17  Nombre como en PRIMUS
                    sb.append(LINE_SEPARATOR);
                    sb.append(position.toString());
                }
            }
        } else if (symbol instanceof TimeSignature) {
            if (symbol instanceof SignTimeSignature) {
                sb.append(GraphicalSymbol.number);
                sb.append(VALUE_SEPARATOR);
                sb.append(((SignTimeSignature)symbol).getSignString());
            } else if (symbol instanceof FractionalTimeSignature ){
                FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
                sb.append(GraphicalSymbol.number);
                sb.append(VALUE_SEPARATOR);
                sb.append(ts.getNumerator());
                sb.append(LINE);
                sb.append('4');
                sb.append(SEPARATOR);
                sb.append(GraphicalSymbol.number);
                sb.append(VALUE_SEPARATOR);
                sb.append(ts.getDenominator());
                sb.append(LINE);
                sb.append('2');
            } else {
                throw new ExportException("Unsupported time signature" + symbol.getClass());
            }

        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;
            PositionInStaff positionInStaff = symbol.getStaff().computePositionInStaff(note.getTime(),
                    note.getPitch().getPitchClass().getNoteName(), note.getPitch().getOctave());
            Accidentals accidentalToDraw = drawnAccidentals.get(note.getAtomPitch());
            if (accidentalToDraw != null) {
                sb.append(GraphicalSymbol.accidental);
                sb.append(VALUE_SEPARATOR);
                sb.append(accidentalToDraw.getAbbrName());
                sb.append(LINE_SEPARATOR);
                sb.append(positionInStaff.toString());
                sb.append(SEPARATOR);
            }
            sb.append(GraphicalSymbol.note);
            sb.append(VALUE_SEPARATOR);
            convertDuration(sb, note.getAtomFigure());
            sb.append(LINE_SEPARATOR);
            sb.append(positionInStaff.toString());

            if (positionInStaff.laysOnLine()) {
                positionInStaff = positionInStaff.move(1);
            }
            convertDots(sb, note.getAtomFigure(), positionInStaff);
        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            sb.append(GraphicalSymbol.rest);
            sb.append(VALUE_SEPARATOR);
            convertDuration(sb, rest.getAtomFigure());
            sb.append(LINE_SEPARATOR);
            sb.append(CENTER_LINE.toString());
            convertDots(sb, rest.getAtomFigure(), CENTER_LINE);
        } else {
            throw new ExportException("Unsupported symbol " + symbol.getClass());
        }
    }
}
