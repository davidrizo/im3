package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Barline;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class LayoutBarline extends LayoutSymbolInStaff<Barline> {
    Line line;

    public LayoutBarline(LayoutStaff layoutStaff, Barline coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        // TODO: 21/9/17 Sólo vale para pentagramas - debe sobresalir igual con percusión
        Coordinate from = new Coordinate(position.getX(), layoutStaff.getYAtLine(1));
        Coordinate to = new Coordinate(position.getX(), layoutStaff.getYAtLine(5));

        line = new Line(from, to);
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }
}
