package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaffSystem;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Component;

import java.util.ArrayList;
import java.util.List;

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
     * Besides the time, it indicates the order as the position from left to right
     */
    protected int defaultHorizontalOrdering;



    private List<Component<? extends LayoutCoreSymbol<CoreSymbolType>>> components;


    /**
     * @param layoutFont
     * @param coreSymbol
     */
    public LayoutCoreSymbol(LayoutFont layoutFont, CoreSymbolType coreSymbol) {
        this.coreSymbol = coreSymbol;
        this.layoutFont = layoutFont;
        // Initial value - it will be changed using the displacement of the Coordinate position
        this.position = new Coordinate(new CoordinateComponent(), new CoordinateComponent());

        defaultHorizontalOrdering = LayoutSymbolsHorizontalOrderings.getInstance().getGroupDefaultOrder(this);
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

    /*@Override
    public int compareTo(LayoutCoreSymbol o) {
        return o.getTime().compareTo(o.getTime()); // see getTime()
        //return LayoutCoreSymbolComparator.getInstance().compare(this, o);
    }*/

    public void addComponent(Component<? extends LayoutCoreSymbol<CoreSymbolType>> component) {
        if (components == null) {
            components = new ArrayList<>();
        }
        components.add(component);
    }

    public List<Component<? extends LayoutCoreSymbol<CoreSymbolType>>> getComponents() {
        return components;
    }

    @Override
    public int compareTo(LayoutCoreSymbol o) {
        int diff = getTime().compareTo(o.getTime());
        if (diff == 0) {
            diff = defaultHorizontalOrdering - o.defaultHorizontalOrdering;
            if (diff == 0) {
                diff = hashCode() - o.hashCode();
            }
        }
        return diff;
    }

    @Override
    public String toString() {
        return getClass() + "@" + getTime() + ", hordering=" + defaultHorizontalOrdering;
    }

    /**
     * Such as a dot displaced from a note head
     * @return
     */
    /*public Collection<LayoutCoreSymbol> getDependantCoreSymbols() {
        return null;
    }*/

}
