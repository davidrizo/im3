package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolComparator;

/**
 * It represents the symbols that drive the rendering of the score.
 *
 *
 * @author drizo
 */
public abstract class LayoutCoreSymbol<CoreSymbolType extends ITimedElement> extends NotationSymbol implements Comparable<LayoutCoreSymbol> {
    protected CoreSymbolType coreSymbol;
    protected LayoutFont layoutFont;

    /**
     * @param layoutFont
     * @param coreSymbol
     */
    public LayoutCoreSymbol(LayoutFont layoutFont, CoreSymbolType coreSymbol) {
        this.coreSymbol = coreSymbol;
        this.layoutFont = layoutFont;
        // Initial value - it will be changed using the displacement of the Coordinate position
        this.position = new Coordinate(new CoordinateComponent(), new CoordinateComponent());
    }

    /**
     * The duration of the symbol that should be translated in a spacing
     * @return
     */
    public Time getDuration() {
        return Time.TIME_ZERO;
    }

    public Time getTime() {
        return coreSymbol.getTime();
    }

    public CoreSymbolType getCoreSymbol() {
        return coreSymbol;
    }

    @Override
    public int compareTo(LayoutCoreSymbol o) {
        return LayoutCoreSymbolComparator.getInstance().compare(this, o);
    }
}
