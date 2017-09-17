package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
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

    private Glyph getGlyph(LayoutFont layoutFont) throws ExportException {
        Glyph glyph = null;
        try {
            glyph = layoutFont.getGlyph(this);
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
        return glyph;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException {
        Glyph glyph = getGlyph(layoutFont);
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
    public void generatePDF(PDPageContentStream contentStream, LayoutFont layoutFont, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        Glyph glyph = getGlyph(layoutFont);
        try {
            contentStream.setFont(musicFont, LayoutConstants.FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(getPFDCoordinateX(page, x), getPFDCoordinateY(page, y));
            contentStream.showText(glyph.getUnicode());
            contentStream.endText();


        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public Node getJavaFXRoot(LayoutFont layoutFont) throws GUIException {
        SVGPath path = new SVGPath();
        try {
            Glyph glyph = getGlyph(layoutFont);
            path.setContent(glyph.getPath());
            path.getTransforms().add(layoutFont.getJavaFXScale());
            path.setLayoutX(x);
            path.setLayoutY(y);
        } catch (ExportException e) {
            throw new GUIException(e);
        }

        return path;
    }
}
