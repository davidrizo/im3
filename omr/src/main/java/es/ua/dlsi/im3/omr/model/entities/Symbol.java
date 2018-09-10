package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxYX;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import java.util.List;
import java.util.Objects;

/**
 * One of the agnostic symbols
 */
public class Symbol implements Comparable<Symbol> {
    /**
     * Owner region, transient because we do not want to serialize it
     */
    private transient Region region;
    /**
     * Symbol type and its position in staff
     */
    private AgnosticSymbol agnosticSymbol;

    /**
     * Whether it has been accepted by the user
     */
    private boolean accepted;
    /**
     * Optional: bounding box, absolute values
     */
    private BoundingBoxXY boundingBox;
    /**
     * Optional: strokes used to trace the symbol.
     */
    private Strokes strokes;
    /**
     * Instrument, it may be null if defined in a higher level
     */
    Instrument instrument;

    /**
     * Optional: information about the pixels of the image. May be null
     */
    //private RasterImage rasterMonochromeImage;


    public Symbol() {
    }

    public Symbol(AgnosticSymbol symbolType, double fromX, double fromY, double toX, double toY) throws IM3Exception {
        if (fromX < 0) {
            throw new IM3Exception("Cannot build a symbol with fromX (" + fromX + ") < 0");
        }
        if (fromY < 0) {
            throw new IM3Exception("Cannot build a symbol with fromY (" + fromY + ") < 0");
        }
        if (toX <= fromX) {
            throw new IM3Exception("Cannot build a symbol with toX (" + toX + ") <= fromX (" + fromX + ")");
        }
        if (toY <= fromY) {
            throw new IM3Exception("Cannot build a symbol with toY (" + toY + ") <= fromY (" + fromY + ")");
        }

        this.agnosticSymbol = symbolType;
        boundingBox = new BoundingBoxXY(fromX, fromY, toX, toY);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public AgnosticSymbol getAgnosticSymbol() {
        return agnosticSymbol;
    }

    public void setAgnosticSymbol(AgnosticSymbol agnosticSymbol) {
        this.agnosticSymbol = agnosticSymbol;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public BoundingBoxXY getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxXY boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Strokes getStrokes() {
        return strokes;
    }

    public void setStrokes(Strokes strokes) {
        this.strokes = strokes;
    }

    /*public RasterImage getRasterMonochromeImage() {
        return rasterMonochromeImage;
    }

    public void setRasterMonochromeImage(RasterImage rasterMonochromeImage) {
        this.rasterMonochromeImage = rasterMonochromeImage;
    }*/


    @Override
    public int compareTo(Symbol o) {
        return boundingBox.compareTo(o.boundingBox);
    }

    public double getWidth() {
        return boundingBox.getWidth();
    }

    public double getHeight() {
        return boundingBox.getHeight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return accepted == symbol.accepted &&
                Objects.equals(agnosticSymbol, symbol.agnosticSymbol) &&
                Objects.equals(boundingBox, symbol.boundingBox) &&
                Objects.equals(strokes, symbol.strokes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(agnosticSymbol, accepted, boundingBox, strokes);
    }


     /*
     * It computes the bounding box relative to the region
     * @return
      */
    public BoundingBox computeRelativeBoundingBox() throws IM3Exception {
        if (region == null) {
            return new BoundingBoxYX(boundingBox);
        } else {
            return new BoundingBoxYX(
                    boundingBox.getFromX()-region.getBoundingBox().getFromX(),
                    boundingBox.getFromY()-region.getBoundingBox().getFromY(),
                    boundingBox.getToX()-region.getBoundingBox().getFromX(),
                    boundingBox.getToY()-region.getBoundingBox().getFromY());
        }
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
