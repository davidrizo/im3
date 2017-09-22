package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import javafx.scene.Node;
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

    public Line(String ID, Coordinate from, Coordinate to) {
        this(ID, from, to, DEFAULT_THICKNESS, DEFAULT_STROKE_TYPE);
    }


    public Line(String ID, Coordinate from, Coordinate to, double thickness, StrokeType strokeType) {
        super(ID);
        this.from = from;
        this.to = to;
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
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) {
        XMLExporterHelper.startEnd(sb, tabs, "line",
                "x1", Double.toString(from.getAbsoluteX()),
                "y1", Double.toString(from.getAbsoluteY()),
                "x2", Double.toString(to.getAbsoluteX()),
                "y2", Double.toString(to.getAbsoluteY()),
                "stroke", "black" //TODO Constants
        );
        //TODO Stroke and width

    }

    @Override
    public void generatePDF(PDPageContentStream contents, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            contents.moveTo(getPFDCoordinateX(page, from.getAbsoluteX()), getPFDCoordinateY(page, from.getAbsoluteY()));
            contents.lineTo(getPFDCoordinateX(page, to.getAbsoluteX()), getPFDCoordinateY(page, to.getAbsoluteY()));
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
    public Node getJavaFXRoot() {
        return new javafx.scene.shape.Line(from.getAbsoluteX(), from.getAbsoluteY(), to.getAbsoluteX(), to.getAbsoluteY()); // TODO: 17/9/17 Grosor, color
    }

    @Override
    public double getWidth() {
        return to.getAbsoluteX() - from.getAbsoluteX();
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
