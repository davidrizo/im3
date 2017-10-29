package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;

/**
 * @author drizo
 * SymbolElement in old imcore
 */
public abstract class Component<ParentType extends NotationSymbol> extends NotationSymbol {
    private final Object modelElement;
    protected ParentType parent;
    /**
     *
     * @param parent May be null
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public Component(Object modelElement, ParentType parent, Coordinate position) {
        this.modelElement = modelElement;
        this.parent = parent;
        this.position = position;
    }

    public ParentType getParent() {
        return parent;
    }

    /**
     * @return May be null
     */
    public Object getModelElement() {
        return modelElement;
    }
}
