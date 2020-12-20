package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class ModernPrintedFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/BravuraModernMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/bravura/montserrat-thin.otf";
    private static final String SVGFONT = "/fonts/agnostic/BravuraModernMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_modern.json";

    public ModernPrintedFont() throws IM3Exception {
        super("BravuraModernMuRET", LayoutFonts.modernPrinted, ModernPrintedFont.class.getResourceAsStream(SVGFONT),
                ModernPrintedFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                ModernPrintedFont.class.getResourceAsStream(OTFTEXTCFONT),
                ModernPrintedFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
