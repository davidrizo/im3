package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.Coordinate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// TODO: 21/9/17 ¿Mejor coordinate? 
/**
 * @author drizo
 */
public class Canvas {
    private final Coordinate leftTop;
    private final Coordinate bottomRight;
    private List<GraphicsElement> elementList;

    public Canvas(Coordinate leftTop, Coordinate bottomRight) {
        this.leftTop = leftTop;
        this.bottomRight = bottomRight;
        elementList = new ArrayList<>();
    }

    public void add(GraphicsElement shape) throws IM3Exception {
        if (shape.getCanvas() != null) {
            throw new IM3Exception("The shape " + shape + " is already contained in a canvas");
        }
        shape.setCanvas(this);
        elementList.add(shape);
    }

    public double getHeight() throws IM3Exception {
        return bottomRight.getAbsoluteY() - leftTop.getAbsoluteY();
    }

    public double getWidth() {
        return bottomRight.getAbsoluteX() - leftTop.getAbsoluteX();
    }

    public Coordinate getLeftTop() {
        return leftTop;
    }

    public Coordinate getBottomRight() {
        return bottomRight;
    }

    public List<GraphicsElement> getElements() {
        return elementList;
    }

    public void clear() {
        elementList.clear();
    }
}
