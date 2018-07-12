package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.model.entities.Stroke;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;

/**
 *
 * @author drizo
 */
public class OMRStroke {
    ObservableList<Point> points;
    long firstPointTime;

    public OMRStroke() {
        points = FXCollections.observableArrayList();
    }

    public OMRStroke(Stroke stroke) {
        points = FXCollections.observableArrayList();
        for (Point point: stroke.pointsProperty()) {
            points.add(new Point(point));
        }
        firstPointTime = stroke.getFirstPointTime();
    }

    public void addPoint(double x, double y) {
        if (points.isEmpty()) {
            firstPointTime = new Date().getTime();
            points.add(new Point(0, x, y));
        } else {
            points.add(new Point(new Date().getTime() - firstPointTime, x, y));
        }

    }

    public ObservableList<Point> pointsProperty() {
        return points;
    }

    @Override
    public String toString() {
        if (points.isEmpty()) {
            return "No points";
        } else {
            return "First point = " + points.get(0).toString();
        }
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public Stroke createPOJO() {
        Stroke stroke = new Stroke();
        stroke.setFirstPointTime(firstPointTime);
        for (Point point: points) {
            stroke.addPoint(new Point(point));
        }
        return stroke;
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }
}