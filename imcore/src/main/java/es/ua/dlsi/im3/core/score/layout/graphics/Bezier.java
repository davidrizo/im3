package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
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

    public Bezier(String ID, Coordinate from, Coordinate controlPointFrom, Coordinate controlPointTo, Coordinate to) {
        super(ID);
        this.from = from;
        this.to = to;
        this.controlPointFrom = controlPointFrom;
        this.controlPointTo = controlPointTo;
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
                    "stroke-width", "3", // TODO: 1/10/17 No debería ser 3, deberíamos montar los slurs de otra forma
                    "d", path.toString());

        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void generatePDF(PDPageContentStream contents, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            try {
                contents.moveTo(getPFDCoordinateX(page, from.getAbsoluteX()), getPFDCoordinateY(page, from.getAbsoluteY()));
                contents.curveTo((float)controlPointFrom.getAbsoluteX(), (float)controlPointFrom.getAbsoluteY(),
                        (float)controlPointTo.getAbsoluteX(), (float)controlPointTo.getAbsoluteY(),
                        (float)to.getAbsoluteX(), (float)to.getAbsoluteY());
            } catch (IM3Exception e) {
                throw new ExportException(e);
            }
            contents.stroke();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public Node getJavaFXRoot() throws ExportException {
        try {
            CubicCurve cubicCurve = new CubicCurve(from.getAbsoluteX(), from.getAbsoluteY(), controlPointFrom.getAbsoluteY(), controlPointFrom.getAbsoluteY(),
                    controlPointTo.getAbsoluteX(), controlPointTo.getAbsoluteY(), to.getAbsoluteX(), to.getAbsoluteY());
            cubicCurve.setFill(Color.TRANSPARENT);
            return cubicCurve;
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
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
