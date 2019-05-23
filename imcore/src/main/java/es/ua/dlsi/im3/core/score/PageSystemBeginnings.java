package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * System and page beginnings
 */
public class PageSystemBeginnings {

    /**
     * Explicit system breaks
     */
    TreeMap<Time, SystemBeginning> systemBeginnings;
    /**
     * Explicit page breaks
     */
    TreeMap<Time, PageBeginning> pageBeginnings;


    public PageSystemBeginnings() {
        systemBeginnings = new TreeMap<>();
        pageBeginnings = new TreeMap<>();
    }

    public void addSystemBeginning(SystemBeginning sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("System break has not time set");
        }
        systemBeginnings.put(sb.getTime(), sb);
    }

    public TreeMap<Time, SystemBeginning> getSystemBeginnings() {
        return systemBeginnings;
    }

    public boolean hasSystemBeginning(Time time) {
        return systemBeginnings.containsKey(time);
    }

    public void addPageBeginning(PageBeginning sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("Page break has not time set");
        }
        pageBeginnings.put(sb.getTime(), sb);
    }

    public TreeMap<Time, PageBeginning> getPageBeginnings() {
        return pageBeginnings;
    }

    public boolean hasPageBeginning(Time time) {
        return pageBeginnings.containsKey(time);
    }

    /**
     * @param time
     * @return null if not found
     */
    public SystemBeginning getSystemBeginningAfter(Time time) {
        Map.Entry<Time, SystemBeginning> entry = systemBeginnings.higherEntry(time);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }

    /**
     * @param time
     * @return null if not found
     */
    public PageBeginning getPageBeginningAfter(Time time) {
        Map.Entry<Time, PageBeginning> entry = pageBeginnings.higherEntry(time);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }

}
