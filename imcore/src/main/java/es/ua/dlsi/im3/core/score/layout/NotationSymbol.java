package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.IUniqueIDObject;
import es.ua.dlsi.im3.core.score.layout.graphics.BoundingBox;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.time.Instant;

/**
 * @author drizo
 */
public abstract class NotationSymbol implements IUniqueIDObject {
    protected boolean hidden;
    protected Coordinate position;
    private String ID;
    Instant lastLayout;

    public NotationSymbol() {
    }

    public abstract GraphicsElement getGraphics();

    public Coordinate getPosition() {
        return position;
    }
    /**
     * @return
     */
    public double getWidth() throws IM3Exception {
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
    public BoundingBox computeBoundingBox() throws IM3Exception {
        return getGraphics().computeBoundingBox();
    }


    public void move(double offset) {
        position.setDisplacementX(position.getX().getDisplacement()+offset);
    }

    @Override
    public String __getID() {
        return ID;
    }

    @Override
    public void __setID(String id) {
        this.ID = ID;
    }

    @Override
    public String __getIDPrefix() {
        return this.getClass().getName();
    }

    public void layout() throws IM3Exception {
        doLayout();
        lastLayout = Instant.now();
    }

    protected abstract void doLayout() throws IM3Exception;

    public Instant getLastLayout() {
        return lastLayout;
    }
}
