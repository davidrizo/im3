package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LedgerLines;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.Text;

import java.util.*;

public class LayoutStaff extends NotationSymbol {
    private final Coordinate rightTop;
    Staff staff;
	TreeSet<LayoutCoreSymbolInStaff> layoutSymbolsInStaff;
	ScoreLayout scoreLayout;
	Text staffName;

    /**
     *
     * The key corresponds to the time it is set, the value is the set of ledger
     * lines
     */
    protected TreeMap<Time, LedgerLines> ledgerLines;
    //TODO protected TreeMap<Long, Fermate> fermate;

    /**
     * Arranged bottom-up
     */
	List<Line> lines;
	Group group;


    public LayoutStaff(ScoreLayout scoreLayout, Coordinate leftTop, Coordinate rightTop, Staff staff) throws IM3Exception {
        lines = new ArrayList<>();
        ledgerLines = new TreeMap<>();
        this.staff = staff;
        this.scoreLayout = scoreLayout;
        this.position = leftTop;
        this.rightTop = rightTop;
        //layoutSymbolsInStaff = new TreeSet<>(LayoutCoreSymbolComparator.getInstance());
        layoutSymbolsInStaff = new TreeSet<>();

        group = new Group(InteractionElementType.staff); //TODO IDS

        double staffNameWidth = 0;

        for (int i=0; i<staff.getLineCount(); i++) {
            //double y = LayoutConstants.STAFF_TOP_MARGIN + i*LayoutConstants.SPACE_HEIGHT;
            double y = i* LayoutConstants.SPACE_HEIGHT;
            //Line line = new Line(LayoutConstants.STAFF_LEFT_MARGIN, y, width-LayoutConstants.STAFF_RIGHT_MARGIN, y); //TODO márgenes arriba abajo
            //if (staff.getName() != null) {
              //  staffNameWidth = LayoutConstants.STAFF_NAME_WIDTH;
            //}
            Coordinate from = new Coordinate(new CoordinateComponent(leftTop.getX(), staffNameWidth), new CoordinateComponent(leftTop.getY(), y));
            Coordinate to = new Coordinate(rightTop.getX(), new CoordinateComponent(leftTop.getY(), y));
            Line line = new Line(InteractionElementType.staffLine, from, to); //TODO márgenes arriba abajo - quizás mejor en el grupo en el que están on en la página
            //TODO IDS
            lines.add(0, line);
            group.add(0, line);
        }

        //TODO
       /* if (staff.getName() != null) {
            Coordinate staffNamePosition = new Coordinate(
                    null,
                    lines.get(lines.size()/2).getPosition().getY()
            );

            staffName = new Text("NAME-", scoreLayout.getLayoutFont(staff), staff.getName(), staffNamePosition);
            staffNamePosition.getX().setDisplacement(staffNameWidth - staffName.getWidth() - 35); // TODO: 2/11/17 -35 a piñón
            group.add(staffName);

        }*/
    }

    public List<Line> getLines() {
        return lines;
    }

    public ScoreLayout getScoreLayout() {
        return scoreLayout;
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }


    public Line getTopLine() {
        return lines.get(lines.size()-1);
    }

    public Line getBottomLine() {
        return lines.get(0);
    }

    public void add(LayoutCoreSymbolInStaff symbol) throws IM3Exception {
        if (symbol != null) {
            if (symbol.getGraphics() == null) {
                throw new IM3Exception("The symbol " + symbol + " has not a graphics element associated");
            } else {
                layoutSymbolsInStaff.add(symbol);
                group.add(symbol.getGraphics());
                symbol.setLayoutStaff(this);
            }
        } else {
            throw new IM3Exception("The symbol is null");
        }
    }

    /**
     *
     * @param line Bottom line is 1, in a pentagram, top line is 5
     * @return
     * @throws IM3Exception
     */
    public CoordinateComponent getYAtLine(int line) throws IM3Exception {
        if (line < 1 || line > lines.size()) {
            throw new IM3Exception("Invalid line " + line + ", there are " + lines.size() + " lines");
        }
        return lines.get(line-1).getFrom().getY();
    }

    public CoordinateComponent getYAtCenterLine() {
        return lines.get(lines.size()/2).getFrom().getY();
    }


    /**
     * It returns the y position for a given diatonic pitch at a given time (for taking into account the clef changes)
     * without taking into account the octave change. Used usually for key signatures
     * @param time
     * @param noteName
     * @param octave
     * @return
     * @throws IM3Exception
     */
    public CoordinateComponent computeYPositionForPitchWithoutClefOctaveChange(Time time, DiatonicPitch noteName, int octave) throws IM3Exception {
        PositionInStaff positionInStaff = staff.computePositionForPitchWithoutClefOctaveChange(time, noteName, octave);

        return computeYPosition(positionInStaff);
    }

    /**
     * It returns the y position for a given diatonic pitch at a given time (for taking into account the clef changes)
     * @param time
     * @param noteName
     * @param octave
     * @return
     * @throws IM3Exception
     */
    public CoordinateComponent computeYPositionForPitch(Time time, DiatonicPitch noteName, int octave) throws IM3Exception {
        Clef clef = staff.getRunningClefAt(time);
        return computeYPositionForPitch(clef, noteName, octave);
    }

    /**
     * It returns the y position for a given diatonic pitch at a given time (for taking into account the clef changes)
     * @param clef
     * @param noteName
     * @param octave
     * @return
     * @throws IM3Exception
     */
    public CoordinateComponent computeYPositionForPitch(Clef clef, DiatonicPitch noteName, int octave) throws IM3Exception {
        PositionInStaff positionInStaff = staff.computePositionInStaff(clef, noteName, octave);
        return computeYPosition(positionInStaff);
    }

    public PositionInStaff computePositionInStaff(Time time, DiatonicPitch noteName, int octave) throws IM3Exception {
        return staff.computePositionInStaff(time, noteName, octave);
    }

    public CoordinateComponent computeYPosition(PositionInStaff positionInStaff) throws IM3Exception {
        double heightDifference = -(LayoutConstants.SPACE_HEIGHT * ((double)positionInStaff.getLineSpace()) / 2.0);
        return new CoordinateComponent(lines.get(0).getFrom().getY(), heightDifference);
    }

    public Staff getStaff() {
        return staff;
    }

    public TreeMap<Time, LedgerLines> getLedgerLines() {
        return ledgerLines;
    }

    public LedgerLines getLedgerLineOrNullFor(Time time) throws IM3Exception {
        return ledgerLines.get(time);
    }

    public void addNecessaryLedgerLinesFor(Time time, PositionInStaff positionInStaff, Coordinate noteHeadsPosition, double noteHeadWidth) throws IM3Exception {
        int nLedgerLines = staff.computeNumberLedgerLinesNeeded(positionInStaff);
        if (nLedgerLines != 0) {
            addLedgerLines(time, nLedgerLines > 0 ? nLedgerLines : -nLedgerLines,
                    nLedgerLines > 0 ? PositionAboveBelow.BELOW : PositionAboveBelow.ABOVE,
                    noteHeadsPosition, noteHeadWidth);
        }
    }

    public void addLedgerLines(Time time, int numberOfLines, PositionAboveBelow positionAboveBelow, Coordinate noteHeadsPosition, double noteHeadWidth) throws IM3Exception {
        if (numberOfLines != 0) {
            LedgerLines ll = this.ledgerLines.get(time);
            if (ll == null) {
                LedgerLines object = new LedgerLines(this, noteHeadsPosition, noteHeadWidth, positionAboveBelow, numberOfLines);
                ledgerLines.put(time, object);
                group.add(object.getGraphics());
                //this.addMark(object);
            } else {
                ll.ensure(numberOfLines, positionAboveBelow);
            }
        }
    }

    //20180208 This cannot be computed fromTime - toTime to take into account previous context. public void createNoteAccidentals(Time from, Time to) throws IM3Exception {
    public void createNoteAccidentals() throws IM3Exception {
        //20180208 HashMap<AtomPitch, Accidentals> requiredAccidentalsMap = staff.createNoteAccidentalsToShow(from, to);
        HashMap<AtomPitch, Accidentals> requiredAccidentalsMap = staff.createNoteAccidentalsToShow();
        for (LayoutCoreSymbol symbol : this.layoutSymbolsInStaff) {
            if (symbol instanceof LayoutCoreSingleFigureAtom) {
                LayoutCoreSingleFigureAtom layoutSingleFigureAtom = (LayoutCoreSingleFigureAtom) symbol;
                for (NotePitch notePitch : layoutSingleFigureAtom.getNotePitches()) {
                    //System.out.println(">>>" + notePitch.getAtomPitch().hashCode());
                    Accidentals requiredAccidental = requiredAccidentalsMap.get(notePitch.getAtomPitch());
                    Accidental psAccidental = notePitch.getAccidental();
                    if (psAccidental != null && !psAccidental.getAccidental().equals(requiredAccidental)) {
                        notePitch.removeAccidental();
                        psAccidental = null;
                    }
                    if (psAccidental == null && requiredAccidental != null) {
                        notePitch.addAccidental(requiredAccidental);
                    } // else it is already the one we need
                }
            }
        }
    }
    /*//20180208 This cannot be computed fromTime - toTime to take into account previous context.  public void createNoteAccidentals() throws IM3Exception {
        createNoteAccidentals(Time.TIME_ZERO, Time.TIME_MAX);
    }*/

    public TreeSet<LayoutCoreSymbolInStaff> getLayoutSymbolsInStaff() {
        return layoutSymbolsInStaff;
    }


    /*public void createNoteAccidentals(Time timeZero, Time timeMax) throws IM3Exception {
        TreeMap<DiatonicPitch, ScientificPitch> alteredDiatonicPitchInBar = new TreeMap<>();
        TreeMap<DiatonicPitch, PitchClass> alteredDiatonicPitchInKeySignature = new TreeMap<>();
        KeySignature currentKeySignature = null; // getRunningKeySignatureAt(fromTime);

        // alteredDiatonicPitchInKeySignature =
        // currentKeySignature.getScoreElement().getAlteredDiatonicPitchSet();

        for (LayoutCoreSymbol symbol : this.layoutSymbolsInStaff) {
            if (symbol instanceof LayoutCoreBarline) {
                alteredDiatonicPitchInBar.clear();
            } else if (symbol instanceof LayoutCoreKeySignature) {
                LayoutCoreKeySignature lks = (LayoutCoreKeySignature) symbol;
                currentKeySignature = lks.getCoreSymbol();
                alteredDiatonicPitchInKeySignature = currentKeySignature.getAlteredDiatonicPitchSet();
            } else if (symbol instanceof LayoutCoreSingleFigureAtom) {
                LayoutCoreSingleFigureAtom layoutSingleFigureAtom = (LayoutCoreSingleFigureAtom) symbol;
                for (NotePitch notePitch: layoutSingleFigureAtom.getNotePitches()) {
                    computeRequiredAccidentalsForPitch(alteredDiatonicPitchInBar, alteredDiatonicPitchInKeySignature, notePitch);
                }
            }
        }
    }

    void computeRequiredAccidentalsForPitch(TreeMap<DiatonicPitch, ScientificPitch> alteredNoteNamesInBar,
                                            TreeMap<DiatonicPitch, PitchClass> alteredNoteNamesInKeySignature, NotePitch ps) throws IM3Exception {
        ScientificPitch pc = ps.getScientificPitch();
        if (!alteredNoteNamesInBar.containsValue(pc)) { // if not previously altered
            Accidentals requiredAccidental = computeRequiredAccidental(alteredNoteNamesInKeySignature,
                    pc.getPitchClass());
            if (requiredAccidental != null) {
                Accidental psAccidental = ps.getAccidental();
                if (psAccidental != null && !psAccidental.getAccidental().equals(requiredAccidental)) {
                    ps.removeAccidental();
                }
                if (psAccidental == null) {
                    alteredNoteNamesInBar.put(pc.getPitchClass().getNoteName(), pc);
                    ps.addAccidental(requiredAccidental);
                } // else it is already the one we need
            }
        }
    }

    private Accidentals computeRequiredAccidental(TreeMap<DiatonicPitch, PitchClass> alteredSet, PitchClass pc) {
        // needs accidental?
        Accidentals requiredAccidental = null;
        PitchClass pcInKey = alteredSet.get(pc.getNoteName());
        if (pcInKey != null) { // altered note name in key signature
            if (!pc.equals(pcInKey)) { // alteration not valid for this pitch
                // class
                if (pc.getAccidental() == null || pc.getAccidental() == Accidentals.NATURAL) {
                    requiredAccidental = Accidentals.NATURAL;
                } else {
                    requiredAccidental = pc.getAccidental(); // either flat or
                    // sharp
                }
            }
        } else if (pc.getAccidental() != null && pc.getAccidental() != Accidentals.NATURAL) {
            requiredAccidental = pc.getAccidental(); // either flat or sharp
        }
        return requiredAccidental;
    }*/
}
