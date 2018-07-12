package es.ua.dlsi.im3.omr.model.entities;

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
}
