package es.ua.dlsi.im3.omr.encoding;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.Accidentals;
import es.ua.dlsi.im3.core.score.KeySignature;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.Trill;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticEncoding;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Clef;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Fermata;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Multirest;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Slur;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticEncoding;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbol;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * It encodes a ScoreSong into both agnostic and semantic encodings
 * @autor drizo
 */
public class Encoder {
    static final PositionInStaff CENTER_LINE = PositionInStaff.fromLine(3);
    private static final PositionInStaff FERMATA_POSITION_ABOVE  = PositionsInStaff.SPACE_6;
    private static final PositionInStaff FERMATA_POSITION_BELOW = PositionsInStaff.SPACE_MINUS_1;
    private static VerticalLine barline;
    private static AgnosticSystemBreak agnosticSystemBreak;
    private static SemanticSystemBreak semanticSystemBreak;
    private static Dot dot = new Dot();
    private final AgnosticVersion version;

    AgnosticEncoding agnosticEncoding;
    SemanticEncoding semanticEncoding;
    ScoreSong scoreSong;
    private final VerticalSeparator verticalSeparator;
    private static final JuxtapositionSeparator juxtapositionSeparator = new JuxtapositionSeparator();
    private final HorizontalSeparator horizontalSeparator;

    public Encoder(AgnosticVersion version) {
        this.version = version;
        horizontalSeparator = new HorizontalSeparator(version);
        barline = new VerticalLine(version);
        verticalSeparator = new VerticalSeparator(version);
        agnosticSystemBreak = new AgnosticSystemBreak();
        semanticSystemBreak = new SemanticSystemBreak();
    }

    public Encoder() {
        this(AgnosticVersion.v2);
    }

    private void addVerticalSeparator() {
        agnosticEncoding.add(verticalSeparator);
    }

    public void encode(ScoreSong scoreSong) throws IM3Exception {
        agnosticEncoding = new AgnosticEncoding();
        semanticEncoding = new SemanticEncoding();

        if (scoreSong.getStaves().size() != 1) {
            //Note we don't have information in the MEI file about line breaking
            StringBuilder stringBuilder = new StringBuilder();

            for (Staff staff: scoreSong.getStaves()) {
                stringBuilder.append(staff.getName());
                stringBuilder.append(' ');
            }
            throw new ExportException("Currently only one staff is supported in the export format, and there are " + scoreSong.getStaves().size()
                    + " with names: " + stringBuilder.toString());
        }

        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        List<ITimedElementInStaff> coreSymbolsOrdered = staff.getCoreSymbolsOrdered();
        Time lastEndTime = null;
        for (ITimedElementInStaff symbol : coreSymbolsOrdered) {
            if (symbol instanceof SystemBreak) {
                agnosticEncoding.add(agnosticSystemBreak);
                semanticEncoding.add(semanticSystemBreak);
            } else {
                Measure measure = null;
                if (scoreSong.hasMeasures()) {
                    measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
                }
                if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                    agnosticEncoding.add(new AgnosticSymbol(barline, PositionsInStaff.LINE_1));
                    agnosticEncoding.add(horizontalSeparator);
                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
                    semanticEncoding.add(new SemanticSymbol(new Barline()));
                    ////semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
                }

                convert(symbol, drawnAccidentals);

                if (symbol instanceof Atom) {
                    lastEndTime = ((Atom) symbol).getOffset();
                }

                lastMeasure = measure;
            }
        }

        if (lastEndTime == null) {
            throw new IM3Exception("Song without notes");
        }

        Time measureEndTime = staff.getRunningTimeSignatureAt(lastMeasure).getDuration().add(lastMeasure.getTime());
        if (lastEndTime.equals(measureEndTime)) {
            // add bar line just if the measure is complete
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
            agnosticEncoding.add(new AgnosticSymbol(barline, PositionsInStaff.LINE_1));
            agnosticEncoding.add(horizontalSeparator);
            //semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
            semanticEncoding.add(new SemanticSymbol(new Barline()));
        }

        agnosticEncoding.removeLastSymbolIfSeparator();
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/
    }

    private void convertDuration(StringBuilder sb, AtomFigure figure) {
        sb.append(figure.getFigure().name().toLowerCase());

    }

    private void convertDots(AtomFigure figure, PositionInStaff positionInStaff) {
        for (int i = 0; i < figure.getDots(); i++) {
            agnosticEncoding.add(new AgnosticSymbol(dot, positionInStaff));
            agnosticEncoding.add(horizontalSeparator);
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.dot, null, positionInStaff));
        }
    }

    private void convert(ITimedElementInStaff symbol, HashMap<AtomPitch, Accidentals> drawnAccidentals) throws IM3Exception {
        if (symbol instanceof es.ua.dlsi.im3.core.score.Clef) {
            PositionInStaff positionInStaff = PositionInStaff.fromLine(((es.ua.dlsi.im3.core.score.Clef) symbol).getLine());
            es.ua.dlsi.im3.core.score.Clef clef = (es.ua.dlsi.im3.core.score.Clef) symbol;
            if (clef.getOctaveChange() != 0) {
                throw new IM3Exception("Unsupported octave changes: " + clef);
            }
            ClefNote clefNote;
            switch (clef.getNote()) {
                case C:
                    clefNote = ClefNote.C;
                    break;
                case G:
                    clefNote = ClefNote.G;
                    break;
                case F:
                    clefNote = ClefNote.F;
                    break;
                // TODO: 23/2/18 Octava bassa, alta....
                default: throw new IM3Exception("Invalid clef note: " + clef.getNote());
            }
            agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Clef(clefNote), positionInStaff));
            agnosticEncoding.add(horizontalSeparator);
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.clef, clef.getNote().name(), positionInStaff));
            //semanticTokens.add(new SemanticToken(SemanticSymbol.clef, clef.getNote() + "" + clef.getLine()));
            semanticEncoding.add(new SemanticSymbol(new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.Clef(clefNote, clef.getLine())));

        } else if (symbol instanceof KeySignature) {
            KeySignature ks = (KeySignature) symbol;
            PositionInStaff[] positions = ks.computePositionsOfAccidentals();
            if (positions != null) {
                boolean first = true;
                for (PositionInStaff position : positions) {
                    es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals [] accs = convert(ks.getAccidental());
                    for (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals acc: accs) {
                        agnosticEncoding.add(new AgnosticSymbol(new Accidental(acc), position));
                        agnosticEncoding.add(horizontalSeparator);
                    }
                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, ks.getAccidental().name().toLowerCase(), position));
                }
            }
            /*StringBuilder sb = new StringBuilder();
            sb.append(ks.getConcertPitchKey().getPitchClass().toString());
            if (ks.getConcertPitchKey().getMode() != null && ks.getConcertPitchKey().getMode() != Mode.UNKNOWN) {
                sb.append(ks.getConcertPitchKey().getMode().getName());
            }*/
            //semanticTokens.add(new SemanticToken(SemanticSymbol.keySignature, sb.toString()));
            semanticEncoding.add(new SemanticSymbol(new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.KeySignature(
                    ks.getConcertPitchKey().getPitchClass().getNoteName(),
                    ks.getConcertPitchKey().getPitchClass().getAccidental(),
                    convert(ks.getConcertPitchKey().getMode()))));
            //semanticTokens.add(new SemanticToken(SemanticSymbol.keySignature, sb.toString()));

        } else if (symbol instanceof TimeSignature) {
            es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.TimeSignature timeSignature;

            if (symbol instanceof SignTimeSignature) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.metersign, ((SignTimeSignature) symbol).getSignString(), PositionsInStaff.LINE_3));
                MeterSigns meterSign = convert((SignTimeSignature) symbol);
                agnosticEncoding.add(new AgnosticSymbol(new MeterSign(meterSign), PositionsInStaff.LINE_3));
                agnosticEncoding.add(horizontalSeparator);
                timeSignature = new MeterSignTimeSignature(meterSign);
                //sb.append(((SignTimeSignature) symbol).getSignString());
            } else if (symbol instanceof FractionalTimeSignature) {
                FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
                agnosticEncoding.add(new AgnosticSymbol(new Digit(ts.getNumerator()), PositionsInStaff.LINE_4));
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(new Digit(ts.getDenominator()), PositionsInStaff.LINE_2));
                agnosticEncoding.add(horizontalSeparator);
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getNumerator()), PositionsInStaff.LINE_4));
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getDenominator()), PositionsInStaff.LINE_2));
                //sb.append(ts.getNumerator());
                //sb.append('/');
                //sb.append(ts.getDenominator());
                timeSignature = new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.FractionalTimeSignature(ts.getNumerator(), ts.getDenominator());
            } else {
                throw new ExportException("Unsupported time signature" + symbol.getClass());
            }


            //semanticTokens.add(new SemanticToken(SemanticSymbol.timeSignature, sb.toString()));
            semanticEncoding.add(new SemanticSymbol(timeSignature));
        } else if (symbol instanceof SimpleChord) {
            throw new IM3Exception("Unsupported chords"); // TODO - separar con verticalSeparator
        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;

            if (note.getAtomFigure().getFermata() != null && (note.getAtomFigure().getFermata().getPosition() == null || note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(Positions.above), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();

                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, "above", PositionsInStaff.SPACE_6));
            }

            PositionInStaff positionInStaff = symbol.getStaff().computePositionInStaff(note.getTime(),
                    note.getPitch().getPitchClass().getNoteName(), note.getPitch().getOctave());

            if (note.getAtomPitch().isTiedFromPrevious()) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, END, positionInStaff));
                agnosticEncoding.add(new AgnosticSymbol(new Slur(StartEnd.end), positionInStaff));
                agnosticEncoding.add(horizontalSeparator);
            }

            Accidentals accidentalToDraw = drawnAccidentals.get(note.getAtomPitch());
            if (accidentalToDraw != null) {
                es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals [] accs = convert(accidentalToDraw);
                for (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals acc: accs) {
                    agnosticEncoding.add(new AgnosticSymbol(new Accidental(acc), positionInStaff));
                    agnosticEncoding.add(horizontalSeparator);
                }
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, accidentalToDraw.name().toLowerCase(), positionInStaff));
            }

            //String figureString = generateFigureString(note);
            INoteDurationSpecification figureString = generateFigureString(note);

          //  GraphicalSymbol graphicalSymbol;
            /*if (note.isGrace()) {
                graphicalSymbol = GraphicalSymbol.gracenote;
            } else {
                graphicalSymbol = GraphicalSymbol.note;
            }*/

            // TODO: 18/10/17 Otras marcas
            boolean trill = false;
            if (note.getMarks() != null) {
                for (StaffMark mark : note.getMarks()) {
                    if (mark instanceof Trill) {
                        agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Trill(), PositionsInStaff.SPACE_6));
                        addVerticalSeparator();
                        trill = true;
                        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.trill, null, FERMATA_POSITION_ABOVE));
                    } else {
                        throw new IM3Exception("Unsupported mark: " + mark.getClass());
                    }
                }
            }

            if (note.isGrace()) {
                agnosticEncoding.add(new AgnosticSymbol(new GraceNote(figureString), positionInStaff));
            } else {
                agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note(figureString), positionInStaff));
            }
            //graphicalTokens.add(new GraphicalToken(graphicalSymbol, figureString, positionInStaff));

            if (note.getAtomFigure().getFermata() != null && note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(Positions.below), FERMATA_POSITION_BELOW));
            } else {
                agnosticEncoding.add(horizontalSeparator);
            }

            PositionInStaff dotPositionInStaff;
            if (positionInStaff.laysOnLine()) {
                dotPositionInStaff = positionInStaff.move(1);
            } else {
                dotPositionInStaff = positionInStaff;
            }
            convertDots(note.getAtomFigure(), dotPositionInStaff);

            if (note.getAtomPitch().isTiedToNext()) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, START, positionInStaff));
                agnosticEncoding.add(new AgnosticSymbol(new Slur(StartEnd.start), positionInStaff));
                agnosticEncoding.add(horizontalSeparator);
            }

            /*StringBuilder sb = new StringBuilder();
            sb.append(note.getPitch().toString());
            sb.append(SemanticToken.SUBVALUE_SEPARATOR);
            fillSingleFigureAtom(sb, note);
            SemanticSymbol semanticSymbol;
            if (note.isGrace()) {
                semanticSymbol = SemanticSymbol.gracenote;
            } else {
                semanticSymbol = SemanticSymbol.note;
            }*/

            //semanticTokens.add(new SemanticToken(semanticSymbol, sb.toString()));

            es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.Note semanticNote =
                    new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.Note(note.isGrace(), note.getPitch(), note.getAtomFigure().getFigure(), note.getAtomFigure().getDots(),
                            note.getAtomFigure().getFermata() != null,
                            trill);
            semanticEncoding.add(new SemanticSymbol(semanticNote));


            if (note.getAtomPitch().isTiedToNext()) {
                semanticEncoding.add(new SemanticSymbol(new Tie()));
                //semanticTokens.add(new SemanticToken(SemanticSymbol.tie));
            }

        } else if (symbol instanceof SimpleMultiMeasureRest) {
            SimpleMultiMeasureRest multiMeasureRest = (SimpleMultiMeasureRest) symbol;
            int n = multiMeasureRest.getNumMeasures();

            if (n > 2) { // the digit is placed on top of the symbol by Verovio but after the physical start of multirest
                agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Multirest(), PositionsInStaff.LINE_3));
                agnosticEncoding.add(horizontalSeparator);
            }

            if (multiMeasureRest.getAtomFigure().getFermata() != null && multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                Positions positions = convert(multiMeasureRest.getAtomFigure().getFermata().getPosition());
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(positions), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
            }

            if (n <= 2) { // the digit is placed on top of the symbol by Verovio
                agnosticEncoding.add(new AgnosticSymbol(new Digit(n), PositionsInStaff.SPACE_5));
                addVerticalSeparator();
                if (n == 1) {
                    agnosticEncoding.add(new AgnosticSymbol(new Rest(RestFigures.whole), PositionsInStaff.LINE_4)); // Verovio encodes these rests this way
                } else {
                    agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest(RestFigures.breve), PositionsInStaff.LINE_3)); // Verovio encodes these rests this way
                }
                agnosticEncoding.add(horizontalSeparator);
            } else {
                ArrayList<Integer> digits = new ArrayList<Integer>();
                while (n > 0) {
                    int digit = n % 10;
                    n /= 10;
                    digits.add(0, digit);
                }
                for (Integer digit : digits) {
                    agnosticEncoding.add(new AgnosticSymbol(new Digit(digit), PositionsInStaff.SPACE_5));
                    agnosticEncoding.add(horizontalSeparator);
                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(digit), PositionsInStaff.SPACE_5));
                }
            }

            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.multirest, null, PositionsInStaff.LINE_3));
            //agnosticEncoding.add(new AgnosticSymbol(new Multirest(), PositionsInStaff.LINE_3));

            if (multiMeasureRest.getAtomFigure().getFermata() != null && (multiMeasureRest.getAtomFigure().getFermata().getPosition() == null || multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW)) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));

                Positions positions = convert(multiMeasureRest.getAtomFigure().getFermata().getPosition());
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(positions), FERMATA_POSITION_BELOW));
            }

            //semanticTokens.add(new SemanticToken(SemanticSymbol.multirest, Integer.toString(multiMeasureRest.getNumMeasures())));
            semanticEncoding.add(new SemanticSymbol(new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.Multirest(multiMeasureRest.getNumMeasures())));


        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            if (rest.getAtomFigure().getFermata() != null && (rest.getAtomFigure().getFermata().getPosition() == null || rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
                Positions positions = convert(rest.getAtomFigure().getFermata().getPosition());
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(positions), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();
            }

            PositionInStaff positionsInStaff;
            if (rest.getAtomFigure().getFigure() == Figures.WHOLE) {
                positionsInStaff = PositionsInStaff.LINE_4;
            } else {
                positionsInStaff = PositionsInStaff.LINE_3;
            }

            RestFigures restFigure = convert(rest.getAtomFigure().getFigure());
            agnosticEncoding.add(new AgnosticSymbol(new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest(restFigure), positionsInStaff));
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.rest, rest.getAtomFigure().getFigure().toString().toLowerCase(), positionsInStaff));

            if (rest.getAtomFigure().getFermata() != null && rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));
                Positions positions = convert(rest.getAtomFigure().getFermata().getPosition());
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(new Fermata(positions), FERMATA_POSITION_BELOW));
            } else {
                agnosticEncoding.add(horizontalSeparator);
            }

            convertDots(rest.getAtomFigure(), PositionsInStaff.SPACE_3);

            //StringBuilder sb = new StringBuilder();
            //fillSingleFigureAtom(sb, rest);
            //semanticTokens.add(new SemanticToken(SemanticSymbol.rest, sb.toString()));
            semanticEncoding.add(new SemanticSymbol(new es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.Rest(
                    rest.getAtomFigure().getFigure(),
                    rest.getAtomFigure().getDots(),
                    rest.getAtomFigure().getFermata() != null
            )));

        } else {
            throw new ExportException("Unsupported symbol conversion of: " + symbol.getClass());

        }
    }

    private MajorMinor convert(Mode mode) {
        switch (mode) {
            case MAJOR:
                return MajorMinor.major;
            case MINOR:
                return MajorMinor.minor;
            default:
                return null;
        }
    }

    private Positions convert(PositionAboveBelow position) throws IM3Exception {
        switch (position) {
            case ABOVE: return Positions.above;
            case BELOW: return Positions.below;
            default: throw new IM3Exception("Unsupported position " + position);
        }
    }

    private es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] convert(Accidentals accidental) throws IM3Exception {
        switch (accidental) {
            case FLAT: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.flat};
            case SHARP: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.sharp};
            case DOUBLE_SHARP: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.double_sharp};
            case NATURAL: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.natural};
            case DOUBLE_FLAT: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.flat,
                    es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.flat, };
            default:
                throw new IM3Exception("Unsupported accidental: " + accidental);
        }
    }

    private MeterSigns convert(SignTimeSignature symbol) throws IM3Exception {
        if (symbol instanceof TimeSignatureCommonTime) {
            return MeterSigns.C;
        } else if (symbol instanceof TimeSignatureCutTime) {
            return MeterSigns.Ccut;
        } else {
            throw new IM3Exception("Unsupported meter " + symbol);
        }
    }

    /*TODO private void fillSingleFigureAtom(StringBuilder sb, SingleFigureAtom atom) throws IM3Exception {
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
    }*/

    /*private String generateFigureString(SimpleNote note) {
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
        }*
    }*/
    private INoteDurationSpecification generateFigureString(SimpleNote note) throws IM3Exception {
        BeamGroup beam = note.getBelongsToBeam();
        if (beam != null) {
            int flags = note.getAtomFigure().getFigure().getNumFlags();
            if (beam.getFirstFigure() == note) {
                //return "beamedRight" + flags;
                return new Beam(BeamType.right, flags);
            } else if (beam.getLastFigure() == note) {
                //return "beamedLeft" + flags;
                return new Beam(BeamType.left, flags);
            } else {
                //return "beamedBoth" + flags;
                return new Beam(BeamType.both, flags);
            }
        } else {
            switch (note.getAtomFigure().getFigure()) {
                case DOUBLE_WHOLE: return NoteFigures.doubleWhole;
                case WHOLE: return NoteFigures.whole;
                case HALF: return NoteFigures.half;
                case QUARTER: return NoteFigures.quarter;
                case EIGHTH: return NoteFigures.eighth;
                case SIXTEENTH: return NoteFigures.sixteenth;
                case THIRTY_SECOND: return NoteFigures.thirtySecond;
                case SIXTY_FOURTH: return NoteFigures.sixtyFourth;
                case HUNDRED_TWENTY_EIGHTH: return NoteFigures.hundredTwentyEighth;
                default: throw new IM3Exception("Unsupported note figure: " + note.getAtomFigure().getFigure());
            }
            //return note.getAtomFigure().getFigure().toString().toLowerCase();
        }
    }

    private RestFigures convert(Figures figure) throws IM3Exception {
        switch (figure) {
            case WHOLE: return RestFigures.whole;
            case HALF: return RestFigures.half;
            case QUARTER: return RestFigures.quarter;
            case EIGHTH: return RestFigures.eighth;
            case SIXTEENTH: return RestFigures.sixteenth;
            case THIRTY_SECOND: return RestFigures.thirtySecond;
            case SIXTY_FOURTH: return RestFigures.sixtyFourth;
            case HUNDRED_TWENTY_EIGHTH: return RestFigures.hundredTwentyEighth;
            default: throw new IM3Exception("Unsupported rest figure: " + figure);
        }
    }

    public AgnosticEncoding getAgnosticEncoding() {
        return agnosticEncoding;
    }

    public SemanticEncoding getSemanticEncoding() {
        return semanticEncoding;
    }
}
