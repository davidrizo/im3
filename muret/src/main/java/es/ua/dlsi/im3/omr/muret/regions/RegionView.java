package es.ua.dlsi.im3.omr.muret.regions;

import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class RegionView extends BoundingBoxBasedView<OMRRegion> {
    public RegionView(ImageBasedAbstractController controller, PageView pageView, OMRRegion owner, Color color) {
        super(controller, pageView, owner, color);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }
}
