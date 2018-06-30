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

    public Text(NotationSymbol notationSymbol, InteractionElementType interactionElementType, LayoutFont layoutFont, String text, Coordinate position) {
        super(notationSymbol, interactionElementType);
        this.position = position;
        this.layoutFont = layoutFont;
        this.text = text;
        this.javaFXText = new javafx.scene.text.Text(text);
        this.javaFXText.setFont(layoutFont.getJavaFXTextFont());
        this.width = this.javaFXText.getLayoutBounds().getWidth();
    }

    @Override
    protected void doRepaint() {
        // TODO: 14/3/18 ¿Qué hacemos aquí? 
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public Coordinate getPosition() {
        return position;
    }

    @Override
    public Node doGenerateJavaFXRoot() {
        return this.javaFXText;
    }

    @Override
    public void updateJavaFXRoot() {
        this.javaFXText.setText(this.text);
        this.width = this.javaFXText.getLayoutBounds().getWidth();
    }

    @Override
    public void setJavaFXColor(Color color) {
        javaFXText.fillProperty().setValue(color);
    }

    @Override
    public void doGeneratePDF(PDPageContentStream contentStream, PDFExporter exporter, PDPage page) throws ExportException {
        try {
            contentStream.setFont(exporter.getTextFont(layoutFont), (float)LayoutConstants.TEXT_FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(getPFDCoordinateX(page, position.getAbsoluteX()), getPFDCoordinateY(page, position.getAbsoluteY()));
            contentStream.showText(this.text);
            contentStream.endText();
        } catch (Exception e) {
            throw new ExportException(e);
        }

    }

    @Override
    public void doGenerateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
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
