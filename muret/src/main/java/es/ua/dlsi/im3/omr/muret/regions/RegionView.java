package es.ua.dlsi.im3.omr.muret.regions;

import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class RegionView extends BoundingBoxBasedView<OMRRegion> {
    public RegionView(OMRRegion owner, Color color) {
        super(owner.getOMRPage(), owner, color);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }
}
