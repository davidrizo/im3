package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.Direction;

/**
 * Symbol that can belong to a connector
 */
public interface IConnectable {
    Coordinate getConnectionPoint(Direction direction);
}
