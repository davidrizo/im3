package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.awt.*;
import java.util.HashMap;

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
    public String getUnicodeWihoutFlag(Figures figures) throws IM3Exception {
        return getUnicode(figures, false); //TODO En mensural no será igual
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
    public GraphicsElement createBeam(String ID, Coordinate fromPosition, Coordinate toPosition) {
        Line line = new Line(ID, fromPosition, toPosition);
        return line;
    }

    @Override
    public String getCustosCodePoint() {
        return "mensuralCustosUp";
    }
}
