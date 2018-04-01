package es.ua.dlsi.im3.core.score.layout.coresymbols.connectors;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.IConnectableWithSlurInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.Bezier;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutSlur extends LayoutConnector {
    protected Bezier bezier;

    /**
     *
     * @param from if null, it is a broken slur (those used to connect a slur from a previous system)
     * @param to if null, it is a broken slur (those used to connect a slur to the next system)
     * @throws IM3Exception
     */
    public LayoutSlur(IConnectableWithSlurInStaff from, IConnectableWithSlurInStaff to) throws IM3Exception {
        // TODO: 1/10/17 Que se pueda cambiar la dirección según la posición
        if (from == null && to == null) {
            throw new IM3Exception("from and to cannot be null");
        }
        Direction directionFrom = from == null?to.getDefaultSlurDirection():from.getDefaultSlurDirection();
        Direction directionTo = to == null?from.getDefaultSlurDirection():to.getDefaultSlurDirection();

        /*if (from != null) {
            if (from instanceof LayoutCoreSingleFigureAtom) {
                LayoutCoreSingleFigureAtom f = (LayoutCoreSingleFigureAtom) from;
                System.out.println("FROM: " + f.getCoreSymbol().__getID() + " " + f.isStemUp() + " " + directionFrom);
            }

        }

        if (to != null) {
            if (to instanceof LayoutCoreSingleFigureAtom) {
                LayoutCoreSingleFigureAtom f = (LayoutCoreSingleFigureAtom) to;
                System.out.println("TO: " + f.getCoreSymbol().__getID() + " " + f.isStemUp() + " " + directionFrom);

                if ("B80".equals(f.getCoreSymbol().__getID())) {
                    System.out.println("PARO");
                }
            }

        }*/

        // TODO: 11/2/18 todo ABOVE BELOW
        Coordinate fromPoint;
        if (from == null) {
            fromPoint = new Coordinate(
                    new CoordinateComponent(to.getConnectionPoint(directionTo).getX(), -LayoutConstants.BROKEN_SLUR_WIDTH),
                    to.getConnectionPoint(directionTo).getY());

        } else {
            fromPoint = from.getConnectionPoint(directionFrom);
        }
        Coordinate toPoint;
        if (to == null) {
            toPoint = new Coordinate(
                    new CoordinateComponent(from.getConnectionPoint(directionFrom).getX(), LayoutConstants.BROKEN_SLUR_WIDTH),
                    from.getConnectionPoint(directionFrom).getY());

        } else {
            toPoint = to.getConnectionPoint(directionTo);
        }


        init(fromPoint, directionFrom, toPoint, directionTo);
    }
    private void init(Coordinate from, Direction directionFrom, Coordinate to, Direction directionTo) {
        Coordinate controlFrom = null;
        Coordinate controlTo = null;

        // TODO: 1/10/17 Grosor del slur

        CoordinateComponent controlXFrom = from.getX();
        CoordinateComponent controlXTo = to.getX();

        controlFrom = createControlCoordinate(directionFrom, from, controlXFrom);
        controlTo = createControlCoordinate(directionFrom, to, controlXTo);

        bezier = new Bezier(this, InteractionElementType.slur, from, controlFrom, controlTo, to);
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

    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
