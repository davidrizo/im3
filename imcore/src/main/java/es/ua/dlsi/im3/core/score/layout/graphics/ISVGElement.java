package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;

import java.util.HashSet;

public interface ISVGElement {
    /**
     *
     * @param sb
     * @param tabs
     * @param layoutFont
     * @param usedGlyphs All glyphs included should be added here
     * @throws ExportException
     */
    void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException;
}
