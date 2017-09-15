package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.JSONGlyphNamesReader;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFont;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFontImporter;

import java.io.InputStream;

public class LayoutFont {
    SVGFont font;
    //TODO Font para PDF
    JSONGlyphNamesReader mapping;

    /**
     * @param svgFontResource Typically a file with the font
     * @param mappingResource Typically a file with the mapping (usually SMuFL)
     */
    public LayoutFont(InputStream svgFontResource, InputStream mappingResource) throws ImportException, IM3Exception {
        SVGFontImporter importer = new SVGFontImporter();
        font = importer.importStream(svgFontResource);
        mapping = new JSONGlyphNamesReader(mappingResource);
        this.font = font;
        this.mapping = mapping;
    }


    public Glyph getGlyph(Pictogram element) throws IM3Exception {
        String unicode = mapping.getCodepoint(element.getCodepoint());
        return font.getGlyph(unicode);
    }

    public SVGFont getSVGFont() {
        return font;
    }
}
