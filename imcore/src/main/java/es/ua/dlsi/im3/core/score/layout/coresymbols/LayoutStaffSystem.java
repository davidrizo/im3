package es.ua.dlsi.im3.core.score.layout.coresymbols;

import java.util.ArrayList;
import java.util.List;

/**
 * A staff system arranged in a given layout
 */
public class LayoutStaffSystem {
    ArrayList<LayoutStaff> layoutStaves;

    public LayoutStaffSystem() {
        layoutStaves = new ArrayList<>();
    }

    /**
     * @param position    0 is bottom
     * @param layoutStaff
     */
    public void addLayoutStaff(int position, LayoutStaff layoutStaff) {
        layoutStaves.add(position, layoutStaff);
    }

    public LayoutStaff getBottomStaff() {
        return layoutStaves.get(0);
    }

    public LayoutStaff getTopStaff() {
        return layoutStaves.get(layoutStaves.size() - 1);
    }

    public List<LayoutStaff> getStaves() {
        return layoutStaves;
    }
}