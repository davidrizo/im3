package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxYX;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.model.entities.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Image file
 * @autor drizo
 */
public class OMRImage implements Comparable<OMRImage> {
    /**
     * Parent project
     */
    OMRProject omrProject;
    /**
     * Actual file
     */
    File imageFile;
    /**
     * Order inside project
     */
    IntegerProperty order;
    /**
     * Used for unit tests
     */
    static boolean SKIP_JAVAFX = false;
    /**
     * Used for the GUI
     */
    private ObservableObjectValue<javafx.scene.image.Image> image;
    /**
     * Used to extract pixels from it
     */
    //BufferedImage bufferedImage;

    /**
     * Ordered pages
     */
    ObservableSet<OMRPage> pages;
    /**
     * Size of the image
     */
    private ObjectProperty<BoundingBoxXY> boundingBox;

    /**
     * Comments about the image
     */
    private StringProperty comments;

    /**
     * Instrument, it may be null if not the same for all pages
     */
    ObjectProperty<OMRInstrument> instrument;



    public OMRImage(OMRProject omrProject, File file) {
        this.omrProject = omrProject;
        this.imageFile = file;
        this.order = new SimpleIntegerProperty();
        this.pages = FXCollections.observableSet(new TreeSet<>());
        this.comments = new SimpleStringProperty();
        this.image = new SimpleObjectProperty<>();
        this.instrument = new SimpleObjectProperty<>();
        //loadImageFile();
    }

    public String getComments() {
        return comments.get();
    }

    public StringProperty commentsProperty() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }

    void loadImageFile() throws IM3Exception {
        //checkJAI();

        javafx.scene.image.Image img = null;
        try {
            if (!SKIP_JAVAFX) {
                img = new javafx.scene.image.Image(imageFile.toURI().toURL().toString());

                boundingBox = new SimpleObjectProperty(new BoundingBoxXY(
                        0,
                        0,
                        img.getWidth(),
                        img.getHeight()
                ));

                this.image = new SimpleObjectProperty<>(img);
                Logger.getLogger(OMRImage.class.getName()).log(Level.INFO, "Loading image {0}, width={1}, height={2}",
                        new Object[]{imageFile.getAbsolutePath(), img.getWidth(), img.getHeight()});
            } else {
                this.image = new SimpleObjectProperty<>();
            }

            //bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new IM3Exception(e);
        }
    }

    /*private void checkJAI() throws IM3Exception {
        if (!JAI_CHECKED) {
            Instant start = Instant.now();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Loading JAI");
            JAI_CHECKED = true;

            Iterator<ImageReader> reader = ImageIO.getImageReadersByFormatName("TIFF");
            if (reader == null || !reader.hasNext()) { // pom.xml needs jai-imageio-core for loading TIFF files
                throw new IM3Exception("TIFF format not supported");
            }

            Instant end = Instant.now();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "JAI loaded in {0}", TimeUtils.getTimeElapsed(start, end));
        }
    }*/


    public ObservableSet<OMRPage> getPages() {
        return pages;
    }

    /**
     * Delete file from the disk
     */
    public void deleteFile() throws IM3Exception {
        if (!imageFile.delete()) {
            throw new IM3Exception("Cannot remove file " + imageFile.getAbsolutePath() + " from disk");
        }
    }

    public int getOrder() {
        return order.get();
    }

    public IntegerProperty orderProperty() {
        return order;
    }

    public void setOrder(int order) {
        this.order.set(order);
    }


    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public javafx.scene.image.Image getImage() throws IM3Exception {
        if (image.get() == null) {
            loadImageFile();
        }
        return image.get();
    }

    /*public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }*/

    public Image createPOJO() throws IM3Exception {
        Image pojoImage = new Image(FileUtils.getFileWithoutPath(imageFile.getName()));
        if (instrument.isNotNull().get()) {
            pojoImage.setInstrument(new Instrument(instrument.get().getName()));
        }
        pojoImage.setOrder(order.get());
        pojoImage.setComments(comments.get());
        for (OMRPage omrPage: pages) {
            Page pojoPage = omrPage.createPOJO();
            pojoImage.addPage(pojoPage);
        }
        return pojoImage;
    }

    @Override
    public String toString() {
        return "Image: " + order.get() + ": " + imageFile.getName();
    }

    @Override
    public int compareTo(OMRImage o) {
        int diff = order.get() - o.order.get();
        if (diff == 0) {
            diff = o.getImageRelativeFileName().compareTo(o.getImageRelativeFileName());
        }
        return diff;
    }

    public OMRProject getOmrProject() {
        return omrProject;
    }

    public String getImageRelativeFileName() {
        return FileUtils.getFileWithoutPath(imageFile.getName());
    }

    public void addPage(OMRPage omrPage) {
        this.pages.add(omrPage);
    }

    /**
     * It changes the region of the selected symbols.
     * @param selectedSymbols
     * @param region
     */
    public void changeRegion(List<OMRSymbol> selectedSymbols, OMRRegion region) throws IM3Exception {
        for (OMRSymbol omrSymbol: selectedSymbols) {
            omrSymbol.setOMRRegion(region);
        }
    }

    public void recomputeRegionBoundingBoxes() throws IM3Exception {
        for (OMRPage page: pages) {
            for (OMRRegion region: page.regionsProperty()) {
                double fromX = Double.MAX_VALUE;
                double fromY = Double.MAX_VALUE;
                double toX = Double.MIN_VALUE;
                double toY = Double.MIN_VALUE;
                for (OMRSymbol symbol: region.symbolsProperty()) {
                    fromX = Math.min(fromX, symbol.getX());
                    fromY = Math.min(fromY, symbol.getY());
                    toX = Math.max(toX, symbol.getX() + symbol.getWidth());
                    toY = Math.max(toY, symbol.getY() + symbol.getHeight());
                }
                region.setBoundingBox(new BoundingBoxYX(fromX, fromY, toX, toY));
            }
        }
    }

    /**
     * It creates a new region with the selected symbols. If they were inside other regions they are moved
     */
    /*public void createRegion(OMRPage page, RegionType regionType, List<OMRSymbol> selectedSymbols) {
        // first compute the new bounding box
        List<BoundingBox> boundingBoxes = new LinkedList<>();
        for (OMRSymbol symbol: selectedSymbols) {
            boundingBoxes.add(symbol.getBoundingBox());
        }
        BoundingBox boundingBox = new BoundingBox(Double.MAX_VALUE, )

        OMRRegion region = new OMRRegion(page, )
        for (OMR)
    }*/


    public BoundingBoxXY getBoundingBox() {
        return boundingBox.get();
    }

    public ObjectProperty<BoundingBoxXY> boundingBoxProperty() {
        return boundingBox;
    }

    /**
     * It removes all pages and regions and add new symbols, all in one region and one page
     * @param symbols
     */
    public void replaceSymbols(List<Symbol> symbols) throws IM3Exception {
        pages.clear();

        OMRPage page = new OMRPage(this, boundingBox.get().getFromX(), boundingBox.get().getFromY(), boundingBox.get().getToX(), boundingBox.get().getToY());

        //TODO Partir página
        this.addPage(page);
        OMRRegion region = new OMRRegion(page, 1, boundingBox.get().getFromX(), boundingBox.get().getFromY(), boundingBox.get().getToX(), boundingBox.get().getToY(), RegionType.all);
        page.addRegion(region);
        for (Symbol symbol: symbols) {
            OMRSymbol omrSymbol = new OMRSymbol(region, symbol);
            region.addSymbol(omrSymbol);
        }
    }

    /**
     * It returns the symbols in all pages and regions
     * @return
     */
    public List<OMRSymbol> getAllSymbols() {
        List<OMRSymbol> result = new LinkedList<>();
        for (OMRPage page: pages) {
            for (OMRRegion region: page.regionsProperty()) {
                result.addAll(region.symbolsProperty());
            }
        }
        return result;
    }

    /**
     * It removes all content
     */
    public void clear() {
        this.pages.clear();
    }

    public void deletePage(OMRPage page) {
        pages.remove(page);
    }

    /**
     * If the image has no pages it divides the image into two pages
     * @param x
     */
    public void splitPageAt(double x) throws IM3Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Splitting page at {0}" , x);

        if (pages.isEmpty()) {
            OMRPage omrPage1 = new OMRPage(this, 0, 0, x, this.boundingBox.get().getToY());
            OMRPage omrPage2 = new OMRPage(this, x+1, 0, this.boundingBox.get().getToX(), this.boundingBox.get().getToY());

            this.addPage(omrPage1);
            this.addPage(omrPage2);
        } else {
            for (OMRPage page: pages) {
                if (page.getFromX() == x) {
                    throw new IM3Exception("The specified position is the same as the stating of page " + page);
                }
                if (page.getFromX()+page.getWidth() == x) {
                    throw new IM3Exception("The specified position is the same as the end of page " + page);
                }

                if (page.getFromX() < x && x < page.getFromX()+page.getWidth()) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Splitting page {0}", page);

                    OMRPage newPage = new OMRPage(this, x, 0, page.getFromX()+page.getWidth(), this.boundingBox.get().getToY());
                    page.setWidth(x - page.getFromX()-1);
                    this.addPage(newPage);

                    // create a region for new page
                    OMRRegion newRegion = new OMRRegion(newPage, 2, newPage.getFromX(), newPage.getFromY(), newPage.getWidth(), newPage.getHeight(), RegionType.all);
                    newPage.addRegion(newRegion);

                    // not move all symbols in new page and change the width of previous regions
                    for (OMRRegion region: page.regionsProperty()) {
                        region.setWidth(page.getWidth()); // change the width of all regions inside

                        LinkedList<OMRSymbol> symbols = new LinkedList<>();
                        for (OMRSymbol symbol: region.symbolsProperty()) {
                            if (!region.containsAbsoluteCoordinate(symbol.getX(),symbol.getY())) {
                                if (newRegion.containsAbsoluteCoordinate(symbol.getX(), symbol.getY())) {
                                    symbols.add(symbol);
                                } else {
                                    throw new IM3Exception("No region contains the symbol!!!");
                                }
                            }
                        }
                        for (OMRSymbol omrSymbol: symbols) { // avoid concurrent modification above
                            omrSymbol.setOMRRegion(newRegion);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void splitRegionAt(double x, double y) throws IM3Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Splitting region at {0},{1}" , new Object[]{x, y});
        if (pages.isEmpty()) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "No page, creating a single page");
            OMRPage omrPage1 = new OMRPage(this, 0, 0, this.getBoundingBox().getToX(), this.boundingBox.get().getToY());
            this.addPage(omrPage1);
        }

        // first locate the page
        for (OMRPage page: pages) {
            if (page.getFromX() == x) {
                throw new IM3Exception("The specified position is the same as the stating of page " + page);
            }
            if (page.getFromX() + page.getWidth() == x) {
                throw new IM3Exception("The specified position is the same as the end of page " + page);
            }

            if (page.getFromX() < x && x < page.getFromX() + page.getWidth()) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Splitting region at {0},{1} in page {2}" , new Object[]{x, y, page});
                page.splitRegionAt(y);
            }
        }
    }

    /**
     * It creates one page and region and puts every symbol inside it
     */
    public void leaveJustOnePageAndRegion() throws IM3Exception {
        OMRPage onePage = new OMRPage(this, 0, 0, this.getBoundingBox().getToX(), this.getBoundingBox().getToY());
        OMRRegion oneRegion = new OMRRegion(onePage, 1, 0, 0, this.getBoundingBox().getToX(), this.getBoundingBox().getToY(), RegionType.all);
        onePage.addRegion(oneRegion);
        LinkedList<OMRSymbol> symbols = new LinkedList<>();
        for (OMRPage page: pages) {
            for (OMRRegion omrRegion: page.regionsProperty()) {
                symbols.addAll(omrRegion.symbolsProperty());
            }
        }
        for (OMRSymbol omrSymbol: symbols) { // avoid concurrent modification above
            omrSymbol.setOMRRegion(oneRegion);
        }
        pages.clear();
        pages.add(onePage);
    }

    public OMRInstrument getInstrument() {
        return instrument.get();
    }

    public ObjectProperty<OMRInstrument> instrumentProperty() {
        return instrument;
    }

}


