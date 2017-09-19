package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutTimeSignature;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * It determines the horizontal layout order of symbols
 */
public class CoreSymbolsOrderer {
    public static CoreSymbolsOrderer instance = null;
    HashMap<Class<? extends LayoutSymbolInStaff>, Integer> orders = new HashMap<>();
    {
        orders.put(LayoutClef.class, 0);
        orders.put(LayoutKeySignature.class, 1);
        orders.put(LayoutTimeSignature.class, 2);
        orders.put(LayoutBarline.class, 3);
        orders.put(LayoutSymbolWithDuration.class, 4);
    }

    private CoreSymbolsOrderer() {

    }
    public static final CoreSymbolsOrderer getInstance() {
        synchronized (CoreSymbolsOrderer.class) {
            if (instance == null) {
                instance = new CoreSymbolsOrderer();
            }
        }
        return instance;
    }

    public int getOrder(LayoutSymbolInStaff l) {
        Class clazz = l.getClass();
        Integer result = orders.get(l.getClass());

        while (result == null) {
            clazz = clazz.getSuperclass();
            if (clazz != null && LayoutSymbolInStaff.class.isAssignableFrom(clazz)) {
                result = orders.get(clazz);
            } else {
                Logger.getLogger(CoreSymbolsOrderer.class.getName()).info(l.getClass().getName() + " not found among orders");
                return orders.size();
            }
        }
        return result;
    }
    public int compare(LayoutSymbolInStaff a, LayoutSymbolInStaff b) {
        int diff = getOrder(a) - getOrder(b);
        if (diff == 0) {
            return a.hashCode() - b.hashCode();
        } else {
            return diff;
        }
    }


}


