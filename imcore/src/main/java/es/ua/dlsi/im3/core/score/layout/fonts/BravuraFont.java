package es.ua.dlsi.im3.core.score.layout.fonts;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

import java.io.InputStream;

//TODO Actualizar a 1.204
public class BravuraFont extends LayoutFont {
    //private static final String SVGFONT = "/fonts/bravura/bravura_1.204.svg"; //TODO poder cambiar la fuente, que no tenga por qué ser bravura
    //private static final String METADATA = "/fonts/bravura/bravura_metadata_1.204.json";

    private static final String OTFMUSICFONT = "/fonts/bravura/bravura_1.12.otf"; //TODO poder cambiar la fuente, que no tenga por qué ser bravura
    private static final String OTFTEXTCFONT = "/fonts/bravura/BravuraText_1.12.otf"; //TODO poder cambiar la fuente, que no tenga por qué ser bravura
    private static final String SVGFONT = "/fonts/bravura/bravura_1.12.svg"; //TODO poder cambiar la fuente, que no tenga por qué ser bravura
    private static final String METADATA = "/fonts/bravura/glyphnames-1.12.json";

    public BravuraFont() throws ImportException, IM3Exception {
        super(BravuraFont.class.getResourceAsStream(SVGFONT),
                BravuraFont.class.getResourceAsStream(OTFMUSICFONT),
                BravuraFont.class.getResourceAsStream(OTFTEXTCFONT),
                BravuraFont.class.getResourceAsStream(METADATA));
    }
}
