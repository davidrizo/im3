package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class NoteHead extends Component<LayoutSingleFigureAtom> {
    private static final HashMap<Figures, String> UNICODES = new HashMap<>();
    {
        UNICODES.put(Figures.DOUBLE_WHOLE, "noteheadDoubleWhole");
        UNICODES.put(Figures.WHOLE, "noteheadWhole");
        UNICODES.put(Figures.HALF, "noteheadHalf");
        UNICODES.put(Figures.QUARTER, "noteheadBlack");
        UNICODES.put(Figures.EIGHTH, "noteheadBlack");
        UNICODES.put(Figures.SIXTEENTH, "noteheadBlack");
        UNICODES.put(Figures.THIRTY_SECOND, "noteheadBlack");
        UNICODES.put(Figures.SIXTY_FOURTH, "noteheadBlack");
        UNICODES.put(Figures.HUNDRED_TWENTY_EIGHTH, "noteheadBlack");
        UNICODES.put(Figures.TWO_HUNDRED_FIFTY_SIX, "noteheadBlack");
        // TODO Existen hasta la 1024th

        //TODO Mensural
    }


    Pictogram pictogram;

    /**
     * @param parent
     * @param position Important for allowing methods like getWidth() that will be used by the layout algorithms
     */
    public NoteHead(LayoutFont layoutFont, LayoutSingleFigureAtom parent, Coordinate position) throws IM3Exception {
        super(parent, position);
        Coordinate coordinate = new Coordinate(
                new CoordinateComponent(position.getX()),
                position.getY()
        );
        pictogram = new Pictogram(layoutFont, getUnicode(), coordinate);

        //TODO coordinate.getX().setDisplacement(-pictogram.getWidth());
    }

    private String getUnicode() throws IM3Exception {
        String unicode = UNICODES.get(parent.getCoreSymbol().getAtomFigure().getFigure());
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + parent.getCoreSymbol().getAtomFigure().getFigure());
        }
        return unicode;
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }
}
