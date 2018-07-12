package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.omr.model.entities.Stroke;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

/**
 * @autor drizo
 */
public class OMRStrokes {
    ObservableList<OMRStroke> strokeList;

    public OMRStrokes() {
        strokeList = FXCollections.observableList(new LinkedList<>());
    }

    public OMRStrokes(Strokes strokes) {
        strokeList = FXCollections.observableList(new LinkedList<>());
        for (Stroke stroke: strokes.getStrokeList()) {
            strokeList.add(new OMRStroke(stroke));
        }
    }

    public void setStrokeList(ObservableList<OMRStroke> strokeList) {
        this.strokeList = strokeList;
    }

    public ObservableList<OMRStroke> strokeListProperty() {
        return strokeList;
    }

    public void addStroke(OMRStroke stroke) {
        strokeList.add(stroke);
    }

    public Strokes createPOJO() {
        Strokes strokes = new Strokes();
        for (OMRStroke omrStroke: strokeList) {
            strokes.addStroke(omrStroke.createPOJO());
        }
        return strokes;
    }
}
