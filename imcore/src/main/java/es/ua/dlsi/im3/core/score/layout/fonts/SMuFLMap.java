package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.Polygon;
import es.ua.dlsi.im3.core.score.layout.graphics.StrokeType;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class SMuFLMap implements IFontMap {
    public static final String NOTE_HEAD_WIDTH_CODEPOINT = "noteheadBlack";

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

        // TODO: 2/10/17 Mensural - cuidado con getUnicodeWihoutFlag
    }

    @Override
    public String getUnicode(Figures figure, boolean stemUp) throws IM3Exception {
        String result = UNICODES.get(figure);
        if (result == null) {
            throw new IM3Exception("Cannot find an unicode for " + figure);
        }

        return result;
    }

    @Override
    public String getUnicodeWihoutFlag(Figures figures, boolean stemUp) throws IM3Exception {
        return getUnicode(figures, stemUp); //TODO En mensural no será igual
    }

    @Override
    public String getUnicodeNoteHeadWidth() {
        return NOTE_HEAD_WIDTH_CODEPOINT;
    }

    @Override
    public boolean isGlyphIncludeStemAndFlag(Figures figures) {
        return false;
    }

    @Override
    public GraphicsElement createBeam(NotationSymbol notationSymbol, Coordinate fromPosition, Coordinate toPosition) {
        Coordinate p0 = new Coordinate(fromPosition.getX(), fromPosition.getY());
        Coordinate p1 = new Coordinate(fromPosition.getX(), new CoordinateComponent(fromPosition.getY(), LayoutConstants.BEAM_THICKNESS));
        Coordinate p2 = new Coordinate(toPosition.getX(), new CoordinateComponent(toPosition.getY(), LayoutConstants.BEAM_THICKNESS));
        Coordinate p3 = new Coordinate(toPosition.getX(), toPosition.getY());

        LinkedList<Coordinate> points = new LinkedList<>();
        points.add(p0);
        points.add(p1);
        points.add(p2);
        points.add(p3);

        Polygon polygon = new Polygon(notationSymbol, InteractionElementType.beam, points, 0, StrokeType.eSolid, LayoutConstants.BEAM_COLOR);
        //Line line = new Line(notationSymbol, InteractionElementType.beam, fromPosition, toPosition);
        return polygon;
    }

    @Override
    public String getCustosCodePoint() {
        return "mensuralCustosUp";
    }
}
