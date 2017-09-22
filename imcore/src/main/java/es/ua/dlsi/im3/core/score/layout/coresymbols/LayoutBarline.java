package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class LayoutBarline extends LayoutSymbolInStaff {
    Line line;
    Time time;

    public LayoutBarline(LayoutStaff layoutStaff, Time time) throws IM3Exception {
        super(layoutStaff, null);
        this.time = time;
        // TODO: 21/9/17 Sólo vale para pentagramas - debe sobresalir igual con percusión
        Coordinate from = new Coordinate(position.getX(), layoutStaff.getYAtLine(1));
        Coordinate to = new Coordinate(position.getX(), layoutStaff.getYAtLine(5));

        line = new Line("BARLINE-", from, to);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    @Override
    public Time getTime() {
        return time;
    }
}
