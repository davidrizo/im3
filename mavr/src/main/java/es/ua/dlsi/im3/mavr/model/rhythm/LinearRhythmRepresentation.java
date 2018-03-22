package es.ua.dlsi.im3.mavr.model.rhythm;

import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;
import es.ua.dlsi.im3.mavr.model.Motive;
import es.ua.dlsi.im3.mavr.model.MotiveRepresentation;

import java.util.LinkedList;
import java.util.List;

/**
 * It represents the rhythm as a sequence of lines
 */
public class LinearRhythmRepresentation extends MotiveRepresentation {
    private static final double HEIGHT = 10;
    private static final double SCALE = 10;
    List<Line> lines;

    public LinearRhythmRepresentation(Motive motive) {
        lines = new LinkedList<>();
        for (Atom atom: motive.getAtomList()) {
            for (AtomFigure figure: atom.getAtomFigures()) {
                CoordinateComponent x = new CoordinateComponent(figure.getTime().multiply(SCALE).getComputedTime());
                Coordinate coordinateFrom = new Coordinate(x, new CoordinateComponent());
                Coordinate coordinateTo = new Coordinate(x, new CoordinateComponent(HEIGHT));
                Line line = new Line(null, null, coordinateFrom, coordinateTo);
                lines.add(line);
            }
        }
    }

    public List<Line> getLines() {
        return lines;
    }


    @Override
    public List<? extends Shape> getShapes() {
        return lines;
    }
}
