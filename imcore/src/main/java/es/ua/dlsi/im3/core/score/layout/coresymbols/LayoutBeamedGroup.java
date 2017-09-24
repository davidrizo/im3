package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.BeamedGroup;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

public class LayoutBeamedGroup extends LayoutSymbolInStaff<BeamedGroup> {


    public LayoutBeamedGroup(LayoutStaff layoutStaff, BeamedGroup coreSymbol) {
        super(layoutStaff, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return new Group("BORRAR");
    }// FIXME: 24/9/17 Quitar esta clase
}
