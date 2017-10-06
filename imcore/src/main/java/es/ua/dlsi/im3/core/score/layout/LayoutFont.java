package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.JSONGlyphNamesReader;
import es.ua.dlsi.im3.core.score.layout.fonts.IFontMap;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFont;
import es.ua.dlsi.im3.core.score.layout.svg.SVGFontImporter;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    private final LayoutFonts font;
    OpenTypeFont otfMusicFont;
    OpenTypeFont otfTextFont;
    SVGFont svgFont;
    //TODO Font para PDF
    JSONGlyphNamesReader mapping;
    Font javaFXTextFont;
    Text javaFXTextForSizes;
    double textHeightInPixels;
    IFontMap fontMap;

    /**
     * @param otfMusicFontResource Typically a file with the font
     * @param otfTextFontResource Typically a file with the font
     * @param svgFontResource Typically a file with the font
     * @param mappingResource Typically a file with the mapping (usually SMuFL)
     */
    public LayoutFont(LayoutFonts font, InputStream svgFontResource, InputStream otfMusicFontResource, InputStream otfTextFontResource, InputStream mappingResource, IFontMap fontMap) throws ImportException, IM3Exception {
        this.font = font;
        this.fontMap = fontMap;
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
        javaFXTextFont = Font.loadFont(otfMusicFontResource, LayoutConstants.TEXT_FONT_SIZE);
        javaFXTextForSizes = new Text(0, 0, "X");
        javaFXTextForSizes.setFont(javaFXTextFont);
        textHeightInPixels = javaFXTextForSizes.getBoundsInLocal().getHeight();

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

    public Font getJavaFXTextFont() {
        return javaFXTextFont;
    }

    public double getTextHeightInPixels() {
        return textHeightInPixels;
    }

    public IFontMap getFontMap() {
        return fontMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayoutFont that = (LayoutFont) o;

        return font == that.font;
    }

    @Override
    public int hashCode() {
        return font.hashCode();
    }

    public LayoutFonts getFont() {
        return font;
    }
}
