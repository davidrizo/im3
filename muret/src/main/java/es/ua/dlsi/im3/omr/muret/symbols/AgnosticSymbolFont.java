package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It returns the font symbol associated to an agnostic symbol depending on the notation type
 * @autor drizo
 */
public abstract class AgnosticSymbolFont {
    /**
     * Key is agnostic string, avoid hashCode just in case. We want the order to be maintained as we add symbols
     */
    private LinkedHashMap<String, Glyph> glyphs;
    /**
     * Key is agnostic string (aligned to glyphs map)
     */
    private HashMap<String, AgnosticSymbolType> agnosticSymbolTypes;

    private LayoutFont layoutFont;

    private Font iconsFont;

    public AgnosticSymbolFont(LayoutFont layoutFont) {
        this.layoutFont = layoutFont;
        glyphs = new LinkedHashMap<>();
        agnosticSymbolTypes = new HashMap<>();
        iconsFont = layoutFont.getJavaFXMusicFont(20);
    }

    protected void add(AgnosticSymbolType agnosticSymbolType, String codepoint) {
        try {
            Glyph glyph = layoutFont.getGlyph(codepoint);
            glyphs.put(agnosticSymbolType.toAgnosticString(), glyph);
            agnosticSymbolTypes.put(agnosticSymbolType.toAgnosticString(), agnosticSymbolType);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find a glyph for agnostic symbol '{0}'", agnosticSymbolType.toAgnosticString());
        }
    }

    public Text createFontBasedText(String agnosticString) throws IM3Exception {
        Glyph glyph = glyphs.get(agnosticString);
        Text text;
        if (glyph == null) {
            text = new Text(agnosticString); // without music font
        } else {
            String unicode = glyph.getUnicode();
            text = new Text();
            //Font font = Font.loadFont(layoutFont.getOtfMusicFontResource(), 15);
            text.setFont(iconsFont);
            //text.setFont(Font.loadFont(layoutFont.getOtfMusicFontResource(), 30));

            text.setText(unicode);
            text.setStrokeType(StrokeType.INSIDE);
            text.setStrokeWidth(0);

        }
        return text;

    }
    public Shape createShape(String agnosticString) throws IM3Exception {
        Glyph glyph = glyphs.get(agnosticString);
        Shape shape = null;
        if (glyph == null) {
            shape = new Text(agnosticString);
        } else {
            SVGPath path = new SVGPath();
            path.setContent(glyph.getPath());
            path.getTransforms().add(layoutFont.getJavaFXScale());
            shape = path;
        }

        return shape;

    }
    public Shape createShape(AgnosticSymbolType agnosticSymbolType) throws IM3Exception {
        return createShape(agnosticSymbolType.toAgnosticString());
    }

    public LayoutFont getLayoutFont() {
        return layoutFont;
    }

    /**
     *
     * @return HaspMap where key is the agnostic string and glyph the information for creating the shape
     */
    public HashMap<String, Glyph> getGlyphs() {
        return glyphs;
    }

    public AgnosticSymbolType getAgnosticSymbolType(String agnosticString) {
        return agnosticSymbolTypes.get(agnosticString);
    }
}
