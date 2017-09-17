package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;

/**
 * @author drizo
 * SymbolElement in old imcore
 */
public abstract class Component<ParentType extends  NotationSymbol> extends NotationSymbol {
    protected ParentType parent;

    public Component(ParentType parent) {
        this.parent = parent;
    }

    /**
     * It is invoked previous to the export to the view
     * @throws IM3Exception
     */
    public void computeLayout() throws IM3Exception {
        // empty by default
    }

}
