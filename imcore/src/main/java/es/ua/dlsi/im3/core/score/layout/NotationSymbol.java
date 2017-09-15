package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author drizo
 */
public abstract class NotationSymbol {
    double x;
    double y;
    boolean hidden;
    double width;
    double height;


    public abstract GraphicsElement getGraphics();
}
