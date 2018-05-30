package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.FigureAndDots;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.antlr.ErrorListener;
import es.ua.dlsi.im3.core.io.antlr.GrammarParseRuntimeException;
import es.ua.dlsi.im3.core.io.antlr.ParseError;
import es.ua.dlsi.im3.core.metadata.Person;
import es.ua.dlsi.im3.core.metadata.PersonRoles;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.*;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMayor;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class just import isolated semantic symbols (clefs, notes...) to be handled later
 *
 * @author drizo
 */
public class MensImporter {
    public static class Loader extends mensParserBaseListener {
        private int ksNotesCount;
        private String keyChangeString;
        private Mode keyChangeMode;
        private HumdrumMatrix humdrumMatrix;
        private Figures lastFigure;
        private boolean lastColoured;
        private Perfection lastPerfection;
        private int octaveModif;
        private String noteName;
        private int lastDots;


        public Loader() {
            humdrumMatrix = new HumdrumMatrix();
        }

        @Override
        public void exitHeaderMens(mensParser.HeaderMensContext ctx) {
            super.exitHeaderMens(ctx);
            humdrumMatrix.addItemToCurrentRow(ctx.getText());
        }

        @Override
        public void enterRecord(mensParser.RecordContext ctx) {
            super.enterRecord(ctx);
            humdrumMatrix.addRow();
        }


        /*@Override
        public void enterReferenceRecord(mensParser.ReferenceRecordContext ctx) {
            super.enterReferenceRecord(ctx);
            humdrumMatrix.addRow();
        }

        @Override
        public void exitComposer(mensParser.ComposerContext ctx) {
            super.exitComposer(ctx);
            Person composer = new Person(PersonRoles.COMPOSER, ctx.FULL_LINE_TEXT().getText());
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), composer);
        }*/

        @Override
        public void exitClef(mensParser.ClefContext ctx) {
            super.exitClef(ctx);
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST,
                    "Clef {0}", ctx.getText());

            Clef clef;
            switch (ctx.clefValue().getText()) {
                case "G2":
                    clef = new ClefG2();
                    break;
                case "F2":
                    clef = new ClefF2();
                    break;
                case "F3":
                    clef = new ClefF3();
                    break;
                case "F4":
                    clef = new ClefF4();
                    break;
                case "C1":
                    clef = new ClefC1();
                    break;
                case "C2":
                    clef = new ClefC2();
                    break;
                case "C3":
                    clef = new ClefC3();
                    break;
                case "C4":
                    clef = new ClefC4();
                    break;
                case "C5":
                    clef = new ClefC5();
                    break;
                case "G1":
                    clef = new ClefG1();
                    break;
                default:
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Invalid clef {0}", ctx.getText());
                    throw new GrammarParseRuntimeException("Invalid clef: " + ctx.getText());
            }
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), clef);
        }

        @Override
        public void enterKeySignature(mensParser.KeySignatureContext ctx) {
            super.enterKeySignature(ctx);
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Beginning a key signature",
                    ctx.getText());
            ksNotesCount = 0;
        }


        @Override
        public void exitKeySignature(mensParser.KeySignatureContext ctx) {
            super.exitKeySignature(ctx);

            Key ks = null;
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Key Signature {0}", ctx.getText());
            try {
                Logger.getLogger(MensImporter.class.getName()).log(Level.INFO,
                        "Currently all key signatures are being encoded as UNKOWN");

                if (ctx.keySignatureNote().isEmpty()) {
                    ks = new Key(PitchClasses.C.getPitchClass(), Mode.UNKNOWN); // mode
                } else {
                    DiatonicPitch nn = DiatonicPitch.valueOf(ctx.keySignatureNote().get(0).lowerCasePitch().getText().toUpperCase());
                    if (nn == DiatonicPitch.F) {
                        // sharps
                        ks = new Key(ctx.keySignatureNote().size(), Mode.UNKNOWN.name());
                    } else if (nn == DiatonicPitch.B) {
                        // flats
                        ks = new Key(-ctx.keySignatureNote().size(), Mode.UNKNOWN.name());
                    } else {
                        throw new GrammarParseRuntimeException("Unimplemented key signature support: " + ctx.getText());
                    }
                    // TODO Comprobar el contenido
                }

            } catch (IM3Exception ex) {
                Logger.getLogger(MensImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINE, "Recognized key signature with {0} notes",
                    ksNotesCount);

            humdrumMatrix.addItemToCurrentRow(ctx.getText(), ks);
        }

        @Override
        public void exitMajorKey(mensParser.MajorKeyContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Major key {0}", ctx.getText());
            keyChangeMode = Mode.MAJOR;
            keyChangeString = ctx.getText();
        }

        @Override
        public void exitMinorKey(mensParser.MinorKeyContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Minor key {0}", ctx.getText());
            keyChangeMode = Mode.MINOR;
            keyChangeString = ctx.getText();
        }

        @Override
        public void exitKeyChange(mensParser.KeyChangeContext ctx) {
            try {
                Logger.getLogger(MensImporter.class.getName()).log(Level.FINE, "Key change {0}", ctx.getText());
                DiatonicPitch nn = DiatonicPitch.valueOf(keyChangeString.toUpperCase());
                PitchClass pc;
                if (ctx.keyAccidental() != null) {
                    Accidentals acc = null;
                    switch (ctx.keyAccidental().getText()) {
                        case "n":
                            acc = Accidentals.NATURAL;
                            break;
                        case "-":
                            acc = Accidentals.FLAT;
                            break;
                        case "#":
                            acc = Accidentals.SHARP;
                            break;
                        default:
                            throw new GrammarParseRuntimeException(
                                    "Non valid accidental for key: " + ctx.keyAccidental().getText());
                    }
                    pc = new PitchClass(nn, acc);
                } else {
                    pc = new PitchClass(nn);
                }

                Key kc = new Key(pc, keyChangeMode);
                humdrumMatrix.addItemToCurrentRow(ctx.getText(), kc);
            } catch (IM3Exception ex) {
                Logger.getLogger(MensImporter.class.getName()).log(Level.WARNING, "Cannot parse key change", ex);
                throw new GrammarParseRuntimeException(ex.toString());
            }
        }

        @Override
        public void exitMeterSign(mensParser.MeterSignContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Meter sign {0}", ctx.getText());
            TimeSignature ts;
            try {
                switch (ctx.meterSignValue().getText()) {
                    case "C":
                        ts = new TempusImperfectumCumProlationeImperfecta();
                        break;
                    case "C·":
                        ts = new TempusImperfectumCumProlationePerfecta();
                        break;
                    case "O":
                        ts = new TempusPerfectumCumProlationeImperfecta();
                        break;
                    case "O·":
                        ts = new TempusPerfectumCumProlationePerfecta();
                        break;
                    case "C|":
                        ts = new TimeSignatureCutTime(NotationType.eMensural);
                        break;
                    case "C32":
                        ts = new TimeSignatureProporcionMenor();
                        break;
                    case "C|32":
                        ts = new TimeSignatureProporcionMayor();
                        break;
                    default:
                        throw new IM3Exception("Unsupported meter sign: '" + ctx.getText() + "'");
                }
                humdrumMatrix.addItemToCurrentRow(ctx.getText(), ts);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Invalid meter sign {0}", ctx.getText());
                throw new GrammarParseRuntimeException("Invalid time signature: " + ctx.getText());
            }
        }

        @Override
        public void exitStaff(mensParser.StaffContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Staff {0}", ctx.getText());
            super.exitStaff(ctx);

            int staffNumber = Integer.parseInt(ctx.number().getText());
            //TODO
        }

        @Override
        public void exitSectionLabel(mensParser.SectionLabelContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Section label {0}", ctx.getText());
            super.exitSectionLabel(ctx);
            String sectionLabel = ctx.FIELD_TEXT().getText();
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), new SectionMark(sectionLabel));
        }

        @Override
        public void exitInstrument(mensParser.InstrumentContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Instrument {0}", ctx.getText());
            super.exitInstrument(ctx);
            String instrument = ctx.FIELD_TEXT().getText();
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), new KernInstrument(instrument));
        }

        @Override
        public void exitMetronome(mensParser.MetronomeContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Metronome {0}", ctx.getText());
            super.exitMetronome(ctx);
            String numberStr = ctx.number().getText();
            MetronomeMark mm = new MetronomeMark(Integer.parseInt(numberStr));
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), mm);
        }

        @Override
        public void exitNullInterpretation(mensParser.NullInterpretationContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Null interpretation {0}", ctx.getText());
            super.exitNullInterpretation(ctx);
            KernNullInterpretation kernNullInterpretation = new KernNullInterpretation();
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), kernNullInterpretation);
        }

        @Override
        public void exitBarLine(mensParser.BarLineContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "BarLine {0}", ctx.getText());
            super.exitBarLine(ctx);
            MarkBarline markBarLine = new MarkBarline(); //TODO repetitions ....
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), markBarLine);
        }


        @Override
        public void exitPlaceHolder(mensParser.PlaceHolderContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Placeholder {0}", ctx.getText());
            super.exitPlaceHolder(ctx);
            KernPlaceHolder placeHolder = new KernPlaceHolder();
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), placeHolder);

        }

        @Override
        public void exitFieldComment(mensParser.FieldCommentContext ctx) {
            super.exitFieldComment(ctx);
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Field comment {0}", ctx.getText());
            String text = ctx.FIELD_TEXT().getText();
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), new KernFieldComment(text));
        }



        @Override
        public void exitLayoutRestPosition(mensParser.LayoutRestPositionContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Layout rest position {0}", ctx.getText());
            super.exitLayoutRestPosition(ctx);
            PositionInStaff positionInStaff = null;
            try {
                positionInStaff = PositionInStaff.parseString(ctx.staffPosition().getText());
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Invalid position in staff {0}", ctx.LAYOUT_REST_POSITION().getText());
                throw new GrammarParseRuntimeException("Invalid position in staff: " + e.toString());
            }
            KernRestPosition kernRestPosition = new KernRestPosition(positionInStaff);
            humdrumMatrix.addItemToCurrentRow(ctx.getParent().getParent().getText(), kernRestPosition);
        }

        @Override
        public void enterDuration(mensParser.DurationContext ctx) {
            super.enterDuration(ctx);
            lastColoured = false;
            lastPerfection = null;
        }

        @Override
        public void exitDuration(mensParser.DurationContext ctx) {
            super.exitDuration(ctx);
            lastDots = ctx.dots()==null?0:ctx.dots().getChildCount();
        }

        @Override
        public void exitMensuralFigure(mensParser.MensuralFigureContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural figure {0}", ctx.getText());
            super.exitMensuralFigure(ctx);
            Figures f;

            switch (ctx.getText().charAt(0)) {
                case 'X': f = Figures.MAXIMA; break;
                case 'L': f = Figures.LONGA; break;
                case 'S': f = Figures.BREVE; break;
                case 's': f = Figures.SEMIBREVE; break;
                case 'M': f = Figures.MINIM; break;
                case 'm': f = Figures.SEMIMINIM; break;
                case 'U': f = Figures.FUSA; break;
                case 'u': f = Figures.SEMIFUSA; break;
                default:
                    throw new GrammarParseRuntimeException("Mensural duration '" + ctx.getText() + "' not recognized");
            }
            lastFigure = f;
        }

        @Override
        public void exitColoured(mensParser.ColouredContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural coloration {0}", ctx.getText());
            super.exitColoured(ctx);
            this.lastColoured = true;
        }

        @Override
        public void exitMensuralPerfection(mensParser.MensuralPerfectionContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural perfection {0}", ctx.getText());
            super.exitMensuralPerfection(ctx);
            switch (ctx.getText()) {
                case "p":
                    this.lastPerfection = Perfection.perfectum;
                    break;
                case "i":
                    this.lastPerfection = Perfection.imperfectum;
                    break;
                case "I":
                    this.lastPerfection = Perfection.alteratio;
                    break;
                default:
                    throw new GrammarParseRuntimeException("Mensural perfection '" + ctx.getText() + "' not recognized");

            }
        }

        @Override
        public void exitRest(mensParser.RestContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural rest {0}", ctx.getText());

            super.exitRest(ctx);
            SimpleRest rest = new SimpleRest(lastFigure, lastDots);
            handlePerfectionColoration(rest);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), rest);
        }

        private void handlePerfectionColoration(SingleFigureAtom simpleFigureAtom) {
            simpleFigureAtom.getAtomFigure().setColored(lastColoured);
            if (lastPerfection != null) {
                try {
                    simpleFigureAtom.getAtomFigure().setExplicitMensuralPerfection(lastPerfection);
                } catch (IM3Exception e) {
                    throw new GrammarParseRuntimeException("Cannot set perfection: " + e.toString());
                }
            }
        }

        private void checkAllNoteNameEqual(String text) throws GrammarParseRuntimeException {
            // check all letters are equal
            for (int i = 1; i < text.length(); i++) {
                if (text.charAt(i) != text.charAt(0)) {
                    throw new GrammarParseRuntimeException(
                            "The characters for a note name should be the same for specifying the octave, and we have '"
                                    + text + "'");
                }
            }
        }

        private void handleNoteName(String code, int octaveModif) {
            checkAllNoteNameEqual(code);
            this.octaveModif = octaveModif;
            noteName = code.substring(0, 1).toUpperCase();
        }

        @Override
        public void enterTrebleNotes(mensParser.TrebleNotesContext ctx) {
            super.enterTrebleNotes(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "TrebleNotes {0}", ctx.getText());
            handleNoteName(ctx.getText(), ctx.getText().length() - 1);
        }

        @Override
        public void enterBassNotes(mensParser.BassNotesContext ctx) {
            super.enterBassNotes(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "BassNotes {0}", ctx.getText());
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "TrebleNotes {0}", ctx.getText());
            handleNoteName(ctx.getText(), -ctx.getText().length());
        }

        @Override
        public void exitNote(mensParser.NoteContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural note {0}", ctx.getText());
            super.exitNote(ctx);

            int octave = 4 + octaveModif;

            // check all letters are equal
            DiatonicPitch nn = DiatonicPitch.valueOf(noteName);

            Accidentals acc = Accidentals.NATURAL;
            if (ctx.alteration() !=
                    null) {
                switch (ctx.alteration().getText()) {
                    case "n":
                        acc = Accidentals.NATURAL;
                        break;
                    case "-":
                        acc = Accidentals.FLAT;
                        break;
                    case "#":
                        acc = Accidentals.SHARP;
                        break;
                    default:
                        throw new
                                GrammarParseRuntimeException("Unimplemented accidental: " +
                                ctx.alteration().getText());
                }
            }

            ScientificPitch scientificPitch = new ScientificPitch(new PitchClass(nn, acc), octave);
            SimpleNote note = new SimpleNote(lastFigure, lastDots, scientificPitch);

            if (ctx.alterationVisualMode() != null) {
                switch (ctx.alterationVisualMode().getText()) {
                    case "x":
                        try {
                            note.setWrittenExplicitAccidental(acc);
                        } catch (IM3Exception e) {
                            throw new GrammarParseRuntimeException("Cannot set an written explicit accidental: "+ e);
                        }
                        break;
                    case "xx":
                        throw new UnsupportedOperationException("TO-DO Editorial accidental");
                }
            }

            handlePerfectionColoration(note);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), note);
        }
    }

    static class MensLexer extends mensLexer {
        boolean debug = false;
        public MensLexer(CharStream input, boolean debug) {
            super(input);
            this.debug = debug;
        }

        @Override
        public Token nextToken() {
            Token token = super.nextToken();
            if (debug) {
                System.out.println("Lexer: '" + token + "'");
            }
            return token;
        }
    }

    private HumdrumMatrix importMens(CharStream input, String inputDescription) throws ImportException {
        ErrorListener errorListener = new ErrorListener();
        try {
            Logger.getLogger(MensImporter.class.getName()).log(Level.INFO, "Parsing {0}", inputDescription);

            boolean debugLexer = true;
            mensLexer lexer = new MensLexer(input, debugLexer);
            lexer.addErrorListener(errorListener);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            mensParser parser = new mensParser(tokens);
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.start();
            ParseTreeWalker walker = new ParseTreeWalker();
            Loader loader = new Loader();
            walker.walk(loader, tree);
            if (errorListener.getNumberErrorsFound() != 0) {

                throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                        + inputDescription + "\n" + errorListener.toString());
            }

            return loader.humdrumMatrix;
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(MensImporter.class.getName()).log(Level.WARNING, "Import error {0}", e.getMessage());
            for (ParseError pe : errorListener.getErrors()) {
                Logger.getLogger(MensImporter.class.getName()).log(Level.WARNING, "Parse error: {0}", pe.toString());
            }

            throw new ImportException(e.getMessage());
        }
    }

    public HumdrumMatrix importMens(File file) throws ImportException {
        try {
            CharStream input = CharStreams.fromFileName(file.getAbsolutePath());
            return importMens(input, file.getAbsolutePath());
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    public HumdrumMatrix importMens(String string) throws ImportException {
        CharStream input = CharStreams.fromString(string);
        return importMens(input, string);
    }
}
