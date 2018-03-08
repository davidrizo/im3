package es.ua.dlsi.im3.core.score.layout.coresymbols.connectors;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.StrokeType;

public class LayoutDashedBarlineAcrossStaves extends LayoutConnector {
    protected Line line;

    public LayoutDashedBarlineAcrossStaves(LayoutCoreBarline from, LayoutStaff to) throws IM3Exception {
        Coordinate fromPoint, toPoint;
        if (from == null) {
            throw new IM3RuntimeException("From is null");
        }
        if (to == null) {
            throw new IM3RuntimeException("To is null");
        }

        if (from.getTopEnd().getAbsoluteY() < to.getBottomLine().getPosition().getAbsoluteY()) {
            fromPoint = new Coordinate(from.getPosition().getX(),
                    from.getBottomEnd().getY());
            toPoint = new Coordinate(from.getPosition().getX(),
                    to.getTopLine().getFrom().getY());
        } else {
            fromPoint = new Coordinate(from.getPosition().getX(),
                    from.getTopEnd().getY());
            toPoint = new Coordinate(from.getPosition().getX(),
                    to.getBottomLine().getFrom().getY());
        }


        init(fromPoint, toPoint);
    }

    private void init(Coordinate from, Coordinate to) {
        line = new Line("DASHED-", from, to); //TODO ID
        line.setStrokeType(StrokeType.eDashed);
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }


}
