package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;

/**
 * @author drizo
 * SymbolElement in old imcore
 */
public abstract class Component<ParentType extends  NotationSymbol> extends NotationSymbol {
    protected ParentType parent;
    /**
     *
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public Component(ParentType parent, Coordinate position) {
        this.parent = parent;
        this.position = position;
    }
}
