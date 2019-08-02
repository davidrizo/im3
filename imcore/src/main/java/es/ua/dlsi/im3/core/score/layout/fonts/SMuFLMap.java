package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
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

    /**
     * It includes mensural white notes
     */
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
        UNICODES.put(Figures.MAXIMA, "mensuralNoteheadMaximaWhite");
        UNICODES.put(Figures.LONGA, "mensuralNoteheadLongaWhite");
        UNICODES.put(Figures.BREVE, "mensuralNoteheadLongaWhite"); // it is correct (SMuFL: Longa/brevis notehead, white)
        UNICODES.put(Figures.SEMIBREVE, "mensuralNoteheadSemibrevisVoid");
        UNICODES.put(Figures.MINIM, "mensuralNoteheadMinimaWhite");
        UNICODES.put(Figures.SEMIMINIM, "mensuralNoteheadSemiminimaWhite");
        UNICODES.put(Figures.FUSA, "mensuralNoteheadSemiminimaWhite"); // correct: Semiminima/fusa notehead, white

    }

    private static final HashMap<Figures, String> UNICODES_MENSURAL_COLORED = new HashMap<>();
    {
        UNICODES_MENSURAL_COLORED.put(Figures.LONGA, "mensuralNoteheadLongaBlack");
        UNICODES_MENSURAL_COLORED.put(Figures.BREVE, "mensuralNoteheadLongaBlack"); // it is correct (SMuFL: Longa/brevis notehead, black)
        UNICODES_MENSURAL_COLORED.put(Figures.SEMIBREVE, "mensuralNoteheadSemibrevisBlack");
        UNICODES_MENSURAL_COLORED.put(Figures.MINIM, "mensuralNoteheadSemiminimaWhite");
    }

    private static final HashMap<Figures, String> UNICODES_FLAG_STEM_UP = new HashMap<>();
    {
        UNICODES_FLAG_STEM_UP.put(Figures.EIGHTH, "flag8thUp");
        UNICODES_FLAG_STEM_UP.put(Figures.SIXTEENTH, "flag16thUp");
        UNICODES_FLAG_STEM_UP.put(Figures.THIRTY_SECOND, "flag32ndUp");
        UNICODES_FLAG_STEM_UP.put(Figures.SIXTY_FOURTH, "flag64thUp");
        UNICODES_FLAG_STEM_UP.put(Figures.HUNDRED_TWENTY_EIGHTH, "flag128thUp");
        UNICODES_FLAG_STEM_UP.put(Figures.TWO_HUNDRED_FIFTY_SIX, "flag256thUp");
        // TODO Existen hasta la 1024th
    }

    private static final HashMap<Figures, String> UNICODES_FLAG_STEM_DOWN = new HashMap<>();
    {
        UNICODES_FLAG_STEM_DOWN.put(Figures.EIGHTH, "flag8thDown");
        UNICODES_FLAG_STEM_DOWN.put(Figures.SIXTEENTH, "flag16thDown");
        UNICODES_FLAG_STEM_DOWN.put(Figures.THIRTY_SECOND, "flag32ndDown");
        UNICODES_FLAG_STEM_DOWN.put(Figures.SIXTY_FOURTH, "flag64thDown");
        UNICODES_FLAG_STEM_DOWN.put(Figures.HUNDRED_TWENTY_EIGHTH, "flag128thDown");
        UNICODES_FLAG_STEM_DOWN.put(Figures.TWO_HUNDRED_FIFTY_SIX, "flag256thDown");
    }

    private static final HashMap<Figures, String> UNICODES_STEM_AND_FLAG_UP = new HashMap<>();
    {
        UNICODES_STEM_AND_FLAG_UP.put(Figures.MINIM, "mensuralCombStemUp");
        //UNICODES_STEM_AND_FLAG_UP.put(Figures.SEMIMINIM, "mensuralCombStemUpFlagSemiminima"); // we don't use it, it should have void head
        UNICODES_STEM_AND_FLAG_UP.put(Figures.SEMIMINIM, "mensuralCombStemDown");
        //UNICODES_STEM_AND_FLAG_UP.put(Figures.FUSA, "mensuralCombStemUpFlagFusa"); // used in black mensural
        UNICODES_STEM_AND_FLAG_UP.put(Figures.FUSA, "mensuralCombStemUpFlagSemiminima");
    }


    private static final HashMap<Figures, String> UNICODES_STEM_AND_FLAG_DOWN = new HashMap<>();
    {
        UNICODES_STEM_AND_FLAG_DOWN.put(Figures.MINIM, "mensuralCombStemDown");
        //UNICODES_STEM_AND_FLAG_DOWN.put(Figures.SEMIMINIM, "mensuralCombStemDownFlagSemiminima"); // we don't use it, it should have void head
        UNICODES_STEM_AND_FLAG_DOWN.put(Figures.SEMIMINIM, "mensuralCombStemUp");
        //UNICODES_STEM_AND_FLAG_DOWN.put(Figures.FUSA, "mensuralCombStemDownFlagFusa"); // used in black mensural
        UNICODES_STEM_AND_FLAG_DOWN.put(Figures.FUSA, "mensuralCombStemDownFlagSemiminima");
    }

    @Override
    public String getUnicode(Figures figure, boolean stemUp, boolean colored) throws IM3Exception {
        if (colored) {
            String result = UNICODES_MENSURAL_COLORED.get(figure);
            if (result == null) {
                throw new IM3Exception("Cannot find an unicode for " + figure + " colored");
            }
            return result;
        } else {
            String result = UNICODES.get(figure);
            if (result == null) {
                throw new IM3Exception("Cannot find an unicode for " + figure);
            }

            return result;
        }
    }

    @Override
    public String getUnicodeWihoutFlag(Figures figures, boolean stemUp, boolean colored) throws IM3Exception {
        return getUnicode(figures, stemUp, colored); //TODO En mensural no será igual
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

    @Override
    public String getUnicodeFlag(Figures figures, boolean stemUp) throws IM3Exception {
        HashMap<Figures, String> unicodes;

        if (stemUp) {
            unicodes = UNICODES_FLAG_STEM_UP;
        } else {
            unicodes = UNICODES_FLAG_STEM_DOWN;
        }

        String result = unicodes.get(figures);
        if (result == null) {
            throw new IM3Exception("Cannot find an unicode for flag for " + figures + " and stem up?" + stemUp);
        }
        return result;
    }

    @Override
    public String getUnicodeStemAndFlag(Figures figures, boolean stemUp) throws IM3Exception {
        HashMap<Figures, String> unicodes;

        if (stemUp) {
            unicodes = UNICODES_STEM_AND_FLAG_UP;
        } else {
            unicodes = UNICODES_STEM_AND_FLAG_DOWN;
        }

        String result = unicodes.get(figures);
        if (result == null) {
            throw new IM3Exception("Cannot find an unicode for combined flag and stem for " + figures + " and stem up?" + stemUp);
        }
        return result;
    }
}
