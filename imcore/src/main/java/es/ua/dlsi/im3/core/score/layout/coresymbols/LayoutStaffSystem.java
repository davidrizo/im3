package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.LayoutConnector;
import es.ua.dlsi.im3.core.score.layout.coresymbols.connectors.LayoutDashedBarlineAcrossStaves;

import java.util.*;

// TODO: 20/11/17 Todo esto necesita reescribirse totalmente
/**
 * A staff system arranged in a given layout
 */
public class LayoutStaffSystem {
    HashMap<Staff, HashMap<Time, LayoutCoreBarline>> barlines;

    SortedMap<Staff, LayoutStaff> layoutStaves = null;
    /**
     * Used for page layout
     */
    double startingX;

    Time startingTime;

    Time endingTime;

    public LayoutStaffSystem() {
        layoutStaves = new TreeMap<>();
        barlines = new HashMap<>();
        startingTime = Time.TIME_ZERO;

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

    public void createStaffConnectors(List<LayoutConnector> connectors) throws IM3Exception {
        // create staff connectors
        for (Staff staff: layoutStaves.keySet()) {
            for (Connector connector: staff.getConnectors()) {
                if (connector.getFrom() == staff) {
                    // avoid creating twice
                    if (connector instanceof DashedBarlineAcrossStaves) {
                        DashedBarlineAcrossStaves dashedBarlineAcrossStaves = (DashedBarlineAcrossStaves) connector;

                        HashMap<Time, LayoutCoreBarline> staffBarLines = barlines.get(dashedBarlineAcrossStaves.getFrom()); // getFrom == staff
                        if (staffBarLines != null) {
                            LayoutCoreBarline barline = staffBarLines.get(dashedBarlineAcrossStaves.getTime());
                            if (barline != null) { // if null, it does not belong to this system
                                //throw new IM3Exception("Cannot find a source barline for time " + dashedBarlineAcrossStaves.getTime() + " while creating LayoutDashedBarlineAcrossStaves in layoutStaffSystem " + this);
                                //} else {
                                LayoutStaff toLayoutStaff = layoutStaves.get(dashedBarlineAcrossStaves.getTo());
                                if (toLayoutStaff == null) {
                                    throw new IM3RuntimeException("Cannot find a LayoutStaff for staff " + dashedBarlineAcrossStaves.getTo() + " while creating LayoutDashedBarlineAcrossStaves");
                                }

                                LayoutDashedBarlineAcrossStaves layoutDashedBarlineAcrossStaves = new LayoutDashedBarlineAcrossStaves(barline, toLayoutStaff);
                                //addConnector(layoutDashedBarlineAcrossStaves);
                                connectors.add(layoutDashedBarlineAcrossStaves);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addLayoutCoreBarline(LayoutCoreBarline layoutCoreBarline) {
        HashMap<Time, LayoutCoreBarline> measuresBarLines = barlines.get(layoutCoreBarline.getStaff());
        if (measuresBarLines == null) {
            measuresBarLines = new HashMap<>();
            barlines.put(layoutCoreBarline.getStaff(), measuresBarLines);
        }
        measuresBarLines.put(layoutCoreBarline.getTime(), layoutCoreBarline);
    }
}