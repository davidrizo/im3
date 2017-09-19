package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.Barline;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutTimeSignature;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

public class LayoutSignTimeSignature extends LayoutTimeSignature<SignTimeSignature> {

    public LayoutSignTimeSignature(LayoutStaff layoutStaff, SignTimeSignature coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return null;
    }
}
