package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.score.BeamedGroup;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

public class LayoutCoreBeamedGroup extends LayoutCoreSymbol<BeamedGroup> {


    public LayoutCoreBeamedGroup(LayoutFont layoutFont, BeamedGroup coreSymbol) {
        super(layoutFont, coreSymbol);
    }

    @Override
    public GraphicsElement getGraphics() {
        return new Group("BORRAR");
    }// FIXME: 24/9/17 Quitar esta clase
}
