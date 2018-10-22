package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class RegionView extends BoundingBoxBasedView<OMRRegion> implements Comparable<RegionView> {
    public RegionView(String ID, MuRETBaseController controller, PageView pageView, OMRRegion owner, Color color) {
        super(ID, controller, pageView, owner, color);
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

    @Override
    public int compareTo(RegionView o) {
        return owner.compareTo(o.owner);
    }
}