package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaffSystem;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.ArrayList;

public class Page {
    private ArrayList<LayoutStaffSystem> systemsInPage;
    private Canvas canvas;

    public Page(CoordinateComponent width, CoordinateComponent height) {
        systemsInPage = new ArrayList<>();
        canvas = new Canvas(width, height);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void addSystem(LayoutStaffSystem system) throws IM3Exception {
        systemsInPage.add(system);
        for (LayoutStaff staff: system.getStaves()) {
            canvas.add(staff.getGraphics());
        }
    }

    public ArrayList<LayoutStaffSystem> getSystemsInPage() {
        return systemsInPage;
    }
}
