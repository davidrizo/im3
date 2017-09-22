package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * It determines the horizontal layout order of symbols
 */
public class LayoutSymbolInStaffComparator implements Comparator<LayoutSymbolInStaff> {
    public static LayoutSymbolInStaffComparator instance = null;
    HashMap<Class<? extends LayoutSymbolInStaff>, Integer> orders = new HashMap<>();
    {
        orders.put(LayoutClef.class, 0);
        orders.put(LayoutKeySignature.class, 1);
        orders.put(LayoutTimeSignature.class, 2);
        orders.put(LayoutBarline.class, 3);
        orders.put(LayoutSymbolWithDuration.class, 4);
    }

    private LayoutSymbolInStaffComparator() {

    }
    public static final LayoutSymbolInStaffComparator getInstance() {
        synchronized (LayoutSymbolInStaffComparator.class) {
            if (instance == null) {
                instance = new LayoutSymbolInStaffComparator();
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
                Logger.getLogger(LayoutSymbolInStaffComparator.class.getName()).info(l.getClass().getName() + " not found among orders");
                return orders.size();
            }
        }
        return result;
    }
    @Override
    public int compare(LayoutSymbolInStaff a, LayoutSymbolInStaff b) {
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


