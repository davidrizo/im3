package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.JSONGlyphNamesReader;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.layout.fonts.IFontMap;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
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
import java.util.HashMap;

public abstract class LayoutFont {
    /**
     * As field to be reused
     */
    private final Scale javaFXScale;
    private final LayoutFonts font;
    private final String name;
    private final String otfMusicFontResourcePath;
    OpenTypeFont otfMusicFont;
    OpenTypeFont otfTextFont;
    SVGFont svgFont;
    //TODO Font para PDF
    JSONGlyphNamesReader mapping;
    Font javaFXTextFont;
    Text javaFXTextForSizes;
    double textHeightInPixels;
    IFontMap fontMap;

    //TODO No tiene mucho sentido pasar el path y también el input stream
    /**
     * @param otfMusicFontResource Typically a file with the font
     * @param otfMusicFontResourcePath Typically a file path (using class.getReource) with the font (used for JavaFX, usually the same as otfMusicFontResource)
     * @param otfTextFontResource Typically a file with the font
     * @param svgFontResource Typically a file with the font
     * @param mappingResource Typically a file with the mapping (usually SMuFL)
     */
    public LayoutFont(String name, LayoutFonts font, InputStream svgFontResource, InputStream otfMusicFontResource, String otfMusicFontResourcePath, InputStream otfTextFontResource, InputStream mappingResource, IFontMap fontMap) throws IM3Exception {
        this.name = name;
        this.font = font;
        this.fontMap = fontMap;
        if (otfMusicFontResource == null) {
            throw new ImportException("Music font resource stream is null");
        }
        if (otfTextFontResource == null) {
            throw new ImportException("Text font resource stream is null");
        }
        if (svgFontResource == null) {
            throw new ImportException("Music font svg resource stream is null");
        }

        SVGFontImporter importer = new SVGFontImporter(name);
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
        //TODO ¿Está bien?
        javaFXTextFont = Font.loadFont(otfTextFontResource, LayoutConstants.TEXT_FONT_SIZE);
        javaFXTextForSizes = new Text(0, 0, "X");
        javaFXTextForSizes.setFont(javaFXTextFont);
        textHeightInPixels = javaFXTextForSizes.getBoundsInLocal().getHeight();

        this.otfMusicFontResourcePath = otfMusicFontResourcePath;
    }

    public Glyph getGlyph(String codepoint) throws IM3Exception {
        String unicode = mapping.getCodepoint(codepoint);
        return svgFont.getGlyph(unicode);
    }

    public String getDefaultPositionInStaff(String codepoint) throws IM3Exception {
        return (String) mapping.getPropertyValue(codepoint, "defaultPositionInStaff");
    }

    /**
     * It returns a table with all values from json file: key=codepoint ("U+E050"), value=glyphname ("gClef")
     * @return
     * @throws IM3Exception
     */
    public HashMap<String, String> getCodepointGlyphMap() {
        return mapping.readCodepointToOrderedGlyphMap();
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

    public String getName() {
        return name;
    }

    public Font getJavaFXMusicFont(int size) {
        return Font.loadFont(this.getClass().getResourceAsStream(otfMusicFontResourcePath), size);
    }

    public abstract boolean isValidForNotationType(NotationType notationType);
}
