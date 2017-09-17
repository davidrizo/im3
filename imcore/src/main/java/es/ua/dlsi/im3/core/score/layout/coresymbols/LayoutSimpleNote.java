package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

public class LayoutSimpleNote extends LayoutSymbolInStaff<SimpleNote> {
    SimpleNote note;

    public LayoutSimpleNote(LayoutStaff layoutStaff, SimpleNote coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public Shape getGraphics() {
        return null;
    }
}
