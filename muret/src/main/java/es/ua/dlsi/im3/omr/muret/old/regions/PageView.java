package es.ua.dlsi.im3.omr.muret.old.regions;

import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.omr.muret.old.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.old.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class PageView extends BoundingBoxBasedView<OMRPage> {
    public PageView(String ID, ImageBasedAbstractController controller, OMRPage owner, Color color) {
        super(ID, controller, null, owner, color);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }

    @Override
    public ISelectableTraversable getSelectionParent() {
        return controller;
    }
}