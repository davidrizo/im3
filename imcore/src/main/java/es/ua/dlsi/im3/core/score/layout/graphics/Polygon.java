package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class Polygon extends Shape {
    private static final double DEFAULT_THICKNESS = 1;
    private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.eSolid;
    private static final RGBA DEFAULT_FILL_COLOR = new RGBA(0,0,0,1);
    private final RGBA fillColor;
    //private static final String TRAPEZOID = "rect"; //TODO

    LinkedList<Coordinate> points;

    double thickness;
    private StrokeType strokeType;
    private javafx.scene.shape.Polygon fxpoligon;

    public Polygon(NotationSymbol notationSymbol, InteractionElementType interactionElementType, LinkedList<Coordinate> points)  {
        this(notationSymbol, interactionElementType, points,  DEFAULT_THICKNESS, DEFAULT_STROKE_TYPE, DEFAULT_FILL_COLOR);
    }

    public Polygon(NotationSymbol notationSymbol, InteractionElementType interactionElementType, LinkedList<Coordinate> points, double thickness, StrokeType strokeType, RGBA fillColor) {
        super(notationSymbol,interactionElementType);
        this.points = points;
        this.thickness = thickness;
        this.strokeType = strokeType;
        this.fillColor = fillColor;
        initJavaFXShape();
        /*final StringBuilder fxPath = new StringBuilder();
        fxPath.append(SVGUtils.M).append(startX).append(SVGUtils.SPACE).append(startY).append(SVGUtils.SPACE)
                .append(SVGUtils.L).append(endX).append(SVGUtils.SPACE).append(endY);

        //TODO setStrokeType(strokeType);
        //TODO setThickness(thickness);
        setSVG(fxPath.toString());*/
    }

    private void initJavaFXShape()  {
        // TODO: 2/5/18 grosor, color
        fxpoligon = new javafx.scene.shape.Polygon(); // the contents will be filled later
        if (strokeType == StrokeType.eDashed) {
            fxpoligon.getStrokeDashArray().add(5d); //TODO Dependiendo del tamaño
        }
        fxpoligon.setFill(Color.color(fillColor.getR(), fillColor.getG(), fillColor.getB(), fillColor.getA()));
    }


    @Override
    public void doGenerateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        try {
            String strokeWidth;
            String style = "fill:" + fillColor.getHexadecimalString();

            StringBuilder svgPoints = new StringBuilder();
            for (Coordinate point: points) {
                if (svgPoints.length() > 0) {
                    svgPoints.append(' ');
                }
                svgPoints.append(point.getAbsoluteX());
                svgPoints.append(',');
                svgPoints.append(point.getAbsoluteY());
            }
            XMLExporterHelper.startEnd(sb, tabs, "polygon","points", svgPoints.toString(), "style", style);
            //TODO Resto de parámetros (ver Line)
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void doGeneratePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        try {
            contents.setStrokingColor(0, 0, 0);
            try {
                // TODO: 7/10/17 Stroke type
                contents.moveTo(getPFDCoordinateX(page, points.get(0).getAbsoluteX()), getPFDCoordinateY(page, points.get(0).getAbsoluteY()));
                for (int i=1; i<points.size(); i++) {
                    contents.lineTo(getPFDCoordinateX(page, points.get(i).getAbsoluteX()), getPFDCoordinateY(page, points.get(i).getAbsoluteY()));
                }
                //contents.setStrokingColor((int)fillColor.getR()*255, (int)fillColor.getG()*255, (int)fillColor.getB()*255); //TODO alpha
                contents.setNonStrokingColor((int)fillColor.getR()*255, (int)fillColor.getG()*255, (int)fillColor.getB()*255); //TODO alpha
                contents.fill();

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
        return fxpoligon;
    }

    @Override
    public void updateJavaFXRoot() throws IM3Exception {
        fxpoligon.getPoints().clear();
        for (Coordinate coordinate: points) {
            fxpoligon.getPoints().add(coordinate.getAbsoluteX());
            fxpoligon.getPoints().add(coordinate.getAbsoluteY());
        }
    }

    @Override
    public void setJavaFXColor(Color color) {
        fxpoligon.setStroke(color);
    }

    @Override
    protected void doRepaint() throws IM3Exception {
        // TODO: 14/3/18 ¿Qué hacemos aquí? 
    }

    @Override
    public double getWidth() {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        for (Coordinate coordinate: points) {
            minX = Math.min(minX, coordinate.getAbsoluteX());
            maxX = Math.max(maxX, coordinate.getAbsoluteX());
        }
        return maxX - minX;
    }


    @Override
    public Coordinate getPosition() {
        return points.get(0);
    }
}
