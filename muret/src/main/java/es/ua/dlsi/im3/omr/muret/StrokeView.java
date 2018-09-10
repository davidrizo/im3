package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.muret.model.OMRStroke;
import javafx.collections.ListChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Lines are added through the observation of the OMRStroke points
 * @autor drizo
 */
public class StrokeView extends Path {
    private static final double STROKE_WIDTH = 2;
    OMRStroke omrStroke;
    double lastX;
    double lastY;

    public StrokeView(OMRStroke omrStroke, Color strokesColor) {
        this.omrStroke = omrStroke;
        this.setStroke(strokesColor);
        this.setStrokeWidth(STROKE_WIDTH);
        lastX = -1;
        lastY = -1;

        for (Point p : omrStroke.pointsProperty()) {
            drawLine(p.getX(), p.getY()); // TODO Podríamos pintar la velocidad con colores
        }

        omrStroke.pointsProperty().addListener(new ListChangeListener<Point>() {
                @Override
                public void onChanged(Change<? extends Point> c) {
                    //System.out.println(this.toString() + " " + c.toString());
                    while (c.next()) { // use alwaus this scheme
                        if (c.wasPermutated()) {
                            for (int i = c.getFrom(); i < c.getTo(); ++i) {
                                //permutate //TOO
                            }
                        } else if (c.wasUpdated()) {
                            //update item //TODO
                        } else {
                            // remove
                            for (Point remitem : c.getRemoved()) {
                                //remitem.remove(Outer.this); //TODO
                            }
                            // add
                            for (Point newPoint : c.getAddedSubList()) {
                                drawLine(newPoint.getX(), newPoint.getY());
                            }
                        }
                    }
                }
            }
        );
    }

    /*void addPoint(double x, double y) {
        omrStroke.addPoint(x, y);
    }*/

    final public void drawLine(double x, double y) {
        if (lastX != -1) {
            getElements().add(new MoveTo(lastX, lastY));
            getElements().add(new LineTo(x, y));
        }
        lastX = x;
        lastY = y;
    }

    public OMRStroke getOmrStroke() {
        return omrStroke;
    }
}
