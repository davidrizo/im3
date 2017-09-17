package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.KeySignature;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutKeySignature extends LayoutSymbolInStaff<KeySignature> {

    public LayoutKeySignature(LayoutStaff layoutStaff, KeySignature coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return null;
    }
}
