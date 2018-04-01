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

        line = new Line(this, InteractionElementType.markBarline, from, to);//TODO IDS

    }


    @Override
    public GraphicsElement getGraphics() {
        return line;
    }


    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() throws IM3Exception {
        from.setReferenceY(layoutStaff.getYAtLine(1));
        to.setReferenceY(layoutStaff.getYAtLine(5));
    }
}
