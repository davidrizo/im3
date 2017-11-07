package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.omr.traced.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class Stroke {
    Symbol symbol;
    LinkedList<Coordinate> points;

    public Stroke() {
        points = new LinkedList<>();
    }

    public List<Coordinate> getPoints() {
        return points;
    }

    public void addPoint(Coordinate p) {
        points.add(p);
    }
}
