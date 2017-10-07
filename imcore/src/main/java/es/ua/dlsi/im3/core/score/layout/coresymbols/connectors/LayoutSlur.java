package es.ua.dlsi.im3.core.score.layout.coresymbols.connectors;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.IConnectableWithSlur;
import es.ua.dlsi.im3.core.score.layout.graphics.Bezier;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutSlur extends LayoutConnector {
    protected Bezier bezier;

    public LayoutSlur(IConnectableWithSlur from, IConnectableWithSlur to) throws IM3Exception {
        // TODO: 1/10/17 Que se pueda cambiar la dirección según la posición
        Direction directionFrom = from.getDefaultSlurDirection();
        Direction directionTo = to.getDefaultSlurDirection();

        Coordinate fromPoint = from.getConnectionPoint(directionFrom);
        Coordinate toPoint = to.getConnectionPoint(directionTo);
        init(fromPoint, directionFrom, toPoint, directionTo);
    }

    private void init(Coordinate from, Direction directionFrom, Coordinate to, Direction directionTo) throws IM3Exception {
        Coordinate controlFrom = null;
        Coordinate controlTo = null;

        // TODO: 1/10/17 Grosor del slur

        CoordinateComponent controlXFrom = from.getX();
        CoordinateComponent controlXTo = to.getX();

        controlFrom = createControlCoordinate(directionFrom, from, controlXFrom);
        controlTo = createControlCoordinate(directionFrom, to, controlXTo);

        bezier = new Bezier("BEZ-", from, controlFrom, controlTo, to);
    }

    @Override
    public GraphicsElement getGraphics() {
        return bezier;
    }

    private Coordinate createControlCoordinate(Direction direction, Coordinate reference, CoordinateComponent x) {
        Coordinate coordinate;
        if (direction == Direction.up) {
            coordinate = new Coordinate(
                    x,
                    new CoordinateComponent(reference.getY(), -LayoutConstants.SLUR_HEIGHT)
            );
        } else {
            coordinate = new Coordinate(
                    x,
                    new CoordinateComponent(reference.getY(), LayoutConstants.SLUR_HEIGHT)
            );
        }
        return coordinate;
    }
}
