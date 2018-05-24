package es.ua.dlsi.im3.omr.muret.regions;

import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Symbol bounding box view
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> {

    public SymbolView(ImageBasedAbstractController controller, RegionView regionView, OMRSymbol owner, Color color) {
        super(controller, regionView, owner, color);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }
}
