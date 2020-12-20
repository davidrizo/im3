package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class MensuralPrintedFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/BravuraMensuralMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/bravura/montserrat-thin.otf";
    private static final String SVGFONT = "/fonts/agnostic/BravuraMensuralMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_mensural.json";

    public MensuralPrintedFont() throws IM3Exception {
        super("BravuraMensuralMuRET", LayoutFonts.mensuralPrinted, MensuralPrintedFont.class.getResourceAsStream(SVGFONT),
                MensuralPrintedFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                MensuralPrintedFont.class.getResourceAsStream(OTFTEXTCFONT),
                MensuralPrintedFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
