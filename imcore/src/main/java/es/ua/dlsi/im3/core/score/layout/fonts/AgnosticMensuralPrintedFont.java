package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class AgnosticMensuralPrintedFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/BravuraMensuralMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/bravura/montserrat-thin.otf";
    private static final String SVGFONT = "/fonts/agnostic/BravuraMensuralMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_mensural.json";

    public AgnosticMensuralPrintedFont() throws IM3Exception {
        super("BravuraMensuralMuRET", LayoutFonts.agnosticMensuralPrinted, AgnosticMensuralPrintedFont.class.getResourceAsStream(SVGFONT),
                AgnosticMensuralPrintedFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                AgnosticMensuralPrintedFont.class.getResourceAsStream(OTFTEXTCFONT),
                AgnosticMensuralPrintedFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
