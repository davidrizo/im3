package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class MensuralHandwrittenFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/PatriarcaMensuralMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/capitan/anduaga.otf";
    private static final String SVGFONT = "/fonts/agnostic/PatriarcaMensuralMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_mensural.json";

    public MensuralHandwrittenFont() throws IM3Exception {
        super("PatriarcaMensuralMuRET", LayoutFonts.mensuralHandwritten, MensuralHandwrittenFont.class.getResourceAsStream(SVGFONT),
                MensuralHandwrittenFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                MensuralHandwrittenFont.class.getResourceAsStream(OTFTEXTCFONT),
                MensuralHandwrittenFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
