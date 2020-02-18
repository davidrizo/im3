package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.HashMap;

public class PatriarcaMap implements IFontMap {
    public static final String NOTE_HEAD_WIDTH_CODEPOINT = "mensuralWhiteMinima";
    public static final String CUSTOS = "mensuralCustosUp";

    private static final HashMap<Figures, String> UNICODES_WITHOUT_STEM = new HashMap<>();
    {
        UNICODES_WITHOUT_STEM.put(Figures.MAXIMA, "mensuralWhiteMaxima");
        UNICODES_WITHOUT_STEM.put(Figures.LONGA, "mensuralWhiteLonga");
        UNICODES_WITHOUT_STEM.put(Figures.BREVE, "mensuralWhiteBrevis");
        UNICODES_WITHOUT_STEM.put(Figures.SEMIBREVE, "mensuralBlackSemibrevisVoid");
    }

    private static final HashMap<Figures, String> UNICODES_STEM_UP = new HashMap<>();
    {
        UNICODES_STEM_UP.put(Figures.LONGA, "mensuralWhiteLonga"); //TODO ¿ésta?
        UNICODES_STEM_UP.put(Figures.MINIM, "mensuralWhiteMinima");
        UNICODES_STEM_UP.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES_STEM_UP.put(Figures.FUSA, "mensuralWhiteFusa"); //TODO Ver esto - ¿igual en proporción ternaria?

        //YAAAAA UNICODES_STEM_UP.put(Figures.SEMIFUSA, "mensuralWhiteSemifusa"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES_STEM_UP.put(Figures.SEMIFUSA, "mensuralWhiteSemifusa"); //TODO Ver esto - ¿igual en proporción ternaria?
        //mensuralBlackFusa
        //mensuralBlackSemiminima
    }

    private static final HashMap<Figures, String> UNICODES_STEM_DOWN = new HashMap<>();
    {
        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES_STEM_DOWN.put(Figures.LONGA, "mensuralWhiteLonga"); //TODO ¿ésta?
        UNICODES_STEM_DOWN.put(Figures.BREVE, "mensuralBlackSemibrevisVoid"); //TODO
        UNICODES_STEM_DOWN.put(Figures.SEMIBREVE, "mensuralBlackSemibrevisVoid");
        UNICODES_STEM_DOWN.put(Figures.MINIM, "mensuralWhiteMinimaDown");
        UNICODES_STEM_DOWN.put(Figures.SEMIMINIM, "mensuralWhiteSemiminimaDown"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES_STEM_DOWN.put(Figures.FUSA, "mensuralWhiteFusaDown"); //TODO Ver esto - ¿igual en proporción ternaria?

        //YAAAAA UNICODES_STEM_UP.put(Figures.SEMIFUSA, "mensuralWhiteSemifusa"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES_STEM_DOWN.put(Figures.SEMIFUSA, "mensuralWhiteSemifusaDown"); //TODO Ver esto - ¿igual en proporción ternaria?
        //mensuralBlackFusa
        //mensuralBlackSemiminima
    }

    private static final HashMap<Figures, String> UNICODES_WITHOUT_FLAG_STEM_UP = new HashMap<>();
    {
        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES_WITHOUT_FLAG_STEM_UP.put(Figures.SEMIBREVE, "mensuralBlackSemibrevisVoid");
        UNICODES_WITHOUT_FLAG_STEM_UP.put(Figures.MINIM, "mensuralWhiteMinima");
        //UNICODES_STEM_UP.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES_WITHOUT_FLAG_STEM_UP.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES_WITHOUT_FLAG_STEM_UP.put(Figures.FUSA, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
    }

    private static final HashMap<Figures, String> UNICODES_WITHOUT_FLAG_STEM_DOWN = new HashMap<>();
    {
        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES_WITHOUT_FLAG_STEM_DOWN.put(Figures.SEMIBREVE, "mensuralBlackSemibrevisVoid");
        UNICODES_WITHOUT_FLAG_STEM_DOWN.put(Figures.MINIM, "mensuralWhiteMinimaDown");
        //UNICODES_STEM_UP.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES_WITHOUT_FLAG_STEM_DOWN.put(Figures.SEMIMINIM, "mensuralWhiteSemiminimaDown"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES_WITHOUT_FLAG_STEM_DOWN.put(Figures.FUSA, "mensuralWhiteSemiminimaDown"); //TODO Ver esto - ¿igual en proporción ternaria?
    }


    @Override
    public String getUnicode(Figures figure, boolean stemUp, boolean colored) throws IM3Exception {
        HashMap<Figures, String> unicodes;

        if (figure.usesStem()) {
            if (stemUp) {
                unicodes = UNICODES_STEM_UP;
            } else {
                unicodes = UNICODES_STEM_DOWN;
            }
        } else {
            unicodes = UNICODES_WITHOUT_STEM;
        }

        String result = unicodes.get(figure);
        if (result == null) {
            if (figure.usesStem()) {
                throw new IM3Exception("Cannot find an unicode for " + figure + " and stemUp = " + stemUp);
            } else {
                throw new IM3Exception("Cannot find an unicode for " + figure + " without stem");
            }

        }

        return result;
    }

    @Override
    public String getUnicodeWihoutFlag(Figures figure, boolean stemUp, boolean colored) throws IM3Exception {
        HashMap<Figures, String> unicodes;

        if (stemUp) {
            unicodes = UNICODES_WITHOUT_FLAG_STEM_UP;
        } else {
            unicodes = UNICODES_WITHOUT_FLAG_STEM_DOWN;
        }

        String result = unicodes.get(figure);
        if (result == null) {
            throw new IM3Exception("Cannot find an unicode (without flag) for " + figure + " and stemUp = " + stemUp);
        }

        return result;
    }

    @Override
    public String getUnicodeNoteHeadWidth() {
        return NOTE_HEAD_WIDTH_CODEPOINT;
    }

    @Override
    public boolean isGlyphIncludeStemAndFlag(Figures figures) {
        return true;
    }

    @Override
    public GraphicsElement createBeam(NotationSymbol notationSymbol, Coordinate fromPosition, Coordinate toPosition) {
        Line line = new Line(notationSymbol, InteractionElementType.beam, fromPosition, toPosition);
        line.setThickness(3); //TODO Una pequeña curva
        return line;
    }

    @Override
    public String getCustosCodePoint() {
        return CUSTOS;
    }

    @Override
    public String getUnicodeFlag(Figures figures, boolean stemUp)  {
        if (stemUp) {
            return UNICODES_WITHOUT_FLAG_STEM_UP.get(figures);
        } else {
            return UNICODES_WITHOUT_FLAG_STEM_DOWN.get(figures);
        }
    }

    @Override
    public String getUnicodeStemAndFlag(Figures figures, boolean stemUp)  {
        if (stemUp) {
            return UNICODES_STEM_UP.get(figures);
        } else {
            return UNICODES_STEM_DOWN.get(figures);
        }
    }
}
