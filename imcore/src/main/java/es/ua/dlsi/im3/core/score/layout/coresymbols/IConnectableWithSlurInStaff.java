package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.layout.Direction;

public interface IConnectableWithSlurInStaff extends IConnectable {
    Direction getDefaultSlurDirection();
    LayoutStaff getLayoutStaff();
}
