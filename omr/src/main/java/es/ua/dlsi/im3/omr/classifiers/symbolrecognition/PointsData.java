package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.patternmatching.IEditDistanceOperations;
import es.ua.dlsi.im3.omr.model.entities.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * @autor drizo
 */
public class PointsData {
    List<Point> points;
    static IEditDistanceOperations<Point> POINT_EDIT_DISTANCE_OPERATIONS = new IEditDistanceOperations<Point>() {
        @Override
        public double insertCost(Point item) {
            return 1;
        }

        @Override
        public double deleteCost(Point item) {
            return 1;
        }

        @Override
        public double substitutionCost(Point a, Point b) {
            if (a.getX() == b.getX() && a.getY() == b.getY()) {
                return 0;
            } else {
                return 2; //TODO Costes
            }
        }
    };

    public PointsData() {
        points = new LinkedList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }


    public boolean isEmpty() {
        return points.isEmpty();
    }
}
