package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class Stem extends Component<LayoutSingleFigureAtom> {
    Line line;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public Stem(LayoutSingleFigureAtom parent, Coordinate position, boolean stemUp) {
        super(parent, position);
        Coordinate from = position;
        Coordinate to;

        double yDisplacement = LayoutConstants.SPACE_HEIGHT * 3; // it takes 3 spaces
        if (stemUp) {
            yDisplacement *= -1;
        }
        to = new Coordinate(position.getX(), new CoordinateComponent(position.getY(), yDisplacement));
        
        line = new Line(from, to);
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }
}
