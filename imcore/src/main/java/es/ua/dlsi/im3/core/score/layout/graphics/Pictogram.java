package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
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
    private final Glyph glyph;
    /**
     * Used to compute dimensions of the pictogram
     */
    private final SVGPath path;
    /**
     * SMuFL (or similar) name of the pictogram (e.g. gClef)
     */
    private String codepoint;

    private double x;

    private double y;

    private double width;

    LayoutFont layoutFont;

    public Pictogram(LayoutFont layoutFont, String codepoint) throws IM3Exception {
        this.codepoint = codepoint;
        this.layoutFont = layoutFont;
        glyph = layoutFont.getGlyph(this);

        path = new SVGPath();
        path.setContent(glyph.getPath());
        path.getTransforms().add(layoutFont.getJavaFXScale());
        width = path.getLayoutBounds().getWidth() * layoutFont.getScaleX();

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
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
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
    public void generatePDF(PDPageContentStream contentStream, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
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
    public Node getJavaFXRoot() throws GUIException {
        path.setLayoutX(x);
        path.setLayoutY(y);
        return path;
    }

    public double getWidth() {
        return width;
    }
}