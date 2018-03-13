package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author drizo
 */
public class Bezier extends Shape {
    private final Coordinate controlPointTo;
    private final Coordinate controlPointFrom;
    Coordinate from;
    Coordinate to;
    Coordinate middleInnerPoint;
    private CubicCurve javaFXCubicCurve;

    public Bezier(InteractionElementType interactionElementType, Coordinate from, Coordinate controlPointFrom, Coordinate controlPointTo, Coordinate to) {
        super(interactionElementType);
        this.from = from;
        this.to = to;
        this.controlPointFrom = controlPointFrom;
        this.controlPointTo = controlPointTo;
        initJavaFX();
    }

    private void initJavaFX() {
        javaFXCubicCurve = new CubicCurve();
        javaFXCubicCurve.setFill(Color.TRANSPARENT);
        javaFXCubicCurve.setStroke(Color.BLACK);
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            // see https://www.sitepoint.com/html5-svg-cubic-curves/
            StringBuilder path = new StringBuilder();
            path.append('M');
            path.append(from.getAbsoluteX());
            path.append(',');
            path.append(from.getAbsoluteY());
            path.append(' ');

            path.append('C');
            path.append(controlPointFrom.getAbsoluteX());
            path.append(',');
            path.append(controlPointFrom.getAbsoluteY());

            path.append(' ');
            path.append(controlPointTo.getAbsoluteX());
            path.append(',');
            path.append(controlPointTo.getAbsoluteY());

            path.append(' ');
            path.append(to.getAbsoluteX());
            path.append(',');
            path.append(to.getAbsoluteY());

            XMLExporterHelper.startEnd(sb, tabs, "path",
                    "stroke", "black",
                    "fill", "none",
                    "stroke-width", "2", // TODO: 1/10/17 No debería ser 3, deberíamos montar los slurs de otra forma
                    "d", path.toString());

        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void generatePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            contents.setLineWidth(2); //TODO
            try {
                contents.moveTo(getPFDCoordinateX(page, from.getAbsoluteX()), getPFDCoordinateY(page, from.getAbsoluteY()));
                contents.curveTo(
                        getPFDCoordinateX(page, controlPointFrom.getAbsoluteX()), getPFDCoordinateY(page, controlPointFrom.getAbsoluteY()),
                        getPFDCoordinateX(page, controlPointTo.getAbsoluteX()), getPFDCoordinateY(page, controlPointTo.getAbsoluteY()),
                        getPFDCoordinateX(page, to.getAbsoluteX()), getPFDCoordinateY(page, to.getAbsoluteY()));
            } catch (IM3Exception e) {
                throw new ExportException(e);
            }
            contents.stroke();
            contents.setLineWidth(1); //TODO
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public Node getJavaFXRoot() throws ExportException {
        try {
            javaFXCubicCurve.setStartX(from.getAbsoluteX());
            javaFXCubicCurve.setStartY(from.getAbsoluteY());
            javaFXCubicCurve.setControlX1(controlPointFrom.getAbsoluteX());
            javaFXCubicCurve.setControlY1(controlPointFrom.getAbsoluteY());
            javaFXCubicCurve.setControlX2(controlPointTo.getAbsoluteX());
            javaFXCubicCurve.setControlY2(controlPointTo.getAbsoluteY());
            javaFXCubicCurve.setEndX(to.getAbsoluteX());
            javaFXCubicCurve.setEndY(to.getAbsoluteY());
            /*from.getAbsoluteX(), from.getAbsoluteY(), controlPointFrom.getAbsoluteY(), controlPointFrom.getAbsoluteY(),
                    controlPointTo.getAbsoluteX(), controlPointTo.getAbsoluteY(), to.getAbsoluteX(), to.getAbsoluteY()*/
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }

        return javaFXCubicCurve;
    }

    @Override
    public void setJavaFXColor(Color color) {
        javaFXCubicCurve.setStroke(color);
    }

    @Override
    public double getWidth() {
        return to.getAbsoluteX() - from.getAbsoluteX();
    }

    @Override
    public Coordinate getPosition() {
        return from;
    }
}
