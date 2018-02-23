package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.omr.classifiers.traced.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class Glyph  {
    Symbol symbol;
    LinkedList<Stroke> strokes;
    Double width;
    Double horizontalCentroid;

    public Glyph(Symbol symbol) {
        this.symbol = symbol;
        strokes = new LinkedList<>();
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Stroke> getStrokes() {
        return strokes;
    }

    public void addStroke(Stroke stroke) {
        strokes.add(stroke);
    }

    public double getWidth() {
        // just computed once
        if (width == null) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            for (Stroke stroke: strokes) {
                for (Coordinate point: stroke.getPoints()) {
                    minX = Math.min(minX, point.getX());
                    maxX = Math.max(maxX, point.getX());
                }
            }
            width = maxX - minX;
            horizontalCentroid = (maxX + minX) / 2;
        }
        return width;
    }

    public Double getHorizontalCentroid() {
        if (horizontalCentroid == null) {
            getWidth(); // it computes it
        }
        return horizontalCentroid;
    }
}
