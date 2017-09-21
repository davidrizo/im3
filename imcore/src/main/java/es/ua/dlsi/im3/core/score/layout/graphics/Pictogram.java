package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
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

    Coordinate position;

    private double width;

    LayoutFont layoutFont;

    public Pictogram(LayoutFont layoutFont, String codepoint, Coordinate position) throws IM3Exception {
        this.codepoint = codepoint;
        this.layoutFont = layoutFont;
        this.position = position;
        glyph = layoutFont.getGlyph(this);

        path = new SVGPath();
        path.setContent(glyph.getPath());
        path.getTransforms().add(layoutFont.getJavaFXScale());
        width = path.getLayoutBounds().getWidth() * layoutFont.getScaleX();

    }

    public String getCodepoint() {
        return codepoint;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        XMLExporterHelper.startEnd(sb, tabs, "use",
                "xlink:href", "#" + glyph.getEscapedUnicode(),
                "height", SIZE,
                "width", SIZE,
                "x", position.getAbsoluteX() + "px",
                "y", position.getAbsoluteY() + "px"
        );

        usedGlyphs.add(glyph);
    }

    @Override
    public void generatePDF(PDPageContentStream contentStream, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        try {
            contentStream.setFont(musicFont, LayoutConstants.FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(getPFDCoordinateX(page, position.getAbsoluteX()), getPFDCoordinateY(page, position.getAbsoluteY()));
            contentStream.showText(glyph.getUnicode());
            contentStream.endText();


        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public Node getJavaFXRoot() throws GUIException {
        path.setLayoutX(position.getAbsoluteX());
        path.setLayoutY(position.getAbsoluteY());
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


}
