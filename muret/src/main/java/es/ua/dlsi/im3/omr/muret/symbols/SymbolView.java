package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> {
    public SymbolView(RegionView regionView, OMRSymbol owner, Color color) {
        super(regionView, owner.getX()-regionView.getOwner().getFromX(), owner.getY()-regionView.getOwner().getFromY(), owner.getWidth(), owner.getHeight(), owner, color);
        regionView.getAgnosticStaffView().addSymbol(-regionView.getOwner().getFromX(), owner);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }
}
