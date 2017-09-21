package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class Dot extends Component<NotePitch> {
    Pictogram pictogram;
    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public Dot(LayoutFont layoutFont, NotePitch parent, Coordinate position) throws IM3Exception {
        super(parent, position);
        pictogram = new Pictogram(layoutFont, "augmentationDot", position);
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }
}
