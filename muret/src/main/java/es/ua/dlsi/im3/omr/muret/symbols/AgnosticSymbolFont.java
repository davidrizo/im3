package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It returns the font symbol associated to an agnostic symbol depending on the notation type
 * @autor drizo
 */
public abstract class AgnosticSymbolFont {
    /**
     * Key is agnostic string, avoid hashCode just in case
     */
    private HashMap<String, Glyph> glyphs;

    private LayoutFont layoutFont;

    public AgnosticSymbolFont(LayoutFont layoutFont) {
        this.layoutFont = layoutFont;
        glyphs = new HashMap<>();
    }

    protected void add(AgnosticSymbolType agnosticSymbolType, String codepoint) {
        try {
            Glyph glyph = layoutFont.getGlyph(codepoint);
            glyphs.put(agnosticSymbolType.toAgnosticString(), glyph);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find a glyph for agnostic symbol '{0}'", agnosticSymbolType.toAgnosticString());
        }
    }

    public Shape createShape(AgnosticSymbolType agnosticSymbolType) throws IM3Exception {
        Glyph glyph = glyphs.get(agnosticSymbolType.toAgnosticString());
        Shape shape = null;
        if (glyph == null) {
            shape = new Text(agnosticSymbolType.toAgnosticString());
        } else {
            SVGPath path = new SVGPath();
            path.setContent(glyph.getPath());
            path.getTransforms().add(layoutFont.getJavaFXScale());
            shape = path;
        }

        return shape;
    }

    public LayoutFont getLayoutFont() {
        return layoutFont;
    }
}
