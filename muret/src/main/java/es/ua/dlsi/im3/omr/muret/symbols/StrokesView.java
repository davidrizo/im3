package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.omr.muret.model.OMRStroke;
import es.ua.dlsi.im3.omr.muret.model.OMRStrokes;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class StrokesView extends Group {
    private final Color strokesColor;
    OMRStrokes omrStrokes;
    public StrokesView(OMRStrokes omrStrokes, double offsetX, double offsetY, Color strokesColor) {
        this.omrStrokes = omrStrokes;
        this.strokesColor = strokesColor;
        this.setTranslateX(offsetX);
        this.setTranslateY(offsetY);

        for (OMRStroke omrStroke: omrStrokes.strokeListProperty()) {
            addStroke(omrStroke);
        }

        omrStrokes.strokeListProperty().addListener(new ListChangeListener<OMRStroke>() {
            @Override
            public void onChanged(Change<? extends OMRStroke> c) {
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
                        for (OMRStroke remitem : c.getRemoved()) {
                            //remitem.remove(Outer.this); //TODO
                        }
                        // add
                        for (OMRStroke omrStroke : c.getAddedSubList()) {
                            addStroke(omrStroke);
                        }
                    }
                }
            }
        });
    }

    public OMRStrokes getOmrStrokes() {
        return omrStrokes;
    }

    private void addStroke(OMRStroke omrStroke) {
        StrokeView strokeView = new StrokeView(omrStroke, strokesColor);
        getChildren().add(strokeView);

    }

    /**
     * True if some of the strokes it contains has more than 1 point
     * @return
     */
    public boolean hasMoreThan1Point() {
        for (OMRStroke omrStroke: omrStrokes.strokeListProperty()) {
            if (omrStroke.pointsProperty().size() > 1) {
                return true;
            }
        }
        return false;
    }
}
