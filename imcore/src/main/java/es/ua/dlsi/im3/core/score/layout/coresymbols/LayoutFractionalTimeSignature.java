package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.Barline;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

public class LayoutFractionalTimeSignature extends LayoutTimeSignature<FractionalTimeSignature> {
    Barline barline;

    public LayoutFractionalTimeSignature(LayoutStaff layoutStaff, FractionalTimeSignature coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return null;
    }
}
