package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.CoreSymbolsOrderer;

import java.util.ArrayList;
import java.util.List;

/**
 * It encloses elements that should be aligned vertically. The same simultaneity cannot contain elements
 */
public class Simultaneity implements Comparable<Simultaneity> {
    Time time;
    List<LayoutSymbolInStaff> symbols;
    /**
     * Used to group different objects of the same time
     */
    int order;

    /**
     * Duration
     */
    Time timeSpan;

    /**
     * The maximum of the widths of the symbols
     */
    double maxWidth;

    /**
     * The minimum of the widths of the symbols
     */
    double minWidth;
    /**
     * The width of the simultaneity in the layout
     */
    private double layoutWidth;

    /**
     * @param firstSymbol First inserted symbol. The simultaneity needs at least one symbol
     */
    public Simultaneity(LayoutSymbolInStaff firstSymbol) {
        this.time = firstSymbol.getTime();
        this.order = CoreSymbolsOrderer.getInstance().getOrder(firstSymbol);
        symbols = new ArrayList<>();
        symbols.add(firstSymbol);
        minWidth = maxWidth = firstSymbol.getWidth();
    }

    @Override
    public int compareTo(Simultaneity o) {
        int diff = time.compareTo(o.time);
        if (diff == 0) {
            return order - o.order;
        } else {
            return diff;
        }
    }

    public int size() {
        return symbols.size();
    }
    public Time getTime() {
        return time;
    }

    /**
     * Package visibility. It is used by Simultaneities
     * @param symbol
     */
    void add(LayoutSymbolInStaff symbol) {
        if (CoreSymbolsOrderer.getInstance().getOrder(symbol) != order) {
            throw new IM3RuntimeException("Cannot add different order symbols ( "+ order + " and " +
                    CoreSymbolsOrderer.getInstance().getOrder(symbol) + ")to the same simultaneity");
        }

        maxWidth = Math.max(maxWidth, symbol.getWidth());
        minWidth = Math.min(minWidth, symbol.getWidth());
        symbols.add(symbol);
    }

    @Override
    public String toString() {
        return "Simultaneity{" +
                "time=" + time +
                ", order=" + order +
                ", symbols=" + symbols +
                '}';
    }

    public Time getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Time timeSpan) {
        this.timeSpan = timeSpan;
    }

    /**
     * The minimum width the simultaneity needs for avoiding collisions
     * @return
     */
    public double getMinimumWidth() {
        return maxWidth; // it is not an error, it is maxWidth
    }

    public void setLayoutWidth(double width) {
        this.layoutWidth = width;
    }

    public double getLayoutWidth() {
        return layoutWidth;
    }

    /**
     * The timeSpan will be the maximum of the duration of its elements
     */
    public void setTimeSpanFromElementsDuration() {
        Time maxDur = Time.TIME_ZERO;
        for (LayoutSymbolInStaff layoutSymbolInStaff: symbols) {
            maxDur = Time.max(maxDur, layoutSymbolInStaff.getDuration());
        }
        setTimeSpan(maxDur);
    }

    public void setX(double x) {
        for (LayoutSymbolInStaff layoutSymbolInStaff: symbols) {
            layoutSymbolInStaff.setX(x);
        }
    }
}
