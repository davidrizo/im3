package es.ua.dlsi.im3.core.score.layout.svg;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;

/**
 * It loads the SVG fonts required by the layout subsystem
 */
public class SVGFont {
    HashMap<String, Glyph> glyphs;
    private Integer unitsPerEM;
    private Integer descent;
    private Integer ascent;


    public SVGFont() {
        glyphs = new HashMap<>();
    }


    public void add(Glyph glyph) {
        glyphs.put(glyph.getUnicode(), glyph);
    }

    public Integer getUnitsPerEM() {
        return unitsPerEM;
    }

    public void setUnitsPerEM(Integer unitsPerEM) {
        this.unitsPerEM = unitsPerEM;
    }

    public Integer getDescent() {
        return descent;
    }

    public void setDescent(Integer descent) {
        this.descent = descent;
    }

    public Integer getAscent() {
        return ascent;
    }

    public void setAscent(Integer ascent) {
        this.ascent = ascent;
    }

    public Glyph getGlyph(String unicode) throws IM3Exception {
        Glyph g = glyphs.get(unicode);
        if (g == null) {
            throw new IM3Exception("Glyph with unicode " + StringEscapeUtils.escapeJson(unicode) + " not found");
        }
        return g;
    }
}
