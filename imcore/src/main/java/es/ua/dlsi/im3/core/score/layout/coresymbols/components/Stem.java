package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class Stem extends Component<LayoutCoreSingleFigureAtom> {
    Line line;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public Stem(LayoutCoreSingleFigureAtom parent, Coordinate position, boolean stemUp) {
        super(null, parent, position);
        Coordinate from = position;
        Coordinate to;

        double yDisplacement = LayoutConstants.SPACE_HEIGHT * LayoutConstants.STEM_SPACES;
        if (stemUp) {
            yDisplacement *= -1;
        }
        to = new Coordinate(position.getX(), new CoordinateComponent(position.getY(), yDisplacement));

        line = new Line(InteractionElementType.stem, from, to);
        if (stemUp) {
            double xdisplacement = from.getX().getDisplacement()-1; // line.getWidth();// FIXME: 22/9/17 Based on line thickness
            from.getX().setDisplacement(xdisplacement);
            to.getX().setDisplacement(xdisplacement);
        }
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    public Coordinate getLineEnd() {
        return line.getTo();
    }

    public void setReferenceY(CoordinateComponent stemYPosition) {
        position.setReferenceY(stemYPosition);
    }
}
