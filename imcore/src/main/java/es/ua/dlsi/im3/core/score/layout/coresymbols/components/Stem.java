package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
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

        line = new Line(this, InteractionElementType.stem, from, to);
    }

    public void changeStemDirection() {
        line.getTo().setDisplacementY(line.getTo().getDisplacementY()*-1);
    }

    public void setXDisplacement(double stemXDisplacement) {
        line.getFrom().setDisplacementX(stemXDisplacement);
        line.getTo().setDisplacementX(stemXDisplacement);
    }

    public void setEndAbsoluteY(double toAbsoluteY) throws IM3Exception {
        line.getTo().setDisplacementY(toAbsoluteY-line.getTo().getAbsoluteY());
    }

    public void setStartY(CoordinateComponent referenceY, double ydisplacement) {
        line.getFrom().setReferenceY(referenceY);
        line.getFrom().setDisplacementY(ydisplacement);
    }

    public void setEndY(CoordinateComponent referenceY, double ydisplacement) {
        line.getTo().setReferenceY(referenceY);
        line.getTo().setDisplacementY(ydisplacement);
    }


    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    public Coordinate getLineEnd() {
        return line.getTo();
    }

    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }

}
