package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LayoutStaff extends NotationSymbol {
    Staff staff;
	List<NotationSymbol> notationSymbols;
	ScoreLayout scoreLayout;

    /**
     * Arranged bottom-up
     */
	List<Line> lines;
	Group group;

    public LayoutStaff(ScoreLayout scoreLayout, double width, Staff staff) {
        lines = new ArrayList<>();
        this.staff = staff;
        this.scoreLayout = scoreLayout;
        notationSymbols = new ArrayList<>();
        group = new Group();
        for (int i=0; i<staff.getLineCount(); i++) {
            //double y = LayoutConstants.STAFF_TOP_MARGIN + i*LayoutConstants.SPACE_HEIGHT;
            double y = i* LayoutConstants.SPACE_HEIGHT;
            //Line line = new Line(LayoutConstants.STAFF_LEFT_MARGIN, y, width-LayoutConstants.STAFF_RIGHT_MARGIN, y); //TODO márgenes arriba abajo
            Line line = new Line(0, y, width, y); //TODO márgenes arriba abajo - quizás mejor en el grupo en el que están on en la página
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
    public double getYAtLine(int line) throws IM3Exception {
        if (line < 1 || line > lines.size()) {
            throw new IM3Exception("Invalid line " + line + ", there are " + lines.size() + " lines");
        }
        return lines.get(line-1).getStartY();
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
    public double computeYPositionForPitchWithoutClefOctaveChange(Time time, NoteNames noteName, int octave) throws IM3Exception {
        Clef clef = staff.getRunningClefAt(time);
        return computeYPositionForPitch(clef, noteName, octave + clef.getOctaveChange());
    }

    /**
     * It returns the y position for a given diatonic pitch at a given time (for taking into account the clef changes)
     * @param clef
     * @param noteName
     * @param octave
     * @return
     * @throws IM3Exception
     */
    public double computeYPositionForPitch(Clef clef, NoteNames noteName, int octave) throws IM3Exception {
        PositionInStaff positionInStaff = staff.computeLineSpacePitch(clef, noteName, octave);
        return computeYPositionForLinespace(positionInStaff);
    }

    public double computeYPositionForLinespace(PositionInStaff positionInStaff) throws IM3Exception {
        double heightDifference = lines.get(0).getStartY() - (LayoutConstants.SPACE_HEIGHT * ((double)positionInStaff.getLineSpace()) / 2.0);
                //(double) linespace) / 2.0; // result
        return heightDifference;
    }

    public Staff getStaff() {
        return staff;
    }
}
