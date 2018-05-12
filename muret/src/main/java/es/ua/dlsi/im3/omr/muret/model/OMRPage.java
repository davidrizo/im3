package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.omr.model.entities.Page;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.RegionType;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class OMRPage implements Comparable<OMRPage>, IOMRBoundingBox {
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty fromX;
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty fromY;
    /**
     * Bounding box x, absolute value
     */
    DoubleProperty width;
    /**
     * Bounding box height
     */
    DoubleProperty height;
    /**
     * Name based on ??? // TODO: 21/4/18
     */
    StringProperty name;
    /**
     * Image it belongs to
     */
    private final OMRImage omrImage;
    /**
     * List of instruments - they must belong to the OMRProject
     */
    Set<OMRInstrument> instruments;
    /**
     * List of regions, orderer by cartesian position
     */
    ObservableSet<OMRRegion> regions;


    public OMRPage(OMRImage omrImage, double fromX, double fromY, double toX, double toY) throws IM3Exception {
        this.omrImage = omrImage;
        this.fromX = new SimpleDoubleProperty(fromX);
        this.fromY = new SimpleDoubleProperty(fromY);
        this.width = new SimpleDoubleProperty(toX-fromX);
        this.height = new SimpleDoubleProperty(toY-fromY);
        this.instruments = new TreeSet<>(); // we mantain it ordered
        this.regions = FXCollections.observableSet(new TreeSet<>());
        this.name = new SimpleStringProperty();
        this.name.bind(Bindings.concat("Page ", this.fromX, ", ", this.fromY));
    }

    /*Abril public void addStaff(ToggleGroup staffToggleGroup, int leftTopX, int leftTopY, int bottomRightX, int bottomRightY) throws IM3Exception {
        OMRRegion staff = new OMRStaff(this, leftTopX, leftTopY, bottomRightX, bottomRightY);
        regions.add(staff);
    }*/


    public Set<OMRInstrument> getInstruments() {
        return instruments;
    }
    public void addInstrument(OMRInstrument instrument) {
        this.instruments.add(instrument);
    }
    public void removeInstrument(OMRInstrument instrument) {
        this.instruments.remove(instrument);
    }

    public void addRegion(OMRRegion region) {
        this.regions.add(region);
    }
    public void removeRegion(OMRRegion region) {
        this.regions.remove(region);
    }

    public ObservableSet<OMRRegion> getRegions() {
        return regions;
    }

    /**
     * Hide all staves but shown
     */
    /*public void onStaffShown(OMRStaff shownStaff) {
        for (OMRRegion region: regions) {
            if (region != shownStaff) {
                region.selectedProperty().set(false);
            }
        }
    }*/

    public OMRImage getOMMRImage() {
        return omrImage;
    }

    /*public OMRMainController getOMRController() {
        return omrProject.getOMRController();
    }*/


    public boolean containsInstrument(OMRInstrument instrument) {
        return instruments.contains(instrument);
    }

    public void clearRegions() {
        this.regions.clear();
    }

    /*public void addRegions(List<Region> regions) {
        for (Region region: regions) {
            this.regions.add(new OMRRegion(this, region));
        }
    }*/

    public Page createPOJO() throws IM3Exception {
        Page pojoPage = new Page(new BoundingBoxXY(fromX.get(), fromY.get(), fromX.get()+width.get(), fromY.get()+height.get()));
        for (OMRRegion region: regions) {
            pojoPage.getRegions().add(region.createPOJO());
        }
        return pojoPage;
    }

    /**
     *
     * @param pojoRegion
     * @return null if not found
     */
    public OMRRegion findRegion(Region pojoRegion) {
        for (OMRRegion region: regions) {
            if (region.getFromX() == pojoRegion.getBoundingBox().getFromX()
                && region.getFromY() == pojoRegion.getBoundingBox().getFromY()
                && region.getWidth() == pojoRegion.getBoundingBox().getToX() - pojoRegion.getBoundingBox().getFromX()
                && region.getHeight() == pojoRegion.getBoundingBox().getToY() - pojoRegion.getBoundingBox().getFromY()
                    && region.getRegionType() == pojoRegion.getRegionType()) {
                return region;
            }
        }
        return null;
    }

    /*public URL getImageFileURL() {
        try {
            return imageFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IM3RuntimeException(e);
        }
    }*/

    public LayoutFont getManuscriptLayoutFont() {
        return omrImage.omrProject.getManuscriptLayoutFont();
    }

    public NotationType getNotationType() {
        return omrImage.getOmrProject().getNotationType();
    }


    /*public BufferedImage getBufferedImage() {
        return omrImage.getBufferedImage(); // TODO: 21/4/18 Subimage!!!!
    }*/

    public URL getImageFileURL() throws MalformedURLException {
        return omrImage.getImageFile().toURI().toURL(); // TODO: 21/4/18 ¿Está bien?
    }


    @Override
    public DoubleProperty fromXProperty() {
        return fromX;
    }

    @Override
    public DoubleProperty widthProperty() {
        return width;
    }

    @Override
    public DoubleProperty fromYProperty() {
        return fromY;
    }

    @Override
    public DoubleProperty heightProperty() {
        return height;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public int compareTo(OMRPage o) {
        if (fromX.get() < o.fromX.get()) {
            return -1;
        } else if (fromX.get() > o.fromX.get()) {
            return 1;
        } else if (fromY.get() < o.fromY.get()) {
            return -1;
        } else if (fromY.get() > o.fromY.get()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return name.get();
    }

    public double getFromX() {
        return fromX.get();
    }

    public void setFromX(double fromX) {
        this.fromX.set(fromX);
    }

    public double getFromY() {
        return fromY.get();
    }

    public void setFromY(double fromY) {
        this.fromY.set(fromY);
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public void splitRegionAt(double y) throws IM3Exception {
        if (regions.isEmpty()) {
            OMRRegion omrRegion1 = new OMRRegion(this, 1, fromX.get(), 0, fromX.get()+width.get(), y-1, RegionType.staff);
            OMRRegion omrRegion2 = new OMRRegion(this, 2, fromX.get(), y, fromX.get()+width.get(), height.get(), RegionType.staff);
            this.addRegion(omrRegion1);
            this.addRegion(omrRegion2);
        } else {
            for (OMRRegion region: regions) {
                double fromY = region.getFromY();
                double toY = region.getFromY()+region.getHeight();
                if (y == fromY) {
                    throw new IM3Exception("Splitting region at other region beginning " + region);
                }
                if (y == toY) {
                    throw new IM3Exception("Splitting region at other region ending " + region);
                }
                if (y > fromY && y < toY) {
                    region.heightProperty().setValue(y - 1 - fromY);
                    OMRRegion newRegion = new OMRRegion(this, regions.size() + 1, region.getFromX(), y, region.getWidth(), toY - y, RegionType.staff);
                    regions.add(newRegion);

                    // now arrange all symbols so that they fall in the correct region
                    LinkedList<OMRSymbol> symbolsToMoveToNewRegion= new LinkedList<>();
                    for (OMRSymbol symbol: region.symbolsProperty()) {
                        if (!region.containsAbsoluteCoordinate(symbol.getCenterX(), symbol.getCenterY())) {
                            if (newRegion.containsAbsoluteCoordinate(symbol.getX(), symbol.getY())) {
                                symbolsToMoveToNewRegion.add(symbol);
                            } else {
                                throw new IM3Exception("All symbols should be inside the old or the new region!!");
                            }
                        }
                    }

                    for (OMRSymbol symbol: symbolsToMoveToNewRegion) {
                        symbol.setOMRRegion(newRegion);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OMRPage)) return false;
        OMRPage omrPage = (OMRPage) o;
        return Objects.equals(fromX, omrPage.fromX) &&
                Objects.equals(fromY, omrPage.fromY) &&
                Objects.equals(width, omrPage.width) &&
                Objects.equals(height, omrPage.height) &&
                Objects.equals(name, omrPage.name) &&
                Objects.equals(omrImage, omrPage.omrImage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fromX, fromY, width, height, name, omrImage);
    }
}
