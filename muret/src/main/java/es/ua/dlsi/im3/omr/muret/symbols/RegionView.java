package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
     * Contains a staff with symbols on top
     */
    AgnosticStaffView agnosticStaffView;
    private AgnosticSymbolFont agnosticSymbolFont;


    public RegionView(AgnosticSymbolFont agnosticSymbolFont, PageView parentBoundingBox, OMRRegion owner, Color color) throws IM3Exception {
        super(parentBoundingBox, 0.0, 0.0, owner.getWidth(), owner.getHeight() + STAFF_HEIGHT, owner, color);
        this.agnosticSymbolFont = agnosticSymbolFont;
        createImageView();
        createStaffView();
        vBox = new VBox(5);
        vBox.getChildren().addAll(imageViewWithSymbols, agnosticStaffView);
        this.getChildren().add(vBox);
    }

    private void createImageView() throws IM3Exception {
        fullImage = owner.getOMRPage().getOMMRImage().getImage();
        imageView = new ImageView(fullImage);
        imageView.setViewport(new Rectangle2D(owner.getFromX(), owner.getFromY(), owner.getWidth(), owner.getHeight()));

        symbolsBoundingBoxesGroup = new Group();
        imageViewWithSymbols = new Group();
        imageViewWithSymbols.getChildren().addAll(imageView, symbolsBoundingBoxesGroup); // order is important
    }

    private void createStaffView() throws IM3Exception {
        agnosticStaffView = new AgnosticStaffView(agnosticSymbolFont, owner.getWidth(), STAFF_HEIGHT, -getOwner().getFromX());
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
}
