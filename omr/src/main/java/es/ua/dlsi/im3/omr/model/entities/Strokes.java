package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxYX;

import java.util.LinkedList;
import java.util.List;

/**
 * @autor drizo
 */
public class Strokes {
    List<Stroke> strokeList;

    public Strokes() {
        strokeList = new LinkedList<>();
    }

    public List<Stroke> getStrokeList() {
        return strokeList;
    }

    public void setStrokeList(List<Stroke> strokeList) {
        this.strokeList = strokeList;
    }

    public void addStroke(Stroke stroke) {
        this.strokeList.add(stroke);
    }

    public BoundingBox computeBoundingBox() throws IM3Exception {
        double fromX = Double.MAX_VALUE;
        double fromY = Double.MAX_VALUE;
        double toX = -1;
        double toY = -1;
        for (Stroke stroke: strokeList) {
            BoundingBox strokeBoundingBox = stroke.computeBoundingBox();
            fromX = Math.min(fromX, strokeBoundingBox.getFromX());
            fromY = Math.min(fromY, strokeBoundingBox.getFromY());
            toX = Math.max(toX, strokeBoundingBox.getToX());
            toY = Math.max(toY, strokeBoundingBox.getToY());
        }
        return new BoundingBoxYX(fromX, fromY, toX, toY);
    }

    public boolean isEmpty() {
        return strokeList.isEmpty();
    }
}
