package es.ua.dlsi.im3.core.score.facsimile;

import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;

import java.util.LinkedList;
import java.util.List;

/**
 * Based on the facsimile surface element of MEI
 * @author drizo
 */
public class Surface {
    private String ID;
    private BoundingBox boundingBox;
    private List<Graphic> graphicList;
    private List<Zone> zoneList;

    public Surface() {
        zoneList = new LinkedList<>();
        graphicList = new LinkedList<>();
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<Zone> getZoneList() {
        return zoneList;
    }

    public void addZone(Zone zone) {
        zoneList.add(zone);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Graphic> getGraphicList() {
        return graphicList;
    }

    public void addGraphic(Graphic graphic) {
        graphicList.add(graphic);
    }
}
