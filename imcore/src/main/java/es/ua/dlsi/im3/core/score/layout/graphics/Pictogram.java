package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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
    private Glyph glyph;
    /**
     * Used to compute dimensions of the pictogram
     */
    private final SVGPath path;
    /**
     * SMuFL (or similar) name of the pictogram (e.g. gClef)
     */
    private String codePoint;

    Coordinate position;

    private double width;

    private double height;

    LayoutFont layoutFont;

    public Pictogram(NotationSymbol notationSymbol, InteractionElementType interactionElementType, LayoutFont layoutFont, String codepoint, Coordinate position) throws IM3Exception {
        super(notationSymbol, interactionElementType);
        this.codePoint = codepoint;
        this.layoutFont = layoutFont;
        this.position = position;
        if (layoutFont == null) {
            throw new IM3Exception("layoutFont cannot be null");
        }
        path = new SVGPath();
        path.getTransforms().add(layoutFont.getJavaFXScale());
        doRepaint();
    }

    @Override
    protected void doRepaint() throws IM3Exception {
        glyph = layoutFont.getGlyph(getCodePoint());
        path.setContent(glyph.getPath());
        width = path.getLayoutBounds().getWidth() * layoutFont.getScaleX();
        height = path.getLayoutBounds().getHeight() * layoutFont.getScaleX();
    }

    public String getCodePoint() {
        return codePoint;
    }

    @Override
    public void doGenerateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            XMLExporterHelper.startEnd(sb, tabs, "use",
                    "xlink:href", "#" + glyph.getEscapedUnicodeFontUnique(),
                    "height", SIZE,
                    "width", SIZE,
                    "x", position.getAbsoluteX() + "px",
                    "y", position.getAbsoluteY() + "px"
            );
        } catch (IM3Exception e) {
            Logger.getLogger(Pictogram.class.getName()).log(Level.WARNING, "Cannot generate SVG for pictogram with codepoint " + codePoint, e);
            throw new ExportException(e);
        }

        usedGlyphs.add(glyph);
    }

    @Override
    public void doGeneratePDF(PDPageContentStream contentStream, PDFExporter exporter, PDPage page) throws ExportException {
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
    public Node doGenerateJavaFXRoot() throws GUIException {
        try {
            updateJavaFXRoot();
        } catch (IM3Exception e) {
            throw new GUIException(e);
        }
        return path;
    }

    @Override
    public void updateJavaFXRoot() throws IM3Exception {
        path.setLayoutX(position.getAbsoluteX());
        path.setLayoutY(position.getAbsoluteY());
    }

    @Override
    public void setJavaFXColor(Color color) {
        path.setFill(color);
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

    public void setCodePoint(String codePoint) {
        this.codePoint = codePoint;
    }
}
