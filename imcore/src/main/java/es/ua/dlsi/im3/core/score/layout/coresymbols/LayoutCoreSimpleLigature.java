package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.SimpleLigature;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

public class LayoutCoreSimpleLigature extends LayoutCoreSymbol<SimpleLigature> {

    public LayoutCoreSimpleLigature(LayoutFont layoutFont, SimpleLigature coreSymbol) {
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

}
