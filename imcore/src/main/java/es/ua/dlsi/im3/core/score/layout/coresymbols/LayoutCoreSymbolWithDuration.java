package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;

public abstract class LayoutCoreSymbolWithDuration<CoreSymbolType extends ITimedElementInStaff> extends LayoutCoreSymbolInStaff<CoreSymbolType> {
    public LayoutCoreSymbolWithDuration(LayoutFont layoutFont, CoreSymbolType coreSymbol) {
        super(layoutFont, coreSymbol);
    }


}
