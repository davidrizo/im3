package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.components.Component;

import java.util.Collection;

/**
 * It represents the symbols that drive the rendering of the score.
 *
 * The symbol itself is not drawable, the symbol symbols, connectors and
 * attachments are the drawable components of the symbol
 *
 * @author drizo
 */
public abstract class CoreSymbol extends NotationSymbol {
    Collection<Component> components;
    /**
     * This value is computed by the layout algorithm
     */
    private double x;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void computeLayout() throws IM3Exception {
        // empty by default
    }
}
