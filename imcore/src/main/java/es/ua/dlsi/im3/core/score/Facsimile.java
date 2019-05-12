package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.score.facsimile.Surface;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on the facsimile element of MEI
 * @author drizo
 */
public class Facsimile {
    private List<Surface> surfaceList;

    public Facsimile() {
        surfaceList = new ArrayList<>();
    }

    public List<Surface> getSurfaceList() {
        return surfaceList;
    }

    public void addSurface(Surface surface) {
        this.surfaceList.add(surface);
    }
}
