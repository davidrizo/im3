package es.ua.dlsi.im3.core.score.layout.svg;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Objects;

public class Glyph {
    /**
     * Optional
     */
    private String name;

    private String unicode;
    /**
     * The default horizontal advance after rendering a glyph in horizontal orientation. Glyph widths are required to be non-negative, even if the glyph is typically rendered right-to-left, as in Hebrew and Arabic scripts.
     */
    Integer defaultHorizontalAdvance;

    /**
     * d parameter
     */
    String path;
    /**
     * It includes the font name to avoid confusions of same unicodes in SVG with several fonts
     */
    private String escapedUnicode;

    private String fontName;


    public Glyph(String fontName, String unicode, String path) {
        this.fontName = fontName;
        this.unicode = unicode;
        this.escapedUnicode = StringEscapeUtils.escapeJson(fontName + unicode);
        this.path = path;
    }

    public String getFontName() {
        return fontName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnicode() {
        return unicode;
    }

    public Integer getDefaultHorizontalAdvance() {
        return defaultHorizontalAdvance;
    }

    public void setDefaultHorizontalAdvance(int defaultHorizontalAdvance) {
        this.defaultHorizontalAdvance = defaultHorizontalAdvance;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Glyph{" +
                "unicode='" + unicode + '\'' +
                ", path=" + path +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Glyph glyph = (Glyph) o;
        return Objects.equals(escapedUnicode, glyph.escapedUnicode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(escapedUnicode);
    }

    public String getEscapedUnicodeFontUnique() {
        return escapedUnicode;
    }
}
