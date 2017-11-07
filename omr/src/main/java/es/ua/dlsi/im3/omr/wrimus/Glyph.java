package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import es.ua.dlsi.im3.omr.traced.Coordinate;
import javafx.scene.Node;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Glyph  {
    Symbol symbol;
    LinkedList<Stroke> strokes;
    Double width;

    public Glyph(Symbol symbol) {
        this.symbol = symbol;
        strokes = new LinkedList<>();
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Stroke> getStrokes() {
        return strokes;
    }

    public void addStroke(Stroke stroke) {
        strokes.add(stroke);
    }

    public double getWidth() {
        // just computed once
        if (width == null) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            for (Stroke stroke: strokes) {
                for (Coordinate point: stroke.getPoints()) {
                    minX = Math.min(minX, point.getX());
                    maxX = Math.max(maxX, point.getX());
                }
            }
            width = maxX - minX;
        }
        return width;
    }
}
