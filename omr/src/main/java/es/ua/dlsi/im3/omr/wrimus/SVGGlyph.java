package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import es.ua.dlsi.im3.omr.classifiers.traced.Coordinate;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.util.HashSet;

public class SVGGlyph extends Shape {
    public static final String GLYPH = "glyph";
    public static final String CLASS = "class";
    public static final String G = "g";
    public static final String POLYLINE = "polyline";
    public static final String POINTS = "points";
    private static final String TRANSLATE = "translate";
    private static final String TRANSFORM = "transform";
    private static final String STYLE="style";
    private static final String STYLE_VALUE="fill:none;stroke:black;stroke-width:2";

    private final Glyph glyph;
    es.ua.dlsi.im3.core.score.layout.Coordinate position;

    public SVGGlyph(Glyph glyph, InteractionElementType interactionElementType) {
        super(new NotationSymbol() {
            @Override
            public GraphicsElement getGraphics() {
                return null;
            }

            @Override
            protected void doLayout() throws IM3Exception {

            }
        }, interactionElementType);
        this.glyph = glyph;
        this.position = new es.ua.dlsi.im3.core.score.layout.Coordinate();
    }

    @Override
    protected void doRepaint() throws IM3Exception {

    }

    @Override
    public double getWidth() throws IM3Exception {
        return glyph.getWidth();
    }

    @Override
    public es.ua.dlsi.im3.core.score.layout.Coordinate getPosition() throws IM3Exception {
        return position;
    }

    @Override
    public Node doGenerateJavaFXRoot() throws GUIException, ExportException {
        //not used
        return null;
    }

    @Override
    public void updateJavaFXRoot() throws IM3Exception {

    }

    @Override
    public void setJavaFXColor(Color color) {
    }

    @Override
    public void doGeneratePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        //not used
    }

    @Override
    public void doGenerateSVG(StringBuilder sb, int tabs, HashSet<es.ua.dlsi.im3.core.score.layout.svg.Glyph> usedGlyphs) throws ExportException {
        StringBuilder translate = new StringBuilder(TRANSLATE);
        translate.append('(');
        try {
            translate.append(position.getAbsoluteX());
            translate.append(',');
            translate.append(position.getAbsoluteY());
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
        translate.append(')');

        XMLExporterHelper.start(sb, tabs, G, CLASS, glyph.getSymbol().getName(), TRANSFORM, translate.toString());
        for (Stroke stroke: glyph.getStrokes()) {
            StringBuilder points = new StringBuilder();
            boolean first = true;
            for (Coordinate point: stroke.getPoints()) {
                if (first) {
                    first = false;
                } else {
                    points.append(' ');
                }
                points.append(point.getX());
                points.append(',');
                points.append(point.getY());
            }

            XMLExporterHelper.startEnd(sb, tabs+1, POLYLINE, STYLE, STYLE_VALUE, POINTS, points.toString());
        }
        XMLExporterHelper.end(sb, tabs, G);
    }
}
