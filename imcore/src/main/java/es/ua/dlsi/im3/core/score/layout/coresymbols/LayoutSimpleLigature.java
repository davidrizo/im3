package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleLigature;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

public class LayoutSimpleLigature extends LayoutSymbolInStaff<SimpleLigature> {
    SimpleLigature ligature;

    public LayoutSimpleLigature(LayoutStaff layoutStaff, SimpleLigature coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public Shape getGraphics() {
        return null;
    }
}
