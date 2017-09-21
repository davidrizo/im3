package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutSimpleRest extends LayoutSymbolWithDuration<SimpleRest> {
    SimpleRest rest;

    public LayoutSimpleRest(LayoutStaff layoutStaff, SimpleRest coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return null;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }

}
