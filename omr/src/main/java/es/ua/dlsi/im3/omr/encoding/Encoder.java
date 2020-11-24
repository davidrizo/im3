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
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Fermata;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Slur;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticEncoding;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbol;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.*;


import java.util.*;

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
    private boolean processSystemBreaks;

    public Encoder(AgnosticVersion version, boolean processSystemBreaks) {
        this.version = version;
        this.processSystemBreaks = processSystemBreaks;
        horizontalSeparator = new HorizontalSeparator(version);
        barline = new VerticalLine(version);
        verticalSeparator = new VerticalSeparator(version);
        agnosticSystemBreak = new AgnosticSystemBreak();
        semanticSystemBreak = new SemanticSystemBreak();
    }

    public Encoder(boolean processSystemBreaks) {
        this(AgnosticVersion.v2, processSystemBreaks);
    }

    private void addVerticalSeparator() {
        agnosticEncoding.add(verticalSeparator);
    }

    private void addHorizontalSeparator() {
        agnosticEncoding.add(horizontalSeparator);
    }

    public void encode(Staff staff, Segment segment) throws IM3Exception {
        this.scoreSong = staff.getScoreSong();
        agnosticEncoding = new AgnosticEncoding();
        semanticEncoding = new SemanticEncoding();

        HashMap<AtomPitch, Accidentals> drawnAccidentals = staff.createNoteAccidentalsToShow();

        Measure lastMeasure = null;
        List<ITimedElementInStaff> coreSymbolsOrdered = staff.getCoreSymbolsOrdered();
        Time lastEndTime = null;
        boolean newSystem = false;
        boolean firstSystem = true;

        for (ITimedElementInStaff symbol : coreSymbolsOrdered) {
            if (processSystemBreaks) {
                if (staff.getParts().get(0).getPageSystemBeginnings().hasSystemBeginning(symbol.getTime())) {
                    // remove the system beginning
                    staff.getParts().get(0).getPageSystemBeginnings().removeSystemBeginning(symbol.getTime());
                    agnosticEncoding.add(agnosticSystemBreak);
                    semanticEncoding.add(semanticSystemBreak);
                }
            }

            if (segment.contains(symbol.getTime())) {
                if (processSystemBreaks && symbol instanceof SystemBreak) {
                    semanticEncoding.add(new SemanticBarline());
                    agnosticEncoding.add(new AgnosticSymbol(version, barline, PositionsInStaff.LINE_1));
                    agnosticEncoding.add(agnosticSystemBreak);
                    semanticEncoding.add(semanticSystemBreak);
                    newSystem = true;
                } else {
                    Measure measure = null;
                    if (scoreSong.hasMeasures()) {
                        measure = scoreSong.getMeasureActiveAtTime(symbol.getTime());
                    }

                    if (newSystem && processSystemBreaks) {
                        es.ua.dlsi.im3.core.score.Clef lastClef = staff.getRunningClefAt(symbol);
                        encodeClef(lastClef);

                        KeySignature lastKeySignature = staff.getRunningKeySignatureAt(symbol);
                        encodeKeySignature(lastKeySignature);
                        newSystem = false;
                        //TODO slurs
                    } else {
                        if (measure != lastMeasure && lastMeasure != null) { // lastMeasure != null for not drawing the last bar line
                            agnosticEncoding.add(new AgnosticSymbol(version, barline, PositionsInStaff.LINE_1));
                            agnosticEncoding.add(horizontalSeparator);
                            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
                            semanticEncoding.add(new SemanticBarline());
                            ////semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
                        }
                    }

                    convert(symbol, drawnAccidentals, null);

                    if (symbol instanceof Atom) {
                        lastEndTime = ((Atom) symbol).getOffset();
                    }

                    lastMeasure = measure;
                }
            }
        }

        if (lastEndTime == null) {
            throw new IM3Exception("Song without notes");
        }

        if (lastMeasure != null) { // not used in mensural
            Time measureEndTime = staff.getRunningTimeSignatureAt(lastMeasure).getDuration().add(lastMeasure.getTime());
            if (lastEndTime.equals(measureEndTime)) {
                // add bar line just if the measure is complete
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.barline, null, PositionsInStaff.LINE_1));
                agnosticEncoding.add(new AgnosticSymbol(version, barline, PositionsInStaff.LINE_1));
                agnosticEncoding.add(horizontalSeparator);
                //semanticTokens.add(new SemanticToken(SemanticSymbol.barline));
                semanticEncoding.add(new SemanticBarline());
            }
        }

        agnosticEncoding.removeLastSymbolIfSeparator();
        /*sb.append(SEPARATOR);
        sb.append(THICKBARLINE_0);*/
    }

    public void encode(ScoreSong scoreSong) throws IM3Exception {
        if (scoreSong.getStaves().size() != 1) {
            //TODO We have this information!!!
            //SemanticNote we don't have information in the MEI file about line breaking
            StringBuilder stringBuilder = new StringBuilder();

            for (Staff staff: scoreSong.getStaves()) {
                stringBuilder.append(staff.getName());
                stringBuilder.append(' ');
            }
            throw new ExportException("Currently only one staff is supported in the export format, and there are " + scoreSong.getStaves().size()
                    + " with names: " + stringBuilder.toString());
        }

        Staff staff = scoreSong.getStaves().get(0);
        encode(staff, new Segment(Time.TIME_ZERO, Time.TIME_MAX));
    }

    private void convertDuration(StringBuilder sb, AtomFigure figure) {
        sb.append(figure.getFigure().name().toLowerCase());

    }

    private void convertDots(AtomFigure figure, PositionInStaff positionInStaff) {
        for (int i = 0; i < figure.getDots(); i++) {
            agnosticEncoding.add(new AgnosticSymbol(version, dot, positionInStaff));
            agnosticEncoding.add(horizontalSeparator);
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.dot, null, positionInStaff));
        }
    }

    private void convert(ITimedElementInStaff symbol, HashMap<AtomPitch, Accidentals> drawnAccidentals, Integer tupletNumber) throws IM3Exception {
        if (symbol instanceof es.ua.dlsi.im3.core.score.Clef) {
            es.ua.dlsi.im3.core.score.Clef clef = (es.ua.dlsi.im3.core.score.Clef) symbol;
            encodeClef(clef);
        } else if (symbol instanceof KeySignature) {
            KeySignature ks = (KeySignature) symbol;
            encodeKeySignature(ks);
        } else if (symbol instanceof TimeSignature) {
            encodeTimeSignature((TimeSignature) symbol);
        } else if (symbol instanceof SimpleChord) {
            if (version == AgnosticVersion.v1) {
                throw new IM3Exception("Unsupported chords in V1");
            } else {
                SimpleChord chord = (SimpleChord) symbol;

                int i=0;
                boolean tiedFromPreviousGenerated = false;
                TreeSet<AtomPitch> sortedPitches = new TreeSet<>();
                sortedPitches.addAll(chord.getAtomPitches());

                Set<AtomPitch> sortedPitchesSet = sortedPitches.descendingSet();
                PositionInStaff [] positionInStaffs = new PositionInStaff[sortedPitchesSet.size()];
                for (AtomPitch atomPitch: sortedPitchesSet) {
                    positionInStaffs[i] = symbol.getStaff().computePositionInStaff(chord.getTime(),
                            atomPitch.getScientificPitch().getPitchClass().getNoteName(), atomPitch.getScientificPitch().getOctave());

                    if (atomPitch.isTiedFromPrevious()) {
                        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, END, positionInStaff));
                        if (tiedFromPreviousGenerated) {
                            addVerticalSeparator();
                        }
                        agnosticEncoding.add(new AgnosticSymbol(version, new Slur(StartEnd.end), positionInStaffs[i]));
                        tiedFromPreviousGenerated = true;
                    }

                    i++;
                }
                if (tiedFromPreviousGenerated) {
                    agnosticEncoding.add(horizontalSeparator);
                }

                if (chord.getAtomFigure().getFermata() != null && (chord.getAtomFigure().getFermata().getPosition() == null || chord.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                    agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(Positions.above), FERMATA_POSITION_ABOVE));
                    addVerticalSeparator();

                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, "above", PositionsInStaff.SPACE_6));
                }

                i=0;
                boolean accdidentalsGenerated = false;
                for (AtomPitch atomPitch: sortedPitches.descendingSet()) {
                    Accidentals accidentalToDraw = drawnAccidentals.get(atomPitch);
                    if (accidentalToDraw != null) {
                        es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals [] accs = convert(accidentalToDraw);
                        for (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals acc: accs) {
                            if (accdidentalsGenerated) {
                                addVerticalSeparator();
                            }
                            agnosticEncoding.add(new AgnosticSymbol(version, new Accidental(acc), positionInStaffs[i]));
                            accdidentalsGenerated = true;
                        }
                        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, accidentalToDraw.name().toLowerCase(), positionInStaff));
                    }
                    i++;
                }
                if (accdidentalsGenerated) {
                    agnosticEncoding.add(horizontalSeparator);
                }


                //String figureString = generateNoteDurationSpecification(note);
                INoteDurationSpecification figureString = generateNoteDurationSpecification(chord);

                //  GraphicalSymbol graphicalSymbol;
            /*if (note.isGrace()) {
                graphicalSymbol = GraphicalSymbol.gracenote;
            } else {
                graphicalSymbol = GraphicalSymbol.note;
            }*/

                // TODO: 18/10/17 Otras marcas
                boolean trill = false;
                if (chord.getMarks() != null) {
                    for (StaffMark mark : chord.getMarks()) {
                        if (mark instanceof Trill) {
                            agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Trill(), PositionsInStaff.SPACE_6));
                            addVerticalSeparator();
                            trill = true;
                            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.trill, null, FERMATA_POSITION_ABOVE));
                        } else {
                            throw new IM3Exception("Unsupported mark: " + mark.getClass());
                        }
                    }
                }

                for (i=0; i<positionInStaffs.length; i++) {
                    if (i>0) {
                        addVerticalSeparator();
                    }
                    if (chord.isGrace()) {
                        agnosticEncoding.add(new AgnosticSymbol(version, new GraceNote(figureString), positionInStaffs[i]));
                    } else {
                        agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note(figureString), positionInStaffs[i]));
                    }
                }
                //graphicalTokens.add(new GraphicalToken(graphicalSymbol, figureString, positionInStaff));

                if (chord.getAtomFigure().getFermata() != null && chord.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));
                    addVerticalSeparator();
                    agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(Positions.below), FERMATA_POSITION_BELOW));
                } else {
                    agnosticEncoding.add(horizontalSeparator);
                }

                if (chord.getAtomFigure().getDots()>0) {
                    for (i = 0; i < positionInStaffs.length; i++) {
                        if (i > 0) {
                            addVerticalSeparator();
                        }
                        PositionInStaff dotPositionInStaff;
                        if (positionInStaffs[i].laysOnLine()) {
                            dotPositionInStaff = positionInStaffs[i].move(1);
                        } else {
                            dotPositionInStaff = positionInStaffs[i];
                        }
                        agnosticEncoding.add(new AgnosticSymbol(version, dot, dotPositionInStaff));
                    }
                    agnosticEncoding.add(horizontalSeparator);
                }

                boolean tiedToNextGenerated = false;
                i=0;
                for (AtomPitch atomPitch: chord.getAtomPitches()) {
                    if (atomPitch.isTiedToNext()) {
                        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, START, positionInStaff));
                        if (tiedToNextGenerated) {
                            addVerticalSeparator();
                        }
                        agnosticEncoding.add(new AgnosticSymbol(version, new Slur(StartEnd.start), positionInStaffs[i]));
                        tiedToNextGenerated = true;
                    }
                    i++;
                }

                if (tiedToNextGenerated) {
                    agnosticEncoding.add(horizontalSeparator);
                }




                /*for (AtomPitch atomPitch: chord.getAtomPitches()) {
                    SemanticNote semanticNote =
                            new SemanticNote(chord.isGrace(), atomPitch.getScientificPitch(), chord.getAtomFigure().getFigure(), chord.getAtomFigure().getDots(),
                                    chord.getAtomFigure().getFermata() != null,
                                    trill, tupletNumber);
                    semanticEncoding.add(semanticNote);
                }

                i=0;
                for (AtomPitch atomPitch: chord.getAtomPitches()) {
                    if (atomPitch.isTiedToNext()) {
                        semanticEncoding.add(new SemanticTie(null))); //TODO Esto no está bien
                        //semanticTokens.add(new SemanticToken(SemanticSymbol.tie));
                    }
                }*/

                //TODO ties
                semanticEncoding.add(new SemanticChord(chord));
            }
        } else if (symbol instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) symbol;

            PositionInStaff positionInStaff = symbol.getStaff().computePositionInStaff(note.getTime(),
                    note.getPitch().getPitchClass().getNoteName(), note.getPitch().getOctave());

            if (note.getAtomPitch().isTiedFromPrevious()) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.slur, END, positionInStaff));
                agnosticEncoding.add(new AgnosticSymbol(version, new Slur(StartEnd.end), positionInStaff));
                agnosticEncoding.add(horizontalSeparator);
            }

            if (note.getAtomFigure().getFermata() != null && (note.getAtomFigure().getFermata().getPosition() == null || note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(Positions.above), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();

                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, "above", PositionsInStaff.SPACE_6));
            }



            Accidentals accidentalToDraw = drawnAccidentals.get(note.getAtomPitch());
            if (accidentalToDraw != null) {
                es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals [] accs = convert(accidentalToDraw);
                for (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals acc: accs) {
                    agnosticEncoding.add(new AgnosticSymbol(version, new Accidental(acc), positionInStaff));
                    agnosticEncoding.add(horizontalSeparator);
                }
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.accidental, accidentalToDraw.name().toLowerCase(), positionInStaff));
            }

            //String figureString = generateNoteDurationSpecification(note);
            INoteDurationSpecification noteDurationSpecification = generateNoteDurationSpecification(note);

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
                        agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Trill(), PositionsInStaff.SPACE_6));
                        addVerticalSeparator();
                        trill = true;
                        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.trill, null, FERMATA_POSITION_ABOVE));
                    } else {
                        throw new IM3Exception("Unsupported mark: " + mark.getClass());
                    }
                }
            }

            Directions directions = null;
            if (note.getDuration().getComputedTime() <= Figures.HALF.getDuration().getComputedTime()) {
                if (note.getExplicitStemDirection() != null && note.getExplicitStemDirection() != StemDirection.computed && note.getExplicitStemDirection() != StemDirection.none) {
                    if (note.getExplicitStemDirection() == StemDirection.down) {
                        directions = Directions.down;
                    } else {
                        directions = Directions.up;
                    }
                } else {
                    if (positionInStaff.compareTo(PositionsInStaff.LINE_3) < 0) {
                        directions = Directions.up;
                    } else {
                        directions = Directions.down;
                    }
                }
            }

            if (note.isGrace()) {
                agnosticEncoding.add(new AgnosticSymbol(version, new GraceNote(noteDurationSpecification, directions), positionInStaff));
            } else {
                agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note(noteDurationSpecification, directions), positionInStaff));
            }
            //graphicalTokens.add(new GraphicalToken(graphicalSymbol, figureString, positionInStaff));

            if (note.getAtomFigure().getFermata() != null && note.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, note.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(Positions.below), FERMATA_POSITION_BELOW));
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
                agnosticEncoding.add(new AgnosticSymbol(version, new Slur(StartEnd.start), positionInStaff));
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

            /*SemanticNote semanticNote =
                    new SemanticNote(note.isGrace(), note.getPitch(), note.getAtomFigure().getFigure(), note.getAtomFigure().getDots(),
                            note.getAtomFigure().getFermata() != null,
                            trill, tupletNumber);
            semanticEncoding.add(semanticNote));


            if (note.getAtomPitch().isTiedToNext()) {
                semanticEncoding.add(new SemanticTie(null)));
                //semanticTokens.add(new SemanticToken(SemanticSymbol.tie));
            }*/
            //TODO ties
            semanticEncoding.add(new SemanticNote(note));

        } else if (symbol instanceof SimpleMultiMeasureRest) {
            SimpleMultiMeasureRest multiMeasureRest = (SimpleMultiMeasureRest) symbol;
            int n = multiMeasureRest.getNumMeasures();

            if (n > 2) { // the digit is placed on top of the symbol by Verovio but after the physical start of multirest
                agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Multirest(), PositionsInStaff.LINE_3));
                agnosticEncoding.add(horizontalSeparator);
            }

            if (multiMeasureRest.getAtomFigure().getFermata() != null && multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE) {
                Positions positions = convert(multiMeasureRest.getAtomFigure().getFermata().getPosition());
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(positions), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
            }

            if (n <= 2) { // the digit is placed on top of the symbol by Verovio
                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(n), PositionsInStaff.SPACE_5));
                addVerticalSeparator();
                if (n == 1) {
                    agnosticEncoding.add(new AgnosticSymbol(version, new Rest(RestFigures.whole), PositionsInStaff.LINE_4)); // Verovio encodes these rests this way
                } else {
                    agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest(RestFigures.breve), PositionsInStaff.LINE_3)); // Verovio encodes these rests this way
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
                    agnosticEncoding.add(new AgnosticSymbol(version, new Digit(digit), PositionsInStaff.SPACE_5));
                    agnosticEncoding.add(horizontalSeparator);
                    //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(digit), PositionsInStaff.SPACE_5));
                }
            }

            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.multirest, null, PositionsInStaff.LINE_3));
            //agnosticEncoding.add(new AgnosticSymbol(new SemanticMultirest(), PositionsInStaff.LINE_3));

            if (multiMeasureRest.getAtomFigure().getFermata() != null && (multiMeasureRest.getAtomFigure().getFermata().getPosition() == null || multiMeasureRest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW)) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, multiMeasureRest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));

                Positions positions = convert(multiMeasureRest.getAtomFigure().getFermata().getPosition());
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(positions), FERMATA_POSITION_BELOW));
            }

            //semanticTokens.add(new SemanticToken(SemanticSymbol.multirest, Integer.toString(multiMeasureRest.getNumMeasures())));
            //semanticEncoding.add(new SemanticMultirest(multiMeasureRest.getNumMeasures())));
            semanticEncoding.add(new SemanticMultirest(multiMeasureRest));


        } else if (symbol instanceof SimpleRest) {
            SimpleRest rest = (SimpleRest) symbol;
            if (rest.getAtomFigure().getFermata() != null && (rest.getAtomFigure().getFermata().getPosition() == null || rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.ABOVE)) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_ABOVE));
                Positions positions = convert(rest.getAtomFigure().getFermata().getPosition());
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(positions), FERMATA_POSITION_ABOVE));
                addVerticalSeparator();
            }

            PositionInStaff positionsInStaff;
            if (rest.getAtomFigure().getFigure() == Figures.WHOLE) {
                positionsInStaff = PositionsInStaff.LINE_4;
            } else {
                positionsInStaff = PositionsInStaff.LINE_3;
            }

            RestFigures restFigure = convert(rest.getAtomFigure().getFigure());
            agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest(restFigure), positionsInStaff));
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.rest, rest.getAtomFigure().getFigure().toString().toLowerCase(), positionsInStaff));

            if (rest.getAtomFigure().getFermata() != null && rest.getAtomFigure().getFermata().getPosition() == PositionAboveBelow.BELOW) {
                //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.fermata, rest.getAtomFigure().getFermata().getPositionInStaff().toString().toLowerCase(), FERMATA_POSITION_BELOW));
                Positions positions = convert(rest.getAtomFigure().getFermata().getPosition());
                addVerticalSeparator();
                agnosticEncoding.add(new AgnosticSymbol(version, new Fermata(positions), FERMATA_POSITION_BELOW));
            } else {
                agnosticEncoding.add(horizontalSeparator);
            }

            convertDots(rest.getAtomFigure(), PositionsInStaff.SPACE_3);

            //StringBuilder sb = new StringBuilder();
            //fillSingleFigureAtom(sb, rest);
            //semanticTokens.add(new SemanticToken(SemanticSymbol.rest, sb.toString()));
            /*semanticEncoding.add(new SemanticRest(
                    rest.getAtomFigure().getFigure(),
                    rest.getAtomFigure().getDots(),
                    rest.getAtomFigure().getFermata() != null,
                    tupletNumber
            )));*/
            semanticEncoding.add(new SemanticRest(rest));

        } else if (symbol instanceof SimpleTuplet) {
            SimpleTuplet simpleTuplet = (SimpleTuplet) symbol;
            int cardinality = simpleTuplet.getCardinality();

            PositionInStaff tupletPositionInStaff = computePositionInStaff(simpleTuplet);
            boolean tupleDigitAboveStaff = tupletPositionInStaff.compareTo(PositionsInStaff.LINE_3) > 0;
            tupletNumber = simpleTuplet.getCardinality();
            //if (simpleTuplet.getEachFigure().getMeterUnit() <= Figures.QUARTER.getMeterUnit()) {
            boolean inBeam = false;
            if (simpleTuplet.getAtoms().get(0) instanceof SingleFigureAtom) {
                inBeam = ((SingleFigureAtom)simpleTuplet.getAtoms().get(0)).getBelongsToBeam() != null;
            } else {
                throw new IM3Exception("Unsupported compound tuplets");
            }

            int i;
            int middle;
            int end;

            if (tupleDigitAboveStaff) {
                i = 0;
                middle = cardinality / 2;
                end = cardinality-1;
            } else {
                convert(simpleTuplet.getAtoms().get(0), drawnAccidentals, tupletNumber);
                i = 1;
                middle = cardinality / 2 + 1;
                end = cardinality;
            }

            if (!inBeam) {
                // if quarter, half,.... or eighth without beam - it prints brackets
                if (!tupleDigitAboveStaff) {
                    agnosticEncoding.removeLastSymbolIfSeparator();
                }
                agnosticEncoding.add(new AgnosticSymbol(version, new Bracket(StartEnd.start), tupletPositionInStaff));

                if (!tupleDigitAboveStaff) {
                    addHorizontalSeparator();
                }

                for (; i< middle ; i++) {
                    convert(simpleTuplet.getAtoms().get(i), drawnAccidentals, tupletNumber);
                }

                if (!tupleDigitAboveStaff) {
                    agnosticEncoding.removeLastSymbolIfSeparator();
                }

                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(cardinality), tupletPositionInStaff));

                if (!tupleDigitAboveStaff) {
                    addHorizontalSeparator();
                }

                for (; i<end; i++) {
                    convert(simpleTuplet.getAtoms().get(i), drawnAccidentals, tupletNumber);
                }

                if (!tupleDigitAboveStaff) {
                    agnosticEncoding.removeLastSymbolIfSeparator();
                }

                agnosticEncoding.add(new AgnosticSymbol(version, new Bracket(StartEnd.end), tupletPositionInStaff));

                if (!tupleDigitAboveStaff) {
                    addHorizontalSeparator();
                }

                if (tupleDigitAboveStaff) {
                    addVerticalSeparator();
                    convert(simpleTuplet.getAtoms().get(cardinality - 1), drawnAccidentals, tupletNumber);
                }

                //TODO SEMANTIC
            } else {
                // if 8th, 16th... - it does not print brackets
                for (; i<middle; i++) {
                    convert(simpleTuplet.getAtoms().get(i), drawnAccidentals, tupletNumber);
                }

                if (!tupleDigitAboveStaff) {
                    agnosticEncoding.removeLastSymbolIfSeparator();
                }
                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(cardinality), tupletPositionInStaff));

                if (!tupleDigitAboveStaff) {
                    addHorizontalSeparator();
                }

                for (; i<cardinality; i++) {
                    convert(simpleTuplet.getAtoms().get(i), drawnAccidentals, tupletNumber);
                }
            }
        } else if (!(symbol instanceof StaffTimedPlaceHolder)) {
            throw new ExportException("Unsupported symbol conversion of: " + symbol.getClass());

        }
    }


    private void encodeClef(es.ua.dlsi.im3.core.score.Clef clef) throws IM3Exception {
        PositionInStaff positionInStaff = PositionInStaff.fromLine(clef.getLine());
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
        agnosticEncoding.add(new AgnosticSymbol(version, new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Clef(clefNote), positionInStaff));
        agnosticEncoding.add(horizontalSeparator);
        //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.clef, clef.getNote().name(), positionInStaff));
        //semanticTokens.add(new SemanticToken(SemanticSymbol.clef, clef.getNote() + "" + clef.getLine()));
        //semanticEncoding.add(new SemanticClef(clefNote, clef.getLine())));
        semanticEncoding.add(new SemanticClef(clef));
    }

    private void encodeTimeSignature(TimeSignature symbol) throws IM3Exception {
        SemanticTimeSignature<TimeSignature> timeSignature;

        if (symbol instanceof SignTimeSignature) {
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.metersign, ((SignTimeSignature) symbol).getSignString(), PositionsInStaff.LINE_3));
            MeterSigns meterSign = convert((SignTimeSignature) symbol);
            agnosticEncoding.add(new AgnosticSymbol(version, new MeterSign(meterSign), PositionsInStaff.LINE_3));
            agnosticEncoding.add(horizontalSeparator);
            //timeSignature = new SemanticMeterSignTimeSignature(meterSign);
            //sb.append(((SignTimeSignature) symbol).getSignString());
            semanticEncoding.add(new SemanticMeterSignTimeSignature((SignTimeSignature) symbol));
        } else if (symbol instanceof FractionalTimeSignature) {
            FractionalTimeSignature ts = (FractionalTimeSignature) symbol;
            if (ts.getNumerator() > 10) {
                if (ts.getNumerator() > 100) {
                    throw new IM3Exception("Unsupported meter: " + ts);
                }
                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(ts.getNumerator() / 10), PositionsInStaff.LINE_4));
                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(ts.getNumerator() % 10), PositionsInStaff.LINE_4));

            } else {
                agnosticEncoding.add(new AgnosticSymbol(version, new Digit(ts.getNumerator()), PositionsInStaff.LINE_4));
            }

            addVerticalSeparator();
            agnosticEncoding.add(new AgnosticSymbol(version, new Digit(ts.getDenominator()), PositionsInStaff.LINE_2));
            agnosticEncoding.add(horizontalSeparator);
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getNumerator()), PositionsInStaff.LINE_4));
            //graphicalTokens.add(new GraphicalToken(GraphicalSymbol.digit, Integer.toString(ts.getDenominator()), PositionsInStaff.LINE_2));
            //sb.append(ts.getNumerator());
            //sb.append('/');
            //sb.append(ts.getDenominator());
            //timeSignature = new SemanticFractionalTimeSignature(ts.getNumerator(), ts.getDenominator());
            semanticEncoding.add(new SemanticFractionalTimeSignature((FractionalTimeSignature) symbol));
        } else {
            throw new ExportException("Unsupported time signature" + symbol.getClass());
        }


        //semanticTokens.add(new SemanticToken(SemanticSymbol.timeSignature, sb.toString()));
        //semanticEncoding.add(timeSignature));

    }

    private void encodeKeySignature(KeySignature ks) throws IM3Exception {
        PositionInStaff[] positions = ks.computePositionsOfAccidentals();
        if (positions != null) {
            boolean first = true;
            for (PositionInStaff position : positions) {
                es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals [] accs = convert(ks.getAccidental());
                for (es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals acc: accs) {
                    agnosticEncoding.add(new AgnosticSymbol(version, new Accidental(acc), position));
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
        /*semanticEncoding.add(new SemanticKeySignature(
                ks.getConcertPitchKey().getPitchClass().getNoteName(),
                ks.getConcertPitchKey().getPitchClass().getAccidental(),
                convert(ks.getConcertPitchKey().getMode()))));*/
        //semanticTokens.add(new SemanticToken(SemanticSymbol.keySignature, sb.toString()));
        semanticEncoding.add(new SemanticKeySignature(ks));
    }

    private PositionInStaff computePositionInStaff(SimpleTuplet simpleTuplet) throws IM3Exception {
        //TODO si está forzado el stem arriba o abajo
        int averageLineSpace = 0;
        int count = 0;
        for (AtomPitch atomPitch: simpleTuplet.getAtomPitches()) {
            PositionInStaff positionInStaff = atomPitch.getStaff().computePositionInStaff(atomPitch.getTime(),
                    atomPitch.getScientificPitch().getPitchClass().getNoteName(), atomPitch.getScientificPitch().getOctave());
            averageLineSpace += positionInStaff.getLineSpace();
            count++;
        }

        averageLineSpace /= count;
        int line3 = PositionsInStaff.LINE_3.getLineSpace();
        if (averageLineSpace >= line3) {
            return PositionsInStaff.SPACE_MINUS_1;
        } else {
            return PositionsInStaff.SPACE_6;
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
            case DOUBLE_SHARP: return new es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals[] {es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Accidentals.doublesharp};
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

    /*private String generateNoteDurationSpecification(SimpleNote note) {
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
    private INoteDurationSpecification generateNoteDurationSpecification(SingleFigureAtom singleFigureAtom) {
        BeamGroup beam = singleFigureAtom.getBelongsToBeam();
        if (beam != null) {
            int flags = singleFigureAtom.getAtomFigure().getFigure().getNumFlags();
            if (beam.getFirstFigure() == singleFigureAtom) {
                //return "beamedRight" + flags;
                return new Beam(BeamType.right, flags);
            } else if (beam.getLastFigure() == singleFigureAtom) {
                //return "beamedLeft" + flags;
                return new Beam(BeamType.left, flags);
            } else {
                //return "beamedBoth" + flags;
                return new Beam(BeamType.both, flags);
            }
        } else {
            switch (singleFigureAtom.getAtomFigure().getFigure()) {
                // MODERN
                case DOUBLE_WHOLE: return NoteFigures.doubleWhole;
                case WHOLE: return NoteFigures.whole;
                case HALF: return NoteFigures.half;
                case QUARTER: return NoteFigures.quarter;
                case EIGHTH: return NoteFigures.eighth;
                case SIXTEENTH: return NoteFigures.sixteenth;
                case THIRTY_SECOND: return NoteFigures.thirtySecond;
                case SIXTY_FOURTH: return NoteFigures.sixtyFourth;
                case HUNDRED_TWENTY_EIGHTH: return NoteFigures.hundredTwentyEighth;

                // MENSURAL
                case BREVE: return NoteFigures.whole;
                default:
                    //TODO URGENT
                    System.err.println("TO-DO mensural!!!!!!!!!!!");
                    return NoteFigures.twoHundredFiftySix;
                    // throw new IM3Exception("Unsupported note figure: " + singleFigureAtom.getAtomFigure().getFigure());
            }
            //return note.getAtomFigure().getFigure().toString().toLowerCase();
        }
    }

    private RestFigures convert(Figures figure) {

        switch (figure) {
            // MODERN
            case WHOLE: return RestFigures.whole;
            case HALF: return RestFigures.half;
            case QUARTER: return RestFigures.quarter;
            case EIGHTH: return RestFigures.eighth;
            case SIXTEENTH: return RestFigures.sixteenth;
            case THIRTY_SECOND: return RestFigures.thirtySecond;
            case SIXTY_FOURTH: return RestFigures.sixtyFourth;
            case HUNDRED_TWENTY_EIGHTH: return RestFigures.hundredTwentyEighth;

            // MENSURAL
            case BREVE: return RestFigures.whole;
            default:
                //TODO URGENT
                System.err.println("TO-DO mensural!!!!!!!!!!!");
                return RestFigures.twoHundredFiftySix;
                //throw new IM3Exception("Unsupported rest figure: " + figure);
        }
    }

    public AgnosticEncoding getAgnosticEncoding() {
        return agnosticEncoding;
    }

    public SemanticEncoding getSemanticEncoding() {
        return semanticEncoding;
    }
}
