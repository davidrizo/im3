package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.layout.coresymbols.*;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Used to group elements in common simultaneities. We don't use the classes because this way we can put in the same simultaneity
 * two different classes such as LayoutCoreBarline and LayoutCoreMarkBarline
 */
public class LayoutSymbolsHorizontalOrderings {
    public static LayoutSymbolsHorizontalOrderings instance = null;
    HashMap<Class<? extends LayoutCoreSymbol>, Integer> orders = new HashMap<>();
    {
        orders.put(LayoutCoreLigature.class, 0);
        orders.put(LayoutCoreBarline.class, 0);
        orders.put(LayoutCoreMarkBarline.class, 0);
        orders.put(LayoutCoreStaffTimedPlaceHolder.class, 0);
        orders.put(LayoutCoreCustos.class, 1);
        orders.put(LayoutCoreSystemBreak.class, 2);
        orders.put(LayoutPageBreak.class, 3);
        orders.put(LayoutCoreClef.class, 4);
        orders.put(LayoutCoreKeySignature.class, 5);
        orders.put(LayoutCoreTimeSignature.class, 6);
        orders.put(LayoutCoreSymbolWithDuration.class, 7);
        orders.put(LayoutCoreDisplacedDot.class, 7); // equal to symbol with duration
    }

    private LayoutSymbolsHorizontalOrderings() {

    }
    public static final LayoutSymbolsHorizontalOrderings getInstance() {
        synchronized (LayoutSymbolsHorizontalOrderings.class) {
            if (instance == null) {
                instance = new LayoutSymbolsHorizontalOrderings();
            }
        }
        return instance;
    }

    public int getGroupDefaultOrder(LayoutCoreSymbol l) {
        Class clazz = l.getClass();
        Integer result = orders.get(l.getClass());

        while (result == null) {
            clazz = clazz.getSuperclass();
            if (clazz != null && LayoutCoreSymbol.class.isAssignableFrom(clazz)) {
                result = orders.get(clazz);
            } else {
                Logger.getLogger(LayoutSymbolsHorizontalOrderings.class.getName()).info(l.getClass().getName() + " not found among orders");
                return orders.size();
            }
        }
        return result;
    }
}
