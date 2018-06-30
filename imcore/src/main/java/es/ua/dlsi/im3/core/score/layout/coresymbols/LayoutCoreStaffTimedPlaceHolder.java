package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.StaffTimedPlaceHolder;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.Direction;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

/**
 * Currently we don't show any visual representation
 */
public class LayoutCoreStaffTimedPlaceHolder extends LayoutCoreSymbolInStaff<StaffTimedPlaceHolder> implements IConnectableWithSlurInStaff {
    Line line;
    public LayoutCoreStaffTimedPlaceHolder(LayoutFont layoutFont, StaffTimedPlaceHolder coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        Coordinate from = position;
        Coordinate to = position; // empty
        line = new Line(this, InteractionElementType.staffTimePlaceHolder, from, to);
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    @Override
    public Direction getDefaultSlurDirection() {
        return Direction.up; // TODO: 31/10/17
    }

    @Override
    public Coordinate getConnectionPoint(Direction direction) {
        return position;
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() {

    }
}
