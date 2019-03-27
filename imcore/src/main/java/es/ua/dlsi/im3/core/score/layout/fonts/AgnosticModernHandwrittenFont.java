package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class AgnosticModernHandwrittenFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/PetalumaModernMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/capitan/anduaga.otf";
    private static final String SVGFONT = "/fonts/agnostic/PetalumaModernMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_modern.json";

    public AgnosticModernHandwrittenFont() throws IM3Exception {
        super("PetalumaModernMuRET", LayoutFonts.agnosticModernHandwritten, AgnosticModernHandwrittenFont.class.getResourceAsStream(SVGFONT),
                AgnosticModernHandwrittenFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                AgnosticModernHandwrittenFont.class.getResourceAsStream(OTFTEXTCFONT),
                AgnosticModernHandwrittenFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
