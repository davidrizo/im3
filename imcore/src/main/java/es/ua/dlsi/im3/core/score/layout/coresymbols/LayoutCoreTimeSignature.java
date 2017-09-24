package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public abstract class LayoutCoreTimeSignature<CoreSymbolType extends ITimedElementInStaff> extends LayoutCoreSymbolInStaff<CoreSymbolType> {
    public LayoutCoreTimeSignature(LayoutFont layoutFont, CoreSymbolType coreSymbol) {
        super(layoutFont, coreSymbol);
    }
}
