package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.HashMap;

/**
 * System and page beginnings
 */
public class PageSystemBeginnings {

    /**
     * Explicit system breaks
     */
    HashMap<Time, SystemBeginning> systemBeginnings;
    /**
     * Explicit page breaks
     */
    HashMap<Time, PageBeginning> pageBeginnings;


    public PageSystemBeginnings() {
        systemBeginnings = new HashMap<>();
        pageBeginnings = new HashMap<>();
    }

    public void addSystemBeginning(SystemBeginning sb) throws IM3Exception {
        if (sb.getTime() == null) {
            throw new IM3Exception("System break has not time set");
        }
        systemBeginnings.put(sb.getTime(), sb);
    }

    public HashMap<Time, SystemBeginning> getSystemBeginnings() {
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

    public HashMap<Time, PageBeginning> getPageBeginnings() {
        return pageBeginnings;
    }

    public boolean hasPageBeginning(Time time) {
        return pageBeginnings.containsKey(time);
    }

}
