package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutCoreSymbolWithDuration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * It determines the horizontal layout order of symbols
 */
public class LayoutCoreSymbolComparator implements Comparator<LayoutCoreSymbol> {
    public static LayoutCoreSymbolComparator instance = null;
    HashMap<Class<? extends LayoutCoreSymbol>, Integer> orders = new HashMap<>();
    {
        orders.put(LayoutCoreClef.class, 0);
        orders.put(LayoutCoreKeySignature.class, 1);
        orders.put(LayoutCoreTimeSignature.class, 2);
        orders.put(LayoutCoreBarline.class, 3);
        orders.put(LayoutCoreSymbolWithDuration.class, 4);
    }

    private LayoutCoreSymbolComparator() {

    }
    public static final LayoutCoreSymbolComparator getInstance() {
        synchronized (LayoutCoreSymbolComparator.class) {
            if (instance == null) {
                instance = new LayoutCoreSymbolComparator();
            }
        }
        return instance;
    }

    public int getOrder(LayoutCoreSymbol l) {
        Class clazz = l.getClass();
        Integer result = orders.get(l.getClass());

        while (result == null) {
            clazz = clazz.getSuperclass();
            if (clazz != null && LayoutCoreSymbol.class.isAssignableFrom(clazz)) {
                result = orders.get(clazz);
            } else {
                Logger.getLogger(LayoutCoreSymbolComparator.class.getName()).info(l.getClass().getName() + " not found among orders");
                return orders.size();
            }
        }
        return result;
    }
    @Override
    public int compare(LayoutCoreSymbol a, LayoutCoreSymbol b) {
        int diff = a.getTime().compareTo(b.getTime());
        if (diff == 0) {
            diff = getOrder(a) - getOrder(b);
            if (diff == 0) {
                return a.hashCode() - b.hashCode();
            } else {
                return diff;
            }
        }
        return diff;
    }


}


