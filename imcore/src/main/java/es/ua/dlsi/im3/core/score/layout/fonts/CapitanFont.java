package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

//TODO Actualizar a 1.204
public class CapitanFont extends LayoutFont {
    //private static final String SVGFONT = "/fonts/bravura/bravura_1.204.svg";
    //private static final String METADATA = "/fonts/bravura/bravura_metadata_1.204.json";

    private static final String OTFMUSICFONT = "/fonts/capitan/capitan_0.1.otf";
    private static final String OTFTEXTCFONT = "/fonts/capitan/anduaga.otf";
    private static final String SVGFONT = "/fonts/capitan/capitan_0.1.svg";
    private static final String METADATA = "/fonts/capitan/capitan_0.1.json";

    public CapitanFont() throws ImportException, IM3Exception {
        super("Capitán", LayoutFonts.capitan, CapitanFont.class.getResourceAsStream(SVGFONT),
                CapitanFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                CapitanFont.class.getResourceAsStream(OTFTEXTCFONT),
                CapitanFont.class.getResourceAsStream(METADATA),
                new CapitanMap());
    }
}
