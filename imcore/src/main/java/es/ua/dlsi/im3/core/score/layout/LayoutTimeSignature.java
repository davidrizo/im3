package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public abstract class LayoutTimeSignature<CoreSymbolType extends ITimedElementInStaff> extends LayoutSymbolInStaff<CoreSymbolType>  {
    public LayoutTimeSignature(LayoutStaff layoutStaff, CoreSymbolType coreSymbol) {
        super(layoutStaff, coreSymbol);
    }
}
