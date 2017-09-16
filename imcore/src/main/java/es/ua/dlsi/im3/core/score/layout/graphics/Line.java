package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.HashSet;

public class Line extends Shape {
    private static final double DEFAULT_THICKNESS = 1;
    private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.eSolid;
    private static final String LINE = "rect"; //TODO

    double startX;
    double startY;
    double endX;
    double endY;
    double thickness;
    private StrokeType strokeType;

    public Line(double startX, double startY, double endX, double endY) {
        this(startX, startY, endX, endY, DEFAULT_THICKNESS, DEFAULT_STROKE_TYPE);
    }

    public Line(double startX, double startY, double endX, double endY, double thickness, StrokeType strokeType) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.thickness = thickness;
        this.strokeType = strokeType;

        /*final StringBuilder fxPath = new StringBuilder();
        fxPath.append(SVGUtils.M).append(startX).append(SVGUtils.SPACE).append(startY).append(SVGUtils.SPACE)
                .append(SVGUtils.L).append(endX).append(SVGUtils.SPACE).append(endY);

        //TODO setStrokeType(strokeType);
        //TODO setThickness(thickness);
        setSVG(fxPath.toString());*/
    }


    @Override
    public void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) {
        XMLExporterHelper.startEnd(sb, tabs, "line",
                "x1", Double.toString(startX),
                "y1", Double.toString(startY),
                "x2", Double.toString(endX),
                "y2", Double.toString(endY),
                "stroke", "black" //TODO Constants
        );
        //TODO Stroke and width

    }

    @Override
    public void generatePDF(PDPageContentStream contents, LayoutFont layoutFont) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            contents.moveTo((float)startX, (float)startY);
            contents.lineTo((float)endX, (float)endY);
            contents.stroke();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
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
}
