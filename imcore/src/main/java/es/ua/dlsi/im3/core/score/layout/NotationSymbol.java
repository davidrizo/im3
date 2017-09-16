package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author drizo
 */
public abstract class NotationSymbol {
    boolean hidden;

    public abstract GraphicsElement getGraphics();
}
