package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.HashMap;

public class CapitanMap implements IFontMap {
    public static final String NOTE_HEAD_WIDTH_CODEPOINT = "noteheadBlack";

    private static final HashMap<Figures, String> UNICODES = new HashMap<>();
    {
        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES.put(Figures.SEMIBREVE, "mensuralWhiteSemibrevis");
        UNICODES.put(Figures.MINIM, "mensuralWhiteMinima");
        //UNICODES.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES.put(Figures.SEMIMINIM, "mensuralBlackMinima"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES.put(Figures.FUSA, "mensuralBlackSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES.put(Figures.SEMIFUSA, "mensuralBlackFusa"); //TODO Ver esto - ¿igual en proporción ternaria?
        //mensuralBlackFusa
        //mensuralBlackSemiminima
    }

    private static final HashMap<Figures, String> UNICODES_WITHOUT_FLAG = new HashMap<>();
    {
        //TODO Mensural
        // TODO: 21/9/17 Para Mensural se debe saber si es blanca o ennegrecida
        UNICODES_WITHOUT_FLAG.put(Figures.SEMIBREVE, "mensuralWhiteSemibrevis");
        UNICODES_WITHOUT_FLAG.put(Figures.MINIM, "mensuralWhiteMinima");
        //UNICODES.put(Figures.SEMIMINIM, "mensuralWhiteSemiminima"); //TODO Ver esto - ¿igual en proporción ternaria?
        UNICODES_WITHOUT_FLAG.put(Figures.SEMIMINIM, "mensuralBlackMinima"); //TODO Ver esto - ¿igual en proporción ternaria?
        // TODO: 26/9/17  IM3 - debemos tener distintas versiones de glifos - cojo las duraciones del sXVII - https://en.wikipedia.org/wiki/Mensural_notation
        UNICODES_WITHOUT_FLAG.put(Figures.FUSA, "mensuralBlackMinima"); //TODO Ver esto - ¿igual en proporción ternaria?
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
    public String getUnicodeWihoutFlag(Figures figure, boolean stemUp) throws IM3Exception {
        String result = UNICODES_WITHOUT_FLAG.get(figure);
        if (result == null) {
            throw new IM3Exception("Cannot find an unicode (without flag) for " + figure);
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
        return "custos";
    }
}
