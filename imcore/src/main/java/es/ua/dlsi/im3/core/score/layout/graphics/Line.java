package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.HashSet;

public class Line extends Shape {
    private static final double DEFAULT_THICKNESS = 1;
    private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.eSolid;
    private static final String LINE = "rect"; //TODO

    Coordinate from;
    Coordinate to;

    double thickness;
    private StrokeType strokeType;
    private javafx.scene.shape.Line fxline;

    public Line(NotationSymbol notationSymbol, InteractionElementType interactionElementType, Coordinate from, Coordinate to)  {
        this(notationSymbol, interactionElementType, from, to, DEFAULT_THICKNESS, DEFAULT_STROKE_TYPE);
    }


    public Line(NotationSymbol notationSymbol, InteractionElementType interactionElementType, Coordinate from, Coordinate to, double thickness, StrokeType strokeType) {
        super(notationSymbol,interactionElementType);
        this.from = from;
        this.to = to;
        this.thickness = thickness;
        this.strokeType = strokeType;

        initJavaFXShape();
        /*final StringBuilder fxPath = new StringBuilder();
        fxPath.append(SVGUtils.M).append(startX).append(SVGUtils.SPACE).append(startY).append(SVGUtils.SPACE)
                .append(SVGUtils.L).append(endX).append(SVGUtils.SPACE).append(endY);

        //TODO setStrokeType(strokeType);
        //TODO setThickness(thickness);
        setSVG(fxPath.toString());*/
    }

    private void initJavaFXShape()  {
        //fxline = new javafx.scene.shape.Line(from.getAbsoluteX(), from.getAbsoluteY(), to.getAbsoluteX(), to.getAbsoluteY()); // TODO: 17/9/17 Grosor, color
        fxline = new javafx.scene.shape.Line(); // TODO: 17/9/17 Grosor, color
        if (strokeType == StrokeType.eDashed) {
            fxline.getStrokeDashArray().add(5d); //TODO Dependiendo del tamaño
        }
    }


    @Override
    public void doGenerateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            String strokeWidth;

            if (this.getStrokeType() == StrokeType.eDashed) {
                XMLExporterHelper.startEnd(sb, tabs, "line",
                        "x1", Double.toString(from.getAbsoluteX()),
                        "y1", Double.toString(from.getAbsoluteY()),
                        "x2", Double.toString(to.getAbsoluteX()),
                        "y2", Double.toString(to.getAbsoluteY()),
                        "stroke", "black", //TODO Constants
                        "stroke-dasharray", "5, 5",
                        "stroke-width", Double.toString(thickness)
                );
            } else {
                XMLExporterHelper.startEnd(sb, tabs, "line",
                        "x1", Double.toString(from.getAbsoluteX()),
                        "y1", Double.toString(from.getAbsoluteY()),
                        "x2", Double.toString(to.getAbsoluteX()),
                        "y2", Double.toString(to.getAbsoluteY()),
                        "stroke", "black", //TODO Constants
                        "stroke-width", Double.toString(thickness)
                );
            }
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
        //TODO Stroke and width

    }

    @Override
    public void doGeneratePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            try {
                // TODO: 7/10/17 Stroke type
                contents.moveTo(getPFDCoordinateX(page, from.getAbsoluteX()), getPFDCoordinateY(page, from.getAbsoluteY()));
                contents.lineTo(getPFDCoordinateX(page, to.getAbsoluteX()), getPFDCoordinateY(page, to.getAbsoluteY()));
            } catch (IM3Exception e) {
                throw new ExportException(e);
            }
            contents.stroke();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public StrokeType getStrokeType() {
        return strokeType;
    }

    public void setStrokeType(StrokeType strokeType) {
        this.strokeType = strokeType;
    }

    @Override
    public Node doGenerateJavaFXRoot() throws ExportException {
        try {
            updateJavaFXRoot();
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
        return fxline;
    }

    @Override
    public void updateJavaFXRoot() throws IM3Exception {
        fxline.setStartX(from.getAbsoluteX());
        fxline.setStartY(from.getAbsoluteY());
        fxline.setEndX(to.getAbsoluteX());
        fxline.setEndY(to.getAbsoluteY());
    }

    @Override
    public void setJavaFXColor(Color color) {
        fxline.setStroke(color);
    }

    @Override
    protected void doRepaint() throws IM3Exception {
        // TODO: 14/3/18 ¿Qué hacemos aquí? 
    }

    @Override
    public double getWidth() {
        return to.getAbsoluteX() - from.getAbsoluteX() + thickness;
    }

    @Override
    public Coordinate getPosition() {
        return from;
    }

    public Coordinate getFrom() {
        return from;
    }

    public void setFrom(Coordinate from) {
        this.from = from;
    }

    public Coordinate getTo() {
        return to;
    }

    public void setTo(Coordinate to) {
        this.to = to;
    }
}
