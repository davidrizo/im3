package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;

public class LayoutSimpleNote extends LayoutSymbolWithDuration<SimpleNote> {
    SimpleNote note;

    public LayoutSimpleNote(LayoutStaff layoutStaff, SimpleNote coreSymbol) {
        super(layoutStaff, coreSymbol);
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
