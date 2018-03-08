package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

//TODO Actualizar a 1.204
public class PatriarcaFont extends LayoutFont {
    //private static final String SVGFONT = "/fonts/bravura/bravura_1.204.svg";
    //private static final String METADATA = "/fonts/bravura/bravura_metadata_1.204.json";

    private static final String OTFMUSICFONT = "/fonts/patriarca/Patriarca-Regular.otf";
    private static final String OTFTEXTCFONT = "/fonts/capitan/anduaga.otf";
    private static final String SVGFONT = "/fonts/patriarca/Patriarca-Regular.svg";
    private static final String METADATA = "/fonts/patriarca/Patriarca-Regular.json";

    public PatriarcaFont() throws IM3Exception {
        super("Patriarca", LayoutFonts.patriarca, PatriarcaFont.class.getResourceAsStream(SVGFONT),
                PatriarcaFont.class.getResourceAsStream(OTFMUSICFONT),
                PatriarcaFont.class.getResourceAsStream(OTFTEXTCFONT),
                PatriarcaFont.class.getResourceAsStream(METADATA),
                new PatriarcaMap());
    }
}
