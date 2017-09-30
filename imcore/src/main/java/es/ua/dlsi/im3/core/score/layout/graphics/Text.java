package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Text extends Shape {
    private final LayoutFont layoutFont;
    private String text;
    private double width;
    private Coordinate position;
    /**
     * Used to compute dimensions of the text
     */
    private javafx.scene.text.Text javaFXText;

    public Text(String ID, LayoutFont layoutFont, String text, Coordinate position) {
        super(ID);
        this.position = position;
        this.layoutFont = layoutFont;
        this.text = text;
        this.javaFXText = new javafx.scene.text.Text(text);
        this.javaFXText.setFont(layoutFont.getJavaFXTextFont());
        this.width = this.javaFXText.getLayoutBounds().getWidth();
    }

    @Override
    public double getWidth() throws IM3Exception {
        return width;
    }

    @Override
    public Coordinate getPosition() throws IM3Exception {
        return position;
    }

    @Override
    public Node getJavaFXRoot() throws GUIException, ExportException {
        return this.javaFXText;
    }

    @Override
    public void generatePDF(PDPageContentStream contentStream, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        try {
            contentStream.setFont(textFont, (float)LayoutConstants.TEXT_FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(getPFDCoordinateX(page, position.getAbsoluteX()), getPFDCoordinateY(page, position.getAbsoluteY()));
            contentStream.showText(this.text);
            contentStream.endText();
        } catch (Exception e) {
            throw new ExportException(e);
        }

    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            XMLExporterHelper.text(sb, tabs, "text", text,
                    "x", position.getAbsoluteX() + "px",
                    "y", position.getAbsoluteY() + "px");
        } catch (IM3Exception e) {
            Logger.getLogger(Pictogram.class.getName()).log(Level.WARNING, "Cannot generate SVG for text " + text, e);
            throw new ExportException(e);
        }
    }
}
