package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Component;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * It represents the symbols that drive the rendering of the score.
 *
 * The symbol itself is not drawable, the symbol symbols, connectors and
 * attachments are the drawable components of the symbol
 *
 * @author drizo
 */
public abstract class LayoutSymbolInStaff<CoreSymbolType extends ITimedElementInStaff> extends NotationSymbol {
    protected CoreSymbolType coreSymbol;
    protected LayoutStaff layoutStaff;

    public LayoutSymbolInStaff(LayoutStaff layoutStaff, CoreSymbolType coreSymbol) {
        this.coreSymbol = coreSymbol;
        this.layoutStaff = layoutStaff;
        // Initial value - it will be changed using the displacement of the Coordinate position
        this.position = new Coordinate(new CoordinateComponent(layoutStaff.getPosition().getX()), new CoordinateComponent(layoutStaff.getPosition().getY()));
    }

    public LayoutStaff getLayoutStaff() {
        return layoutStaff;
    }

    /**
     * The duration of the symbol that should be translated in a spacing
     * @return
     */
    public Time getDuration() {
        return Time.TIME_ZERO;
    }

    public Time getTime() {
        return coreSymbol.getTime();
    }

    public CoreSymbolType getCoreSymbol() {
        return coreSymbol;
    }


}
