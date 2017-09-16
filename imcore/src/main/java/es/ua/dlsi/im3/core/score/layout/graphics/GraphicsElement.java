package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

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

    public abstract void generatePDF(PDPageContentStream contents, LayoutFont layoutFont, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException;

    protected float getPFDCoordinateX(PDPage page, double x) {
        return LayoutConstants.PDF_LEFT_MARGIN + (float) x + page.getCropBox().getLowerLeftX();
    }

    protected float getPFDCoordinateY(PDPage page, double y) {
        return -LayoutConstants.PDF_TOP_MARGIN -(float) y + page.getCropBox().getUpperRightY();
    }

}
