package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolComparator;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutSystemBreak;
import es.ua.dlsi.im3.core.score.layout.graphics.BoundingBox;

import java.util.ArrayList;
import java.util.List;

/**
 * It encloses elements that should be aligned vertically. The same simultaneity cannot contain elements
 */
public class Simultaneity implements Comparable<Simultaneity> {
    Time time;
    List<LayoutCoreSymbol> symbols;
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
    private double x;

    /**
     * @param firstSymbol First inserted symbol. The simultaneity needs at least one symbol
     */
    public Simultaneity(LayoutCoreSymbol firstSymbol) throws IM3Exception {
        if (firstSymbol == null) {
            throw new IM3Exception("The symbol cannot be null");
        }
        this.time = firstSymbol.getTime();
       // print(true, firstSymbol);
        this.order = LayoutCoreSymbolComparator.getInstance().getOrder(firstSymbol);
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
    void add(LayoutCoreSymbol symbol) throws IM3Exception {
        //print(false, symbol);
        if (LayoutCoreSymbolComparator.getInstance().getOrder(symbol) != order) {
            throw new IM3RuntimeException("Cannot add different order symbols ( "+ order + " and " +
                    LayoutCoreSymbolComparator.getInstance().getOrder(symbol) + ") to the same simultaneity");
        }

        maxWidth = Math.max(maxWidth, symbol.getWidth());
        minWidth = Math.min(minWidth, symbol.getWidth());
        symbols.add(symbol);
    }

    /*private void print(boolean first, LayoutCoreSymbol symbol) {
        String f = first?"First ":"";
        System.out.println(f + "symbol " + symbol.getTime() +  " " + symbol);
        if (symbol.getCoreSymbol() instanceof SingleFigureAtom) {
            SingleFigureAtom sfa = (SingleFigureAtom) symbol.getCoreSymbol();
            System.out.println("\t"+sfa.getAtomFigure());
        }
    }*/

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
        for (LayoutCoreSymbol layoutCoreSymbol : symbols) {
            maxDur = Time.max(maxDur, layoutCoreSymbol.getDuration());
        }
        setTimeSpan(maxDur);
    }

    public void setX(double x) {
        this.x = x;
        for (LayoutCoreSymbol layoutCoreSymbol : symbols) {
            layoutCoreSymbol.setX(x);
        }
    }

    public List<LayoutCoreSymbol> getSymbols() {
        return symbols;
    }

    public BoundingBox computeBoundingBox() throws IM3Exception {
        double maxLeftDisplacement = Double.MAX_VALUE;
        double maxRightDisplacement = Double.MIN_VALUE;

        for (LayoutCoreSymbol layoutCoreSymbol : symbols) {
            BoundingBox childBB = layoutCoreSymbol.computeBoundingBox();
            maxLeftDisplacement = Math.min(maxLeftDisplacement, childBB.getLeftEnd());
            maxRightDisplacement = Math.max(maxLeftDisplacement, childBB.getRightEnd());
        }

        return new BoundingBox(maxLeftDisplacement, maxRightDisplacement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simultaneity that = (Simultaneity) o;

        if (order != that.order) return false;
        return time.equals(that.time);
    }

    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + order;
        return result;
    }

    public boolean isSystemBreak() {
        boolean result = false;
        for (LayoutCoreSymbol layoutCoreSymbol : symbols) {
            if (layoutCoreSymbol instanceof LayoutSystemBreak) {
                return true;
            }
        }
        return false;
    }

    public double getX() {
        return x;
    }
}
