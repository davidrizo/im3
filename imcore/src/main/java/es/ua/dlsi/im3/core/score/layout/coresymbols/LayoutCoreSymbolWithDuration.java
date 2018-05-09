package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;

public abstract class LayoutCoreSymbolWithDuration<CoreSymbolType extends ITimedElementInStaff> extends LayoutCoreSymbolInStaff<CoreSymbolType> {
    public LayoutCoreSymbolWithDuration(LayoutFont layoutFont, CoreSymbolType coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
    }

    /**
     * Number of beams to be shown
     * @return
     */
    public abstract int getNumBeams();

    public abstract double getBottomPitchAbsoluteY() throws IM3Exception;

    public abstract double getTopPitchAbsoluteY() throws IM3Exception;
}
