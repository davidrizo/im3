package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;

import java.util.ArrayList;

/**
 * The score is divided into lines or systems.
 */
public class LayoutSystem {
    ArrayList<LayoutStaff> layoutStaves;

    public LayoutSystem() {
        layoutStaves = new ArrayList<>();
    }

    public void addLayoutStaff(LayoutStaff layoutStaff) {
        layoutStaves.add(layoutStaff);
    }
}
