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
    private double height;
    private double width;
    private List<GraphicsElement> elementList;

    public Canvas(double width, double height) {
        this.width = width;
        this.height = height;
        elementList = new ArrayList<>();
    }

    public void add(Shape shape) throws IM3Exception {
        if (shape.getCanvas() != null) {
            throw new IM3Exception("The shape " + shape + " is already contained in a canvas");
        }
        shape.setCanvas(this);
        elementList.add(shape);
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public List<GraphicsElement> getElements() {
        return elementList;
    }
}
