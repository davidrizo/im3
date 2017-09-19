package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.JSONGlyphNamesReader;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFont;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFontImporter;
import javafx.scene.transform.Scale;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;

import java.io.IOException;
import java.io.InputStream;

public class LayoutFont {
    /**
     * As field to be reused
     */
    private final Scale javaFXScale;
    OpenTypeFont otfMusicFont;
    OpenTypeFont otfTextFont;
    SVGFont svgFont;
    //TODO Font para PDF
    JSONGlyphNamesReader mapping;

    /**
     * @param otfMusicFontResource Typically a file with the font
     * @param otfTextFontResource Typically a file with the font
     * @param svgFontResource Typically a file with the font
     * @param mappingResource Typically a file with the mapping (usually SMuFL)
     */
    public LayoutFont(InputStream svgFontResource, InputStream otfMusicFontResource, InputStream otfTextFontResource, InputStream mappingResource) throws ImportException, IM3Exception {
        SVGFontImporter importer = new SVGFontImporter();
        svgFont = importer.importStream(svgFontResource);
        mapping = new JSONGlyphNamesReader(mappingResource);
        this.mapping = mapping;

        OTFParser otfParser = new OTFParser(true);
        try {
            otfMusicFont = otfParser.parse(otfMusicFontResource);
            otfTextFont = otfParser.parse(otfTextFontResource);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        javaFXScale = new Scale(getScaleX(), getScaleY());
    }


    public Glyph getGlyph(Pictogram element) throws IM3Exception {
        String unicode = mapping.getCodepoint(element.getCodepoint());
        return svgFont.getGlyph(unicode);
    }

    public SVGFont getSVGFont() {
        return svgFont;
    }

    public OpenTypeFont getOTFMusicFont() {
        return otfMusicFont;
    }

    public OpenTypeFont getOtfTextFont() {
        return otfTextFont;
    }

    public Scale getJavaFXScale() {
        return javaFXScale;
    }
    /**
     * Scale to default EM size 
     * @return
     */
    public double getScaleX() {
        return LayoutConstants.EM / svgFont.getUnitsPerEM();        
    }

    /**
     * Scale to default EM size 
     * @return
     */
    public double getScaleY() {
        return -LayoutConstants.EM / svgFont.getUnitsPerEM(); // TODO: 17/9/17 El -1 es para Bravura sólo ?
    }
    
}