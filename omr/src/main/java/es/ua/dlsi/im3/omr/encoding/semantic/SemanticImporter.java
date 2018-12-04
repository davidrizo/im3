package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.ScoreToPlayed;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.antlr.ErrorListener;
import es.ua.dlsi.im3.core.io.antlr.GrammarParseRuntimeException;
import es.ua.dlsi.im3.core.io.antlr.ParseError;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.IScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class SemanticImporter implements IScoreSongImporter {
    private ErrorListener errorListener;
    private NotationType notationType = NotationType.eModern; //TODO parametrizar

    public static class Loader extends semanticBaseListener {
        private final ScoreSong scoreSong;
        private final NotationType notationType;
        private boolean newLineFound = false;
        private SimpleNote tieFromNote = null;

        Loader(ScoreSong song, NotationType notationType) {
            scoreSong = song;
            this.notationType = notationType;
        }

        private Time getTime() throws IM3Exception {
            return scoreSong.getStaves().get(0).getLayers().get(0).getDuration();
        }

        @Override
        public void exitNewLine(semanticParser.NewLineContext ctx) {
            super.exitNewLine(ctx);
            newLineFound = true;
        }

        @Override
        public void exitSequence(semanticParser.SequenceContext ctx) {
            super.exitSequence(ctx);
            try {
                if (scoreSong.hasMeasures() && !getTime().equals(scoreSong.getLastMeasure().getEndTime())) {
                    // add a last measure
                    Measure measure = new Measure(scoreSong);
                    scoreSong.addMeasure(scoreSong.getLastMeasure().getEndTime(), measure);
                    measure.setEndTime(getTime());
                }
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot set last measure time", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitClef(semanticParser.ClefContext ctx) {
            super.exitClef(ctx);
            String shape = ctx.children.get(2).getText();
            int line = Integer.parseInt(ctx.children.get(3).getText());
            try {
                Clef clef = ImportFactories.createClef(notationType, shape, line, 0); //TODO octave change in semantic
                clef.setTime(getTime());
                scoreSong.getStaves().get(0).addClef(clef);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot create clef", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitTimeSignature(semanticParser.TimeSignatureContext ctx) {
            super.exitTimeSignature(ctx);
            try {
                TimeSignature timeSignature;
                if (ctx.children.size() == 3) {
                    switch (ctx.children.get(2).getText()) {
                        case "Ct":
                            timeSignature = new TimeSignatureCommonTime(notationType);
                            break;
                        case "Ccut":
                        case "C/":
                            timeSignature = new TimeSignatureCutTime(notationType);
                            break;
                        default:
                            throw new IM3Exception("Unkown meter sign: '" + ctx.children.get(2).getText());
                    }
                } else {
                    int num = Integer.parseInt(ctx.children.get(2).getText());
                    int den = Integer.parseInt(ctx.children.get(4).getText());
                    timeSignature = new FractionalTimeSignature(num, den);
                }
                timeSignature.setTime(getTime());
                scoreSong.getStaves().get(0).addTimeSignature(timeSignature);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot create time signature", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitKeySignature(semanticParser.KeySignatureContext ctx) {
            super.exitKeySignature(ctx);
            try {
                DiatonicPitch diatonicPitch = DiatonicPitch.noteFromName(ctx.children.get(2).getText());
                Accidentals accidentals = null;
                Mode mode = null;
                if (ctx.ACCIDENTALS() != null) {
                    accidentals = Accidentals.accidentalFromName(ctx.ACCIDENTALS().getText());
                }
                if (ctx.MAJOR() != null) {
                    mode = Mode.MAJOR;
                } else if (ctx.MINOR() != null) {
                    mode = Mode.MINOR;
                }
                PitchClass pitchClass = new PitchClass(diatonicPitch, accidentals);
                Key key = new Key(pitchClass, mode);
                KeySignature keySignature = new KeySignature(notationType, key);
                keySignature.setTime(getTime());
                scoreSong.getStaves().get(0).addKeySignature(keySignature);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot create key signature", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitNote(semanticParser.NoteContext ctx) {
            super.exitNote(ctx);
            try {
                Figures figures = Figures.valueOf(ctx.FIGURE().getText().toUpperCase());
                int dots = 0;
                if (ctx.dots() != null) {
                    dots = ctx.dots().getText().length(); // = number of dots
                }
                DiatonicPitch diatonicPitch = DiatonicPitch.noteFromName(ctx.pitch().children.get(0).getText());
                Accidentals accidentals = null;
                if (ctx.pitch().ACCIDENTALS() != null) {
                    accidentals = Accidentals.accidentalFromName(ctx.pitch().ACCIDENTALS().getText());
                }
                int octave = Integer.parseInt(ctx.pitch().octave().getText());
                ScientificPitch scientificPitch = new ScientificPitch(diatonicPitch, accidentals, octave);

                SimpleNote simpleNote = new SimpleNote(figures, dots, scientificPitch);
                if (ctx.GRACENOTE() != null) {
                    simpleNote.setGrace(true);
                }
                scoreSong.getStaves().get(0).getLayers().get(0).add(simpleNote);
                scoreSong.getStaves().get(0).addCoreSymbol(simpleNote);

                if (ctx.FERMATA() != null) {
                    scoreSong.getStaves().get(0).addFermata(simpleNote.getAtomFigure());
                }

                if (tieFromNote != null) {
                    simpleNote.tieFromPrevious(tieFromNote);
                    tieFromNote = null;
                }
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add note", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitTie(semanticParser.TieContext ctx) {
            super.exitTie(ctx);
            Atom lastAtom = scoreSong.getStaves().get(0).getLayers().get(0).getAtomsSortedByTime().last();
            try {
                if (lastAtom == null) {
                    throw new IM3Exception("There is no last atom");
                }
                if (!(lastAtom instanceof SimpleNote)) {
                    throw new IM3Exception("Last atom is not a note, it is a " + lastAtom.getClass().getName());
                }
                tieFromNote = (SimpleNote) lastAtom;
                
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add tie", e);
                throw new GrammarParseRuntimeException(e);

            }
        }

        @Override
        public void exitBarline(semanticParser.BarlineContext ctx) {
            super.exitBarline(ctx);
            Measure measure = new Measure(scoreSong);
            try {
                Time time = getTime();
                if (!scoreSong.hasMeasures()) {
                    scoreSong.addMeasure(Time.TIME_ZERO, measure);
                } else {
                    scoreSong.addMeasure(scoreSong.getLastMeasure().getEndTime(), measure);
                }
                measure.setEndTime(time);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add measure", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitRest(semanticParser.RestContext ctx) {
            super.exitRest(ctx);
            Figures figures = Figures.valueOf(ctx.FIGURE().getText().toUpperCase());
            int dots = 0;
            if (ctx.dots() != null) {
                dots = ctx.dots().getText().length(); // = number of dots
            }
            SimpleRest simpleRest = new SimpleRest(figures, dots);
            try {
                if (tieFromNote != null) {
                    throw new IM3Exception("Trying to tie to a rest from note " + tieFromNote);
                }
                scoreSong.getStaves().get(0).getLayers().get(0).add(simpleRest);
                scoreSong.getStaves().get(0).addCoreSymbol(simpleRest);

                if (ctx.FERMATA() != null) {
                    scoreSong.getStaves().get(0).addFermata(simpleRest.getAtomFigure());
                }
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add rest", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        @Override
        public void exitMultirest(semanticParser.MultirestContext ctx) {
            super.exitMultirest(ctx);
            int number = Integer.parseInt(ctx.INTEGER().getText());
            try {
                Time measureDuration = scoreSong.getStaves().get(0).getLastTimeSignature().getDuration();

                // add the covered measures (but the last one because it will be added by the barline)
                Time time;
                if (scoreSong.hasMeasures()) {
                    time = scoreSong.getLastMeasure().getEndTime();
                } else {
                    time = Time.TIME_ZERO;
                }

                for (int i=0; i<number-1; i++) {
                    Measure measure = new Measure(scoreSong);
                    scoreSong.addMeasure(time, measure);
                    time = time.add(measureDuration);
                    measure.setEndTime(time);
                }

                SimpleMultiMeasureRest simpleRest = new SimpleMultiMeasureRest(measureDuration, number);
                scoreSong.getStaves().get(0).getLayers().get(0).add(simpleRest);
                scoreSong.getStaves().get(0).addCoreSymbol(simpleRest);

            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add multirest", e);
                throw new GrammarParseRuntimeException(e);
            }

        }
    }

    private ScoreSong importSong(semanticLexer lexer, String inputDescription) throws IM3Exception {
        errorListener = new ErrorListener();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        semanticParser parser = new semanticParser(tokens);
        parser.addErrorListener(errorListener);

        // parser.setErrorHandler(new BailErrorStrategy());
        // parser.setBuildParseTree(true); // tell ANTLR to build a parse
        // tree
        semanticParser.SequenceContext tree = parser.sequence();
        ParseTreeWalker walker = new ParseTreeWalker();
        ScoreSong scoreSong = new ScoreSong();
        // currently we are just importing monodic songs of a pentagram
        ScorePart scorePart = scoreSong.addPart();
        Pentagram staff = new Pentagram(scoreSong, "1", 1);
        staff.setNotationType(notationType);
        staff.setName("Semantic");
        scorePart.addStaff(staff);
        scoreSong.addStaff(staff);
        scorePart.addScoreLayer(staff);

        Loader loader = new Loader(scoreSong, NotationType.eModern); //TODO
        walker.walk(loader, tree);
        //if (!loader.newLineFound) {
        //    throw new ImportException("End of line not reached");
        //}
        if (errorListener.getNumberErrorsFound() != 0) {

            throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                    + inputDescription + "\n" + errorListener.toString());
        }

        return scoreSong;

    }

    @Override
    public ScoreSong importSong(File file) throws ImportException {
        try {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Parsing {0}", file.getAbsoluteFile());
            semanticLexer lexer = new semanticLexer(CharStreams.fromPath(file.toPath()));
            return importSong(lexer, file.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Import error {0}", e.getMessage());
            for (ParseError pe : errorListener.getErrors()) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Parse error: {0}", pe.toString());
            }

            throw new ImportException(e.getMessage());
        }
    }

    //TODO notationType
    public ScoreSong importSong(String semanticStringFormat) throws ImportException {
        try {
            CharStream input = CharStreams.fromString(semanticStringFormat);
            semanticLexer lex = new semanticLexer(input);
            return importSong(lex, "String: '" + semanticStringFormat + "'");
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Import error {0}", e.getMessage());
            for (ParseError pe : errorListener.getErrors()) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Parse error: {0}", pe.toString());
            }

            throw new ImportException(e.getMessage());
        }
    }


    @Override
    public ScoreSong importSong(InputStream is) {
        throw new UnsupportedOperationException("TO-DO");
    }

    public static void main(String [] args) throws IM3Exception {
        if (args.length > 2 || args.length == 0) {
            System.err.println("Use: " + SemanticImporter.class.getName() + " [<input file>] <output file (MEI or mid)>, if no input file is given, standard input is used (line must end with an EOL");
            return;
        }

        String outputFileName;
        SemanticImporter semanticImporter = new SemanticImporter();
        ScoreSong scoreSong;

        if (args.length == 1) {
            Scanner s = new Scanner(System.in);
            String line = s.nextLine();
            if (!line.endsWith("\n")) {
                line = line + "\n";
            }
            scoreSong = semanticImporter.importSong(line);
            outputFileName = args[0];
        } else {
            scoreSong = semanticImporter.importSong(new File(args[0]));
            outputFileName = args[1];
        }

        if (outputFileName.toLowerCase().endsWith("mid")) {
            PlayedSong playedSong = new PlayedSong();
            ScoreToPlayed scoreToPlayed = new ScoreToPlayed();
            playedSong = scoreToPlayed.createPlayedSongFromScore(scoreSong);
            MidiSongExporter midiSongExporter = new MidiSongExporter();
            midiSongExporter.exportSong(new File(outputFileName), playedSong);
        } else if (outputFileName.toLowerCase().endsWith("mei")) {
            MEISongExporter exporter = new MEISongExporter();
            exporter.exportSong(new File(outputFileName), scoreSong);
        } else {
            System.err.println("Unsupported output file, it must be .mid or .mei");
        }
    }
}
