package es.ua.dlsi.im3.mavr.model;

import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

import java.util.List;

public abstract class MotiveRepresentation {
    Dimension dimension;

    public abstract List<? extends Shape> getShapes();
}
