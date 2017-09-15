package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.util.HashSet;

public abstract class GraphicsElement {
    private Canvas canvas;
    //TODO AÑADIR ID

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     *
     * @param sb
     * @param tabs
     * @param layoutFont
     * @param usedGlyphs All glyphs included should be added here
     * @throws ExportException
     */
    public abstract void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException;

    public abstract void generatePDF(PDPageContentStream contents, LayoutFont layoutFont) throws ExportException;
}
