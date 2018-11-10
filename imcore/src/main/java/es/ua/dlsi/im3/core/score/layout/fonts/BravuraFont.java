package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

import java.io.InputStream;

//TODO Actualizar a 1.204
public class BravuraFont extends LayoutFont {
    //private static final String SVGFONT = "/fonts/bravura/bravura_1.204.svg";
    //private static final String METADATA = "/fonts/bravura/bravura_metadata_1.204.json";

    private static final String OTFMUSICFONT = "/fonts/bravura/bravura_1.12.otf";
    //private static final String OTFTEXTCFONT = "/fonts/bravura/BravuraText_1.12.otf"; // this does not contain text
    private static final String OTFTEXTCFONT = "/fonts/bravura/montserrat-thin.otf";
    private static final String SVGFONT = "/fonts/bravura/bravura_1.12.svg";
    private static final String METADATA = "/fonts/bravura/glyphnames-1.12.json";
    public BravuraFont() throws ImportException, IM3Exception {
        super("Bravura", LayoutFonts.bravura,
                BravuraFont.class.getResourceAsStream(SVGFONT),
                BravuraFont.class.getResourceAsStream(OTFMUSICFONT),
                OTFMUSICFONT,
                BravuraFont.class.getResourceAsStream(OTFTEXTCFONT),
                BravuraFont.class.getResourceAsStream(METADATA),
                new SMuFLMap());
    }

    @Override
    public boolean isValidForNotationType(NotationType notationType) {
        return true;
    }
}
