package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public abstract class LayoutTimeSignature<CoreSymbolType extends ITimedElementInStaff> extends CompoundLayoutSymbol<CoreSymbolType> {
    public LayoutTimeSignature(LayoutStaff layoutStaff, CoreSymbolType coreSymbol) {
        super(layoutStaff, coreSymbol);
    }
}
