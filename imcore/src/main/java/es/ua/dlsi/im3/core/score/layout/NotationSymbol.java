package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.graphics.BoundingBox;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author drizo
 */
public abstract class NotationSymbol {
    protected boolean hidden;
    protected Coordinate position;

    public abstract GraphicsElement getGraphics();

    public Coordinate getPosition() {
        return position;
    }
    /**
     * @return
     */
    public double getWidth() {
        GraphicsElement gr = getGraphics();
        if (gr == null) {
            return 0;
        } else {
            return gr.getWidth();
        }
    }

    public void setX(double x) {
        position.getX().setDisplacement(x);
    }

    /**
     * The space between the x of the symbol and its left end
     * @return
     */
    public BoundingBox computeBoundingBox() {
        return getGraphics().computeBoundingBox();
    }


}
