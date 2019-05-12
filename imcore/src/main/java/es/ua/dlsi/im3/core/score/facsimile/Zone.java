package es.ua.dlsi.im3.core.score.facsimile;

import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;

/**
 * Based on the facsimile surface zone element of MEI
 * @author drizo
 */
public class Zone {
    private String ID;
    private BoundingBox boundingBox;
    private String type;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
