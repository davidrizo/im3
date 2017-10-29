package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class Flag extends Component<LayoutCoreSingleFigureAtom> {
    private static final HashMap<Figures, String> UNICODES_STEM_UP = new HashMap<>();
    {
        UNICODES_STEM_UP.put(Figures.EIGHTH, "flag8thUp");
        UNICODES_STEM_UP.put(Figures.SIXTEENTH, "flag16thUp");
        UNICODES_STEM_UP.put(Figures.THIRTY_SECOND, "flag32ndUp");
        UNICODES_STEM_UP.put(Figures.SIXTY_FOURTH, "flag64thUp");
        UNICODES_STEM_UP.put(Figures.HUNDRED_TWENTY_EIGHTH, "flag128thUp");
        UNICODES_STEM_UP.put(Figures.TWO_HUNDRED_FIFTY_SIX, "flag256thUp");
        // TODO Existen hasta la 1024th
    }
    private static final HashMap<Figures, String> UNICODES_STEM_DOWN = new HashMap<>();
    {
        UNICODES_STEM_DOWN.put(Figures.EIGHTH, "flag8thDown");
        UNICODES_STEM_DOWN.put(Figures.SIXTEENTH, "flag16thDown");
        UNICODES_STEM_DOWN.put(Figures.THIRTY_SECOND, "flag32ndDown");
        UNICODES_STEM_DOWN.put(Figures.SIXTY_FOURTH, "flag64thDown");
        UNICODES_STEM_DOWN.put(Figures.HUNDRED_TWENTY_EIGHTH, "flag128thDown");
        UNICODES_STEM_DOWN.put(Figures.TWO_HUNDRED_FIFTY_SIX, "flag256thDown");
        // TODO Existen hasta la 1024th
    }

    Pictogram pictogram;

    public Flag(LayoutFont layoutFont, LayoutCoreSingleFigureAtom parent, Figures figure, Coordinate position, boolean stemUp) throws IM3Exception {
        super(null, parent, position);

        pictogram = new Pictogram("FLAG", layoutFont, getUnicode(stemUp, figure), position);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

    private String getUnicode(boolean stemUp, Figures figure) throws IM3Exception {
        HashMap<Figures, String> map = stemUp?UNICODES_STEM_UP:UNICODES_STEM_DOWN;
        String unicode = map.get(figure);
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + figure + " and stem up?" + stemUp);
        }
        return unicode;
    }
}
