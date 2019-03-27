package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class AgnosticModernPrintedFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/BravuraModernMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/bravura/montserrat-thin.otf";
    private static final String SVGFONT = "/fonts/agnostic/BravuraModernMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_modern.json";

    public AgnosticModernPrintedFont() throws IM3Exception {
        super("BravuraModernMuRET", LayoutFonts.agnosticModernPrinted, AgnosticModernPrintedFont.class.getResourceAsStream(SVGFONT),
                AgnosticModernPrintedFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                AgnosticModernPrintedFont.class.getResourceAsStream(OTFTEXTCFONT),
                AgnosticModernPrintedFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
