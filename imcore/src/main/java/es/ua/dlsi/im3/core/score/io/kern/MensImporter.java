package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.antlr.ANTLRUtils;
import es.ua.dlsi.im3.core.io.antlr.ErrorListener;
import es.ua.dlsi.im3.core.io.antlr.GrammarParseRuntimeException;
import es.ua.dlsi.im3.core.io.antlr.ParseError;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.*;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMayor;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class just import isolated semantic symbols (clefs, notes...) to be handled later
 *
 * @author drizo
 */
public class MensImporter {
    private boolean debug;

    public static class Loader extends mensParserBaseListener {
        private final Parser parser;
        private int ksNotesCount;
        private String keyChangeString;
        private Mode keyChangeMode;
        private HumdrumMatrix humdrumMatrix;
        private Figures lastFigure;
        private boolean lastColoured;
        private Perfection lastPerfection;
        private int octaveModif;
        private String noteName;
        private int lastAgumentationDots;
        private boolean lastHasSeparationDot;
        private boolean inLigature;
        private LigatureType ligatureType;
        private boolean debug;
        private StemDirection lastStemDirection;
        private Integer lastRestLinePosition;

        public Loader(Parser parser, boolean debug) {
            this.humdrumMatrix = new HumdrumMatrix();
            this.inLigature = false;
            this.debug = debug;
            this.parser = parser;
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            super.enterEveryRule(ctx);
            if (debug) {
                System.out.println(ANTLRUtils.getRuleDescription(parser, ctx, "Enter"));
            }
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            super.enterEveryRule(ctx);
            if (debug) {
                System.out.print(ANTLRUtils.getRuleDescription(parser, ctx, "Exit"));
                //System.out.print(", next token: " + parser.getTokenStream().getTokenSource().
                System.out.println();
            }
        }


        @Override
        public void exitHeaderMens(mensParser.HeaderMensContext ctx) {
            super.exitHeaderMens(ctx);
            humdrumMatrix.addItemToCurrentRow(ctx.getText());
        }

        @Override
        public void exitPart(mensParser.PartContext ctx) {
            super.exitPart(ctx);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), new ScorePart(null, Integer.parseInt(ctx.number().getText())));
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
        public void exitFractionalMeter(mensParser.FractionalMeterContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Meter sign {0}", ctx.getText());
            TimeSignature ts; //TODO Ver qué hacer con esto, de momento lo pongo en la matrix

            String numStr = ctx.numerator().getText();
            int num = Integer.parseInt(numStr);
            String denStr = ctx.denominator().getText();
            int den = Integer.parseInt(denStr);

            ts = new FractionalTimeSignature(num, den);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), ts);
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
                    case "C.":
                        ts = new TempusImperfectumCumProlationePerfecta();
                        break;
                    case "O":
                        ts = new TempusPerfectumCumProlationeImperfecta();
                        break;
                    case "O.":
                        ts = new TempusPerfectumCumProlationePerfecta();
                        break;
                    case "C|":
                        ts = new TempusImperfectumCumProlationeImperfectaDiminutum();
                        break;
                    case "C3/2":
                        ts = new TimeSignatureProporcionMenor();
                        break;
                    case "C|3/2":
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
            Staff staff = new Pentagram(null, ctx.number().getText(), staffNumber);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), staff);
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
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), kernNullInterpretation);
        }

        @Override
        public void exitBarLine(mensParser.BarLineContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "BarLine {0}", ctx.getText());
            super.exitBarLine(ctx);
            if (ctx.barlineProperties() == null || ctx.barlineProperties().MINUS() == null) {
                MarkBarline markBarLine = new MarkBarline(); //TODO repetitions, double bar line ....
                humdrumMatrix.addItemToCurrentRow(ctx.getText(), markBarLine);
            } // if minus present it is hiddem
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
            String text = ctx.FIELD_TEXT()==null?null:ctx.FIELD_TEXT().getText();
            /*if (text != null && text.startsWith("!")) {
                text = text.substring(1); // the parser returns the ! first
            }*/
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
            //lastAgumentationDots = ctx.augmentationDots()==null?0:ctx.augmentationDots().getChildCount();
            lastAgumentationDots = ctx.augmentationDot()==null?0:ctx.augmentationDot().getChildCount();
            lastHasSeparationDot = ctx.separationDot()!=null;
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
        public void enterRest(mensParser.RestContext ctx) {
            lastRestLinePosition = null;
        }

        @Override
        public void exitRestLinePosition(mensParser.RestLinePositionContext ctx) {
            this.lastRestLinePosition = Integer.parseInt(ctx.getChild(1).getText());
        }


        @Override
        public void exitRest(mensParser.RestContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural rest {0}", ctx.getText());

            super.exitRest(ctx);
            SimpleRest rest = new SimpleRest(lastFigure, lastAgumentationDots);
            rest.setLinePosition(lastRestLinePosition); // it can be null
            rest.getAtomFigure().setFollowedByMensuralDivisionDot(lastHasSeparationDot);
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

        private ScientificPitch constructScientificPitch(mensParser.AlterationContext alterationContext) {
            int octave = 4 + octaveModif;

            // check all letters are equal
            DiatonicPitch nn = DiatonicPitch.valueOf(noteName);

            Accidentals acc = Accidentals.NATURAL;
            if (alterationContext != null) {
                switch (alterationContext.getText()) {
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
                                GrammarParseRuntimeException("Unimplemented accidental: " + alterationContext.getText());
                }
            }

            ScientificPitch scientificPitch = new ScientificPitch(new PitchClass(nn, acc), octave);
            return scientificPitch;
        }

        @Override
        public void enterNote(mensParser.NoteContext ctx) {
            this.lastStemDirection = null;
        }

        @Override
        public void exitStem(mensParser.StemContext ctx) {
            switch (ctx.getText()) {
                case "/":
                    lastStemDirection = StemDirection.up;
                    break;
                case "\\":
                    lastStemDirection = StemDirection.down;
                    break;
                default:
                    throw new GrammarParseRuntimeException("Unimplemented stem direction: " + ctx.getText());
            }
        }

        @Override
        public void exitNote(mensParser.NoteContext ctx) {
            Logger.getLogger(MensImporter.class.getName()).log(Level.FINEST, "Mensural note {0}", ctx.getText());
            super.exitNote(ctx);

            ScientificPitch scientificPitch = constructScientificPitch(ctx.alteration());
            SimpleNote note = new SimpleNote(lastFigure, lastAgumentationDots, scientificPitch);
            note.getAtomFigure().setFollowedByMensuralDivisionDot(lastHasSeparationDot);

            if (lastStemDirection != null) {
                note.setExplicitStemDirection(lastStemDirection);
            }

            if (ctx.alterationVisualMode() != null) {
                switch (ctx.alterationVisualMode().getText()) {
                    case "x":
                        note.setWrittenExplicitAccidental(scientificPitch.getPitchClass().getAccidental());
                        break;
                    case "xx":
                        throw new UnsupportedOperationException("TO-DO Editorial accidental");
                }
            }

            handlePerfectionColoration(note);

            /*if (ctx.afterNote().ligatureType() != null && ctx.afterNote().ligatureType().size() > 0) {
                String str = ctx.afterNote().ligatureType().get(0).getText();
                if (str.equals("R")) {
                    ligatureType = LigatureType.recta;
                } else if (str.equals("Q")) {
                    ligatureType = LigatureType.obliqua;
                } else {
                    throw new GrammarParseRuntimeException("Invalid ligature type: '" + str + "'");
                }
            }*/

            KernLigatureComponent ligatureComponent = null;
            if (ctx.beforeNote().ligatureStart() != null && ctx.beforeNote().ligatureStart().size() > 0) {
                if (ctx.afterNote().ligatureEnd() != null && ctx.afterNote().ligatureEnd().size() > 0) {
                    throw new GrammarParseRuntimeException("Cannot create a ligature of just one symbol");
                }
                if (ctx.beforeNote().ligatureStart().size() != 1) {
                    throw new GrammarParseRuntimeException("Expected just 1 ligature start");
                }
                switch (ctx.beforeNote().ligatureStart().get(0).getText()) {
                    case "[":
                        ligatureType = LigatureType.recta;
                        break;
                    case "<":
                        ligatureType = LigatureType.obliqua;
                        break;
                    default:
                        throw new GrammarParseRuntimeException("Invalid ligature start type, should be [ or <, and it is: " + ctx.beforeNote().ligatureStart().get(0).getText());
                }
                ligatureComponent = new KernLigatureComponent(LigatureStartEnd.start, ligatureType, note);
                inLigature = true;
            } else if (ctx.afterNote().ligatureEnd() != null && ctx.afterNote().ligatureEnd().size() > 0) {
                if (ctx.afterNote().ligatureEnd().size() != 1) {
                    throw new GrammarParseRuntimeException("Expected just 1 ligature end");
                }
                switch (ctx.afterNote().ligatureEnd().get(0).getText()) {
                    case "]":
                        if (ligatureType != LigatureType.recta) {
                            throw new GrammarParseRuntimeException("Expected > and found ] with previous ligature type " + ligatureType);
                        }
                        break;
                    case ">":
                        if (ligatureType != LigatureType.obliqua) {
                            throw new GrammarParseRuntimeException("Expected ] and found > with previous ligature type " + ligatureType);
                        }
                        break;
                    default:
                        throw new GrammarParseRuntimeException("Invalid ligature end type, should be > or ], and it is: " + ctx.beforeNote().ligatureStart().get(0).getText());
                }

                ligatureComponent = new KernLigatureComponent(LigatureStartEnd.end, ligatureType, note);
                inLigature = false;
                ligatureType = null;

            } else if (inLigature) {
                ligatureComponent = new KernLigatureComponent(LigatureStartEnd.inside, ligatureType, note);
            }

            if (ligatureComponent != null) {
                humdrumMatrix.addItemToCurrentRow(ctx.getText(), ligatureComponent);
            } else {
                humdrumMatrix.addItemToCurrentRow(ctx.getText(), note);
            }
        }

        @Override
        public void exitCustos(mensParser.CustosContext ctx) {
            super.exitCustos(ctx);
            ScientificPitch scientificPitch = constructScientificPitch(ctx.alteration());
            Custos custos = new Custos(scientificPitch);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), custos);
        }


        @Override
        public void exitLyricsText(mensParser.LyricsTextContext ctx) {
            super.exitLyricsText(ctx);
            humdrumMatrix.addItemToCurrentRow(ctx.getText(), new KernText(ctx.getText()));
        }

    }

    private HumdrumMatrix importMens(CharStream input, String inputDescription, boolean anyStart) throws ImportException {
        ErrorListener errorListener = new ErrorListener();
        try {
            Logger.getLogger(MensImporter.class.getName()).log(Level.INFO, "Parsing {0}", inputDescription);

            mensLexer lexer = new mensLexer(input);

            if (debug) {
                new ANTLRUtils().printLexer(lexer);
            }

            lexer.addErrorListener(errorListener);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            mensParser parser = new mensParser(tokens);
            parser.addErrorListener(errorListener);

            ParseTree tree;
            if (anyStart) {
                tree = parser.anystart();
            } else {
                tree = parser.start();
            }
            ParseTreeWalker walker = new ParseTreeWalker();
            Loader loader = new Loader(parser, debug);
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
            return importMens(input, file.getAbsolutePath(), false);
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    public HumdrumMatrix importMens(String string) throws ImportException {
        CharStream input = CharStreams.fromString(string);
        return importMens(input, string, false);
    }

    public HumdrumMatrix importMens(String string, boolean anyStart) throws ImportException {
        CharStream input = CharStreams.fromString(string);
        return importMens(input, string, anyStart);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
