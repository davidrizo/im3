package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Simultaneity;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A staff system arranged in a given layout
 */
public class LayoutStaffSystem {
    SortedMap<Staff, LayoutStaff> layoutStaves = null;
    /**
     * Used for page layout
     */
    double startingX;

    Time startingTime;

    Time endingTime;

    public LayoutStaffSystem() {
        layoutStaves = new TreeMap<>();
    }

    public Time getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Time startingTime) {
        this.startingTime = startingTime;
    }

    public Time getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Time endingTime) {
        this.endingTime = endingTime;
    }

    /**
     * @param layoutStaff
     */
    public void addLayoutStaff(LayoutStaff layoutStaff) {
        layoutStaves.put(layoutStaff.getStaff(), layoutStaff);
    }

    public LayoutStaff getBottomStaff() {
        return layoutStaves.get(layoutStaves.lastKey());
    }

    public LayoutStaff getTopStaff() {
        return layoutStaves.get(layoutStaves.firstKey());
    }

    public void setStartingX(double startingX) {
        this.startingX = startingX;
    }

    public double getStartingX() {
        return startingX;
    }

    public LayoutStaff get(Staff staff) {
        return layoutStaves.get(staff);
    }

    public Collection<LayoutStaff> getStaves() {
        return layoutStaves.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayoutStaffSystem that = (LayoutStaffSystem) o;

        return startingTime.equals(that.startingTime);
    }

    @Override
    public int hashCode() {
        return startingTime.hashCode();
    }
}