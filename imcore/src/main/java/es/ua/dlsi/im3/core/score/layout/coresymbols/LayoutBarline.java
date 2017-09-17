package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.Barline;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutBarline extends LayoutSymbolInStaff<Barline> {
    public LayoutBarline(LayoutStaff layoutStaff, Barline coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return null;
    }
}
