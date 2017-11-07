package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.score.PositionAboveBelow;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class LedgerLine extends Component<LedgerLines> {
    Line line;
    int lineNumber;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public LedgerLine(LedgerLines parent, Coordinate position, double noteHeadWidth) {
        super(null, parent, position);
        Coordinate from = new Coordinate(
                new CoordinateComponent(position.getX(), -LayoutConstants.LEDGER_LINE_EXCESS_OVER_NOTE_HEAD),
                new CoordinateComponent(position.getY()));

        Coordinate to = new Coordinate(
                // FIXME: 22/9/17 /2 a piñón
                new CoordinateComponent(position.getX(), noteHeadWidth+LayoutConstants.LEDGER_LINE_EXCESS_OVER_NOTE_HEAD/2), // TODO: 2/11/17 IMPORTANTE ¿¿¿¿¿?????
                new CoordinateComponent(position.getY()));


        line = new Line("LEDGER-LINE-", from, to); //TODO IDS
    }


    @Override
    public GraphicsElement getGraphics() {
        return line;
    }
}
