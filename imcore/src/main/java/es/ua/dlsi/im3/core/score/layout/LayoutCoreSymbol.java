package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolComparator;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaffSystem;

/**
 * It represents the symbols that drive the rendering of the score.
 *
 *
 * @author drizo
 */
public abstract class LayoutCoreSymbol<CoreSymbolType extends ITimedElement> extends NotationSymbol implements Comparable<LayoutCoreSymbol> {
    protected CoreSymbolType coreSymbol;
    protected LayoutFont layoutFont;
    protected LayoutStaffSystem system;
    /**
     * If we want it to be drawn at other time
     */
    protected Time modifiedTime;

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
     *
     * @return
     * @throws IM3Exception If not associated to a system
     */
    public LayoutStaffSystem getSystem() throws IM3Exception {
        if (system == null) {
            throw new IM3Exception("This symbol " + this + " is not associated to a system");
        }
        return system;
    }

    public void setSystem(LayoutStaffSystem system) {
        this.system = system;
    }

    /**
     * The duration of the symbol that should be translated in a spacing
     * @return
     */
    public Time getDuration() {
        return Time.TIME_ZERO;
    }

    public Time getTime() {
        if (modifiedTime == null) {
            return coreSymbol.getTime();
        } else {
            return modifiedTime;
        }
    }

    public void setTime(Time time) {
        this.modifiedTime = time;
    }


    public CoreSymbolType getCoreSymbol() {
        return coreSymbol;
    }

    @Override
    public int compareTo(LayoutCoreSymbol o) {
        return LayoutCoreSymbolComparator.getInstance().compare(this, o);
    }
}
