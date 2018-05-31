package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.GrayscaleImageData;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class RegionView extends BoundingBoxBasedView<OMRRegion> {
    private static final double STAFF_HEIGHT = LayoutConstants.EM * 3;
    VBox vBox;
    /**
     * For overlaying imageView and symbols
     */
    Group imageViewWithSymbols;
    /**
     * Extract of the full image using a viewport over the full image
     */
    ImageView imageView;
    /**
     * Bounding boxes
     */
    Group symbolsBoundingBoxesGroup;
    /**
     * Full image from the file
     */
    Image fullImage;
    /**
     * Contains a staff with agnostic symbols on top
     */
    AgnosticStaffView agnosticStaffView;
    /**
     * Contains a staff view of a rendered score
     */
    SemanticStaffView semanticStaffView;
    /**
     * The font for the agnostic staff view
     */
    private AgnosticSymbolFont agnosticSymbolFont;

    DraggableRectangle newSymbolBoundingBox;

    public RegionView(ImageBasedAbstractController controller, AgnosticSymbolFont agnosticSymbolFont, PageView parentBoundingBox, OMRRegion owner, Color color) throws IM3Exception {
        super(controller, parentBoundingBox, 0.0, 0.0, owner.getWidth(), owner.getHeight() + STAFF_HEIGHT, owner, color);
        this.agnosticSymbolFont = agnosticSymbolFont;
        createImageView();
        createAgnosticStaffView();
        createSemanticStaffView();
        vBox = new VBox(5);
        vBox.getChildren().addAll(imageViewWithSymbols, agnosticStaffView);
        this.getChildren().add(vBox);
        initSemantic();
    }

    private void initSemantic() {
        vBox.getChildren().add(semanticStaffView);
        SymbolCorrectionController scontroller = (SymbolCorrectionController) controller;
        semanticStaffView.visibleProperty().bind(scontroller.editMusicEnabledProperty());
    }

    private void createImageView() throws IM3Exception {
        fullImage = owner.getOMRPage().getOMMRImage().getImage();
        imageView = new ImageView(fullImage);
        imageView.setViewport(new Rectangle2D(owner.getFromX(), owner.getFromY(), owner.getWidth(), owner.getHeight()));
        symbolsBoundingBoxesGroup = new Group();
        imageViewWithSymbols = new Group();
        imageViewWithSymbols.getChildren().addAll(imageView, symbolsBoundingBoxesGroup); // order is important
    }

    private void createAgnosticStaffView() throws IM3Exception {
        agnosticStaffView = new AgnosticStaffView(controller, agnosticSymbolFont, owner.getWidth(), STAFF_HEIGHT, -getOwner().getFromX());
    }

    private void createSemanticStaffView() {
        semanticStaffView = new SemanticStaffView();
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }

    public AgnosticStaffView getAgnosticStaffView() {
        return agnosticStaffView;
    }

    public void addSymbolView(SymbolView symbolView) {
        symbolsBoundingBoxesGroup.getChildren().add(symbolView);
    }

    @Override
    public void handle(KeyEvent event) {
        super.handle(event);
        switch (event.getCode()) {
            default:
                agnosticStaffView.handle(event);
        }
    }

    public void delete(SymbolView symbolView) {
        //TODO Command
        // it changes the model and ImageBasedAbstractController, that is bound to model changes propagates all changes in the view
        this.owner.removeSymbol(symbolView.getOwner());
    }

    @Override
    public void onSymbolRemoved(BoundingBoxBasedView elementView) {
        super.onSymbolRemoved(elementView);
        symbolsBoundingBoxesGroup.getChildren().remove(elementView);
        agnosticStaffView.onSymbolRemoved(elementView);
    }

    @Override
    protected void doMousePressed(MouseEvent event) {
        super.doMousePressed(event);

        SymbolCorrectionController symbolCorrectionController = (SymbolCorrectionController) controller;
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            newSymbolBoundingBox = new DraggableRectangle(event.getX(), event.getY(), 1, 1, Color.GOLD);
            newSymbolBoundingBox.setFill(Color.TRANSPARENT);
            newSymbolBoundingBox.setStroke(Color.GOLD);
            newSymbolBoundingBox.setStrokeWidth(2);
            this.getChildren().add(newSymbolBoundingBox);
        }
     }

    @Override
    protected void doMouseReleased(MouseEvent event) {
        super.doMouseReleased(event);
        if (newSymbolBoundingBox != null) { // if adding a symbol
            addNewSymbolWithBoundingBox(
                    newSymbolBoundingBox.xProperty().get()+owner.getFromX(),
                    newSymbolBoundingBox.yProperty().get()+owner.getFromY(),
                    newSymbolBoundingBox.widthProperty().get(),
                    newSymbolBoundingBox.heightProperty().get());

            this.getChildren().remove(newSymbolBoundingBox);
            newSymbolBoundingBox = null;
        }
    }

    @Override
    protected void doMouseDragged(MouseEvent event) {
        super.doMouseDragged(event);
        if (newSymbolBoundingBox != null) {
            newSymbolBoundingBox.widthProperty().setValue(event.getX() - newSymbolBoundingBox.xProperty().getValue() );
            newSymbolBoundingBox.heightProperty().setValue(event.getY() - newSymbolBoundingBox.yProperty().getValue());
        }
    }

    private GrayscaleImageData getGrayScaleImage(double x, double y, double width, double height) throws IM3Exception {
        es.ua.dlsi.im3.omr.model.entities.Image image = this.owner.getOMRPage().getOMMRImage().createPOJO();
        BoundingBox boundingBox = new BoundingBoxXY(x, y, x+width, y+height);
        int[] pixels = image.getGrayscaleImagePixels(owner.getOMRPage().getOMMRImage().getOmrProject().getImagesFolder(), boundingBox);
        return new GrayscaleImageData(pixels);
    }

    private void addNewSymbolWithBoundingBox(double x, double y, double width, double height) {
        if (width > 1 && height > 1) {
            try {
                GrayscaleImageData grayscaleImageData = getGrayScaleImage(x, y, width, height);
                List<AgnosticSymbol> orderedRecognizedSymbols = controller.getDashboard().getModel().classifySymbolFromImage(grayscaleImageData);
                if (orderedRecognizedSymbols == null || orderedRecognizedSymbols.isEmpty()) {
                    throw new IM3Exception("No symbols returned");
                }

                //TODO Mostrar barra corrección con símbolos ordenados
                AgnosticSymbol agnosticSymbol = orderedRecognizedSymbols.get(0);
                OMRSymbol omrSymbol = new OMRSymbol(owner, agnosticSymbol, x, y, width, height);
                owner.addSymbol(omrSymbol); // ImageBasedAbstractController is listening the model for changes and it propagates any change

                SymbolView symbolView = (SymbolView) controller.doSelect(omrSymbol);
                symbolView.doEdit();

            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot add symbol", e);
                ShowError.show(OMRApp.getMainStage(), "Cannot add a symbol", e);
            }
        } else {
            ShowError.show(OMRApp.getMainStage(), "Cannot add a symbols with bounding box of width or height with less than 2 pixels");
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}
