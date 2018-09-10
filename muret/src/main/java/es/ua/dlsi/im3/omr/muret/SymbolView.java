package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.old.symbols.StrokesView;
import javafx.scene.Group;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Symbol bounding box view
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> implements Comparable<SymbolView> {
    public static final Color STROKES_COLOR = Color.LIGHTGREEN;

    private StrokesView strokesView;

    public SymbolView(String ID, MuRETBaseController controller, RegionView regionView, OMRSymbol owner, Color color) {
        super(ID, controller, regionView, owner, color);

        if (owner.getStrokes() != null) {
            //strokesView = new StrokesView(owner.getStrokes(), -regionView.getOwner().getFromX(), -regionView.getOwner().getFromY(), es.ua.dlsi.im3.omr.muret.old.symbols.RegionView.STROKES_COLOR); //TODO Strokes color no así
            strokesView = new StrokesView(owner.getStrokes(), 0, 0, STROKES_COLOR); //TODO Strokes color no así
            this.getChildren().add(strokesView);
        }
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
