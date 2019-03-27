package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

public class AgnosticMensuralHandwrittenFont extends LayoutFont {

    private static final String OTFMUSICFONT = "/fonts/agnostic/PatriarcaMensuralMuRET-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/capitan/anduaga.otf";
    private static final String SVGFONT = "/fonts/agnostic/PatriarcaMensuralMuRET-Regular.svg";
    private static final String METADATA = "/fonts/agnostic/muret_mensural.json";

    public AgnosticMensuralHandwrittenFont() throws IM3Exception {
        super("PatriarcaMensuralMuRET", LayoutFonts.agnosticMensuralHandwritten, AgnosticMensuralHandwrittenFont.class.getResourceAsStream(SVGFONT),
                AgnosticMensuralHandwrittenFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                AgnosticMensuralHandwrittenFont.class.getResourceAsStream(OTFTEXTCFONT),
                AgnosticMensuralHandwrittenFont.class.getResourceAsStream(METADATA),
                null);
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return notationType == NotationType.eMensural;
    }
}
