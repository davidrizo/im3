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
    String codepoint;

    public Pictogram(String codepoint) {
        this.codepoint = codepoint;
    }

    public String getCodepoint() {
        return codepoint;
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
                "x", "20px",
                "y", "12px"
        ); //TODO resto <use xlink:href="#E93C" x="12178" y="1850" height="720px" width="720px" />

        usedGlyphs.add(glyph);
    }

    @Override
    public void generatePDF(PDPageContentStream contents, LayoutFont layoutFont) throws ExportException {

    }

}
