package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LedgerLines;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class LayoutStaff extends NotationSymbol {
    private final Coordinate rightTop;
    Staff staff;
	List<NotationSymbol> notationSymbols;
	ScoreLayout scoreLayout;

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

    public LayoutStaff(ScoreLayout scoreLayout, Coordinate leftTop, Coordinate rightTop, Staff staff) {
        lines = new ArrayList<>();
        ledgerLines = new TreeMap<>();
        this.staff = staff;
        this.scoreLayout = scoreLayout;
        this.position = leftTop;
        this.rightTop = rightTop;
        notationSymbols = new ArrayList<>();
        group = new Group();
        for (int i=0; i<staff.getLineCount(); i++) {
            //double y = LayoutConstants.STAFF_TOP_MARGIN + i*LayoutConstants.SPACE_HEIGHT;
            double y = i* LayoutConstants.SPACE_HEIGHT;
            //Line line = new Line(LayoutConstants.STAFF_LEFT_MARGIN, y, width-LayoutConstants.STAFF_RIGHT_MARGIN, y); //TODO márgenes arriba abajo
            Coordinate from = new Coordinate(leftTop.getX(), new CoordinateComponent(leftTop.getY(), y));
            Coordinate to = new Coordinate(rightTop.getX(), new CoordinateComponent(leftTop.getY(), y));
            Line line = new Line(from, to); //TODO márgenes arriba abajo - quizás mejor en el grupo en el que están on en la página
            lines.add(0, line);
            group.add(0, line);
        }
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

    public void add(NotationSymbol symbol) {
        if (symbol != null) {
            if (symbol.getGraphics() == null) {
                System.err.println("TO-DO EXCEPCION symbol null para " + symbol.getClass());
            } else {
                notationSymbols.add(symbol);
                group.add(symbol.getGraphics());
            }
        } else {
            System.err.println("TO-DO EXCEPCION symbol null");
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
        Clef clef = staff.getRunningClefAt(time);
        return computeYPositionForPitch(clef, noteName, octave + clef.getOctaveChange());
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
        Clef clef = staff.getRunningClefAt(time);
        return staff.computePositionInStaff(clef, noteName, octave);
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

}
