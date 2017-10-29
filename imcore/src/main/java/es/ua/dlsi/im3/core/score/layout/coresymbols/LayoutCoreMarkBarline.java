package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class LayoutCoreMarkBarline extends LayoutCoreSymbolInStaff<MarkBarline> {
    private final Coordinate from;
    private final Coordinate to;

    Line line;

    public LayoutCoreMarkBarline(LayoutFont layoutFont, MarkBarline coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        this.line = line;

        from = new Coordinate(position.getX(), null);
        to = new Coordinate(position.getX(), null);

        line = new Line("MARKBARLINE-", from, to);//TODO IDS

    }


    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        from.setReferenceY(layoutStaff.getYAtLine(1));
        to.setReferenceY(layoutStaff.getYAtLine(5));

    }
}
