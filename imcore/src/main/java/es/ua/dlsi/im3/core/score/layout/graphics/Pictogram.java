package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.util.HashSet;

/**
 * A pictogram from a font in a SMuFL layout or similar
 */
public class Pictogram extends GraphicsElement {
    private static final String SIZE = LayoutConstants.EM + "px";
    /**
     * SMuFL (or similar) name of the pictogram (e.g. gClef)
     */
    private String codepoint;

    private double x;

    private double y;

    public Pictogram(String codepoint) {
        this.codepoint = codepoint;
    }

    public String getCodepoint() {
        return codepoint;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException {
        Glyph glyph = null;
        try {
            glyph = layoutFont.getGlyph(this);
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
        XMLExporterHelper.startEnd(sb, tabs, "use",
                "xlink:href", "#" + glyph.getEscapedUnicode(),
                "height", SIZE,
                "width", SIZE,
                "x", x + "px",
                "y", y + "px"
        );

        usedGlyphs.add(glyph);
    }

    @Override
    public void generatePDF(PDPageContentStream contents, LayoutFont layoutFont) throws ExportException {

    }

}
