package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MEI2GraphicSymbols {
    static final PositionInStaff CENTER_LINE = PositionInStaff.fromLine(3);
    public static final char SEPARATOR = '\t';
    public static final String START = "start";
    public static final String END = "end";
    public static final String ABOVE = "above";
    public static final String BELOW = "below";
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
            List<GraphicalToken> graphicalTokens = convert(scoreSong).getTokens();
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

    public ScoreGraphicalDescription convert(ScoreSong scoreSong) throws IM3Exception {
        ArrayList<GraphicalToken> graphicalTokens = new ArrayList<>();
        if (scoreSong.getStaves().size() != 1) {
            //Note we don't have information in the MEI file about line breaking
            throw new ExportException("Currently only one staff is supported in the export format");
        }

        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        List<ITimedElementInStaff> coreSymbolsOrdered = staff.getCoreSymbolsOrdered();
        Time lastEndTime = null;
        for (ITimedElementInStaff symbol: coreSymbolsOrdered) {
            Measure measure = null;
            if (scoreSong.hasMeasures()) {
                measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
            }
            if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
            }

            convert(graphicalTokens, symbol, drawnAccidentals);

            if (symbol instanceof Atom) {
                lastEndTime = ((Atom)symbol).getOffset();
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
        }
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/

        return new ScoreGraphicalDescription(graphicalTokens);
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
                    graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, ks.getAccidental().name().toLowerCase(), position));
                }
            }
        } else if (symbol instanceof TimeSignature) {
            if (symbol instanceof SignTimeSignature) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.metersign, ((SignTimeSignature)symbol).getSignString(), PositionsInStaff.LINE_3));
            } else if (symbol instanceof FractionalTimeSignature ){
                FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getNumerator()), PositionsInStaff.LINE_4));
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getDenominator()), PositionsInStaff.LINE_2));
            } else {
                throw new ExportException("Unsupported time signature" + symbol.getClass());
            }

        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;

            if (note.getAtomFigure().getFermata() != null && note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
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
                        graphicalTokens.add(new GraphicalToken(GraphicalSymbol.trill, null, null));
                    } else {
                        throw new IM3Exception("Unsupported mark: " + mark.getClass());
                    }
                }
            }

            graphicalTokens.add(new GraphicalToken(graphicalSymbol, figureString, positionInStaff));

            if (note.getAtomFigure().getFermata() != null && note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
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
        } else if (symbol instanceof SimpleMultiMeasureRest) {
            SimpleMultiMeasureRest multiMeasureRest = (SimpleMultiMeasureRest) symbol;
            int n = multiMeasureRest.getNumMeasures();
            ArrayList<Integer> digits = new ArrayList<>();
            while (n>0) {
                int digit = n%10;
                n/=10;
                digits.add(0, digit);
            }
            for (Integer digit: digits) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(digit), PositionsInStaff.SPACE_5));
            }
            if (multiMeasureRest.getAtomFigure().getFermata() != null && multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
            }

            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.multirest, null, PositionsInStaff.LINE_3));

            if (multiMeasureRest.getAtomFigure().getFermata() != null && multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
            }

        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            if (rest.getAtomFigure().getFermata() != null && rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
            }

            PositionInStaff positionsInStaff;
            if (rest.getAtomFigure().getFigure() == Figures.WHOLE) {
                positionsInStaff = PositionsInStaff.LINE_4;
            } else {
                positionsInStaff = PositionsInStaff.LINE_3;
            }
            graphicalTokens.add(new GraphicalToken(GraphicalSymbol.rest, rest.getAtomFigure().getFigure().toString(), positionsInStaff));

            if (rest.getAtomFigure().getFermata() != null && rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPosition().toString().toLowerCase(), null));
            }


            convertDots(graphicalTokens, rest.getAtomFigure(), PositionsInStaff.SPACE_3);
        } else {
            throw new ExportException("Unsupported symbol conversion of: " + symbol.getClass());
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

    /**
     * @param args
     */
    public static final void main(String [] args) {
        if (args.length != 1) {
            System.err.println("Use: MEI2GraphicSymbols <mei files folder (it leaves here the output file with extension .prm)>");
            return;
        }

        File inputFolder = new File(args[0]);
        if (!inputFolder.exists()) {
            System.err.println("The folder " + inputFolder.getAbsolutePath() + " does not exist");
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        try {
            FileUtils.readFiles(inputFolder, files, "mei", true);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e);
            return;
        }

        int i=0;
        int n = files.size();
        for (File file: files) {
            System.out.println("Processing " + i + "/" + n);
            i++;

            MEISongImporter importer = new MEISongImporter();
            ScoreGraphicalDescriptionWriter writer = new ScoreGraphicalDescriptionWriter();
            MEI2GraphicSymbols converter = new MEI2GraphicSymbols();
            try {
                ScoreSong scoreSong = importer.importSong(file);
                ScoreGraphicalDescription graficalDescription = converter.convert(scoreSong);
                File outputFile = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".prm");
                writer.write(outputFile, graficalDescription);
            } catch (Exception e) {
                System.err.print("---------------------------------------------------------------");
                System.err.print("Error processing " + file.getAbsolutePath());
                e.printStackTrace(System.err);
            }
        }
    }
}
