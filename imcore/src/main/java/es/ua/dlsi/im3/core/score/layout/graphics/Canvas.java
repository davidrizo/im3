package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author drizo
 */
public class Canvas {
    private final CoordinateComponent width;
    private final CoordinateComponent height;
    private List<GraphicsElement> elementList;

    public Canvas(CoordinateComponent width, CoordinateComponent height) {
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

    public double getHeight()  {
        return height.getAbsoluteValue();
    }

    public double getWidth() {
        return width.getAbsoluteValue();
    }


    public List<GraphicsElement> getElements() {
        return elementList;
    }

    public void clear() {
        elementList.clear();
    }

    public CoordinateComponent getWidthCoordinateComponent() {
        return width;
    }
}
