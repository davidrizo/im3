package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;

public abstract class LayoutSymbolWithDuration<CoreSymbolType extends ITimedElementInStaff> extends LayoutSymbolInStaff<CoreSymbolType> {
    public LayoutSymbolWithDuration(LayoutStaff layoutStaff, CoreSymbolType coreSymbol) {
        super(layoutStaff, coreSymbol);
    }
}
