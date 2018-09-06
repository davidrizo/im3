package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Symbol bounding box view
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> implements Comparable<SymbolView> {

    public SymbolView(String ID, MuRETBaseController controller, RegionView regionView, OMRSymbol owner, Color color) {
        super(ID, controller, regionView, owner, color);
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

    public PositionInStaff changePosition(int lineSpaces) {
        return owner.chageRelativePosition(lineSpaces);
    }

    /*public PositionInStaff getPositionInStaff() {
        return owner.getPositionInStaff();
    }

    public void setPositionInStaff(PositionInStaff positionInStaff) {
        owner.setPositionInStaff(positionInStaff);
    }*/

    public AgnosticSymbolType changeSymbolType(AgnosticSymbolType agnosticSymbolType) {
        return owner.changeAgnosticSymbolType(agnosticSymbolType);
    }

    @Override
    public int compareTo(SymbolView o) {
        return owner.compareTo(o.owner);
    }
}
