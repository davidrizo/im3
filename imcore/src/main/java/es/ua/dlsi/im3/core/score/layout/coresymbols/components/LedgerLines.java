package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.PositionAboveBelow;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LedgerLines extends Component<LayoutStaff> {
    private final double noteHeadWidth;
    HashMap<PositionAboveBelow, List<LedgerLine>> ledgerLines;

    Group group;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     * @param positionAboveBelow
     * @param numberOfLines
     */
    public LedgerLines(LayoutStaff parent, Coordinate position, double noteHeadWidth, PositionAboveBelow positionAboveBelow, int numberOfLines) {
        super(parent, position);
        this.noteHeadWidth = noteHeadWidth;
        group = new Group();
        ensure(numberOfLines, positionAboveBelow);
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    public final void ensure(int numberOfLines, PositionAboveBelow positionAboveBelow) {
        if (ledgerLines == null) {
            ledgerLines = new HashMap<>();
        }
        List<LedgerLine> list = ledgerLines.get(positionAboveBelow);
        if (list == null) {
            list = new ArrayList<>();
            ledgerLines.put(positionAboveBelow, list);
        }
        if (list.size() < numberOfLines) {
            for (int nline = list.size(); nline < numberOfLines; nline++) {
                CoordinateComponent y;
                if (positionAboveBelow == PositionAboveBelow.ABOVE) {
                    y = new CoordinateComponent(parent.getTopLine().getPosition().getY(), -(nline + 1) * LayoutConstants.SPACE_HEIGHT);
                } else if (positionAboveBelow == PositionAboveBelow.ABOVE) {
                    y = new CoordinateComponent(parent.getBottomLine().getPosition().getY(), (nline + 1)* LayoutConstants.SPACE_HEIGHT);
                } else {
                    throw new IM3RuntimeException("Invalid position: " + positionAboveBelow);
                }

                Coordinate ledgerLinePosition = new Coordinate(position.getX(), y);
                LedgerLine ledgerLine = new LedgerLine(this, ledgerLinePosition, noteHeadWidth);
                list.add(ledgerLine);
                group.add(ledgerLine.getGraphics());
            }
        }
    }
}
