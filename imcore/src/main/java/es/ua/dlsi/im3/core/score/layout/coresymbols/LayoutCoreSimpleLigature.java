package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.LigaturaBinaria;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

public class LayoutCoreSimpleLigature extends LayoutCoreSymbol<LigaturaBinaria> {

    public LayoutCoreSimpleLigature(LayoutFont layoutFont, LigaturaBinaria coreSymbol) {
        super(layoutFont, coreSymbol);
    }

    @Override
    public Shape getGraphics() {
        return null;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }
    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
