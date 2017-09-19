package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleChord;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutSimpleChord extends LayoutSymbolInStaff<SimpleChord> {
    public LayoutSimpleChord(LayoutStaff layoutStaff, SimpleChord coreSymbol) {
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
