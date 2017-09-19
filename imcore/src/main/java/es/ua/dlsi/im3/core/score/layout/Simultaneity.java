package es.ua.dlsi.im3.core.score.layout;

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
     * @param firstSymbol First inserted symbol. The simultaneity needs at least one symbol
     */
    public Simultaneity(LayoutSymbolInStaff firstSymbol) {
        this.time = firstSymbol.getTime();
        this.order = CoreSymbolsOrderer.getInstance().getOrder(firstSymbol);
        symbols = new ArrayList<>();
        symbols.add(firstSymbol);
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
}
