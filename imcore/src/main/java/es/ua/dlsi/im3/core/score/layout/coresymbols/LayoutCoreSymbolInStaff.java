package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

/**
 * The x coordinate must be computed on constructor. The y coordinate will be computed once it is assigned to a layoutStaff
 * @param <CoreSymbolType>
 */
public abstract class LayoutCoreSymbolInStaff<CoreSymbolType extends ITimedElementInStaff> extends LayoutCoreSymbol<CoreSymbolType> {
    protected LayoutStaff layoutStaff;
    public LayoutCoreSymbolInStaff(LayoutFont layoutFont, CoreSymbolType coreSymbol) {
        super(layoutFont, coreSymbol);
    }

    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        this.layoutStaff = layoutStaff;
    }

    public Staff getCoreStaff() {
        return ((ITimedElementInStaff)coreSymbol).getStaff();
    }

    public LayoutStaff getLayoutStaff() {
        return layoutStaff;
    }


}
