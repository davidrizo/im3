package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    Coordinate position;

    private double width;

    private double height;

    LayoutFont layoutFont;

    public Pictogram(String ID, LayoutFont layoutFont, String codepoint, Coordinate position) throws IM3Exception {
        super(ID);
        this.codepoint = codepoint;
        this.layoutFont = layoutFont;
        this.position = position;
        if (layoutFont == null) {
            throw new IM3Exception("layoutFont cannot be null");
        }
        glyph = layoutFont.getGlyph(this);

        path = new SVGPath();
        path.setContent(glyph.getPath());
        path.getTransforms().add(layoutFont.getJavaFXScale());
        width = path.getLayoutBounds().getWidth() * layoutFont.getScaleX();
        height = path.getLayoutBounds().getHeight() * layoutFont.getScaleX();
    }

    public String getCodepoint() {
        return codepoint;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            XMLExporterHelper.startEnd(sb, tabs, "use",
                    "xlink:href", "#" + glyph.getEscapedUnicodeFontUnique(),
                    "height", SIZE,
                    "width", SIZE,
                    "x", position.getAbsoluteX() + "px",
                    "y", position.getAbsoluteY() + "px"
            );
        } catch (IM3Exception e) {
            Logger.getLogger(Pictogram.class.getName()).log(Level.WARNING, "Cannot generate SVG for pictogram with codepoint " + codepoint, e);
            throw new ExportException(e);
        }

        usedGlyphs.add(glyph);
    }

    @Override
    public void generatePDF(PDPageContentStream contentStream, PDFExporter exporter, PDPage page) throws ExportException {
        try {
            PDType0Font musicFont = exporter.getMusicFont(layoutFont);

            contentStream.setFont(musicFont, LayoutConstants.FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(getPFDCoordinateX(page, position.getAbsoluteX()), getPFDCoordinateY(page, position.getAbsoluteY()));
            contentStream.showText(glyph.getUnicode());
            contentStream.endText();


        } catch (Exception e) {
            throw new ExportException(e);
        }
    }

    @Override
    public Node getJavaFXRoot() throws GUIException {
        path.setLayoutX(position.getAbsoluteX());
        try {
            path.setLayoutY(position.getAbsoluteY());
        } catch (IM3Exception e) {
            throw new GUIException(e);
        }
        return path;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public Coordinate getPosition() {
        return position;
    }


    public double getHeight() {
        return height;
    }
}
