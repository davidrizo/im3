package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author drizo
 */
public class Canvas {
    private int height;
    private int width;
    private List<GraphicsElement> elementList;

    /**
     * @param width Pixels
     * @param height Pixels
     */
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        elementList = new ArrayList<>();
    }

    public void add(GraphicsElement shape) throws IM3Exception {
        if (shape.getCanvas() != null) {
            throw new IM3Exception("The shape " + shape + " is already contained in a canvas");
        }
        shape.setCanvas(this);
        elementList.add(shape);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List<GraphicsElement> getElements() {
        return elementList;
    }
}
