package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It generates a SVG file with the input staff taking random symbols from the dataset
 */
public class StaffRenderer {
    private static final double GLYPH_SEPARATION = 30;
    final int STAFF_TOP_MARGIN = 120; // TODO
    final int LINE_THICKNESS = 3; // as defined in the Homus Dataset
    final int LINE_SEPARATION = 14; // as defined in the Homus Dataset
    private final Staff staff;
    private final HomusDataset homusDataset;
    ArrayList<Line> staffLines;
    List<Shape> shapes;
    private GlyphFinder glyphFinder;

    public StaffRenderer(HomusDataset homusDataset, Staff staff) {
        this.staff = staff;
        this.homusDataset = homusDataset;
        glyphFinder = new GlyphFinder(homusDataset);
    }

    public void render(File svgFile) throws IM3Exception {
        int width = 1000; // TODO
        int height = 400; // TODO

        shapes = new LinkedList<>();
        CoordinateComponent fromX = new CoordinateComponent();
        CoordinateComponent toX = new CoordinateComponent(width); // TODO: 7/11/17
        staffLines = new ArrayList<>();
        // render the staff lines
        for (int i=0; i<5; i++) {
            CoordinateComponent coordinateComponentY = new CoordinateComponent(STAFF_TOP_MARGIN+i*LINE_SEPARATION);
            Coordinate coordinateFrom = new Coordinate(fromX, coordinateComponentY);
            Coordinate coordinateTo = new Coordinate(toX, coordinateComponentY);
            Line line = new Line(null, coordinateFrom, coordinateTo);
            line.setThickness(3); // TODO: 7/11/17 Comprobarlo
            shapes.add(line);
            staffLines.add(line);
        }
        
        renderStaffContents();
        
        String svg = exportToSVG(width, height, shapes);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(svgFile),"UTF-8"));
            //TODO Sólo saca el primer canvas
            out.write(svg);
            out.close();
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }

    private void renderStaffContents() throws IM3Exception {
        // first get a list of glyphs
        List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
        List<SVGGlyph> glyphs = new LinkedList<>();
        HashMap<AtomPitch, Accidentals> accidentalsToShow = staff.createNoteAccidentalsToShow();
        //TODO Posición vertical
        for (ITimedElementInStaff symbol: symbols) {
            if (!symbol.getTime().isZero()) {
                //TODO Cuando hay cambios de compás
                if (staff.getScoreSong().getMeasureWithOnset(symbol.getTime()) != null) {
                    // add a barline
                    Glyph glyph = glyphFinder.findBarline();
                    glyphs.add(new SVGGlyph(glyph));
                }
            }

            if (symbol instanceof Clef) {
                Glyph glyph = glyphFinder.findClef((Clef) symbol);
                glyphs.add(new SVGGlyph(glyph));
            } else if (symbol instanceof KeySignature) {
                KeySignature keySignature = (KeySignature) symbol;
                for (KeySignatureAccidentalElement accidental: keySignature.getAccidentals()) {
                    Glyph glyph = glyphFinder.findAcidental(accidental.getAccidental());
                    glyphs.add(new SVGGlyph(glyph));
                }
            } else if (symbol instanceof FractionalTimeSignature) {
                Glyph glyph = glyphFinder.findTimeSignature((FractionalTimeSignature) symbol);
                glyphs.add(new SVGGlyph(glyph));
            } else if (symbol instanceof SimpleNote) {
                renderNote(glyphs, (SimpleNote) symbol, accidentalsToShow);
            } else if (symbol instanceof SimpleRest) {
                renderRest(glyphs, (SimpleRest) symbol);
            } else {
                Logger.getLogger(StaffRenderer.class.getName()).log(Level.WARNING, "Cannot find symbol type " + symbol.getClass());
            }
        }

        // then space them horizontally
        double currentX = 0;
        for (SVGGlyph glyph: glyphs) { // TODO: 7/11/17 Hacer un espaciado irregular
            glyph.getPosition().setDisplacementX(currentX);
            currentX += glyph.getWidth() + GLYPH_SEPARATION;
            shapes.add(glyph);
        }
    }

    private void renderNote(List<SVGGlyph> glyphs, SimpleNote symbol, HashMap<AtomPitch, Accidentals> accidentalsToShow) throws IM3Exception {
        // TODO: 7/11/17 Ledger lines
        Accidentals accidental = accidentalsToShow.get(symbol.getAtomPitch());
        if (accidental != null) {
            Glyph accGlyph = glyphFinder.findAcidental(accidental);
            glyphs.add(new SVGGlyph(accGlyph));
        }
        Glyph figureGlyph = glyphFinder.findFigure(symbol.getAtomFigure().getFigure(), "Note");
        glyphs.add(new SVGGlyph(figureGlyph));

        for (int i=0; i<symbol.getAtomFigure().getDots(); i++) {
            Glyph dotGlyph = glyphFinder.findDot();
            glyphs.add(new SVGGlyph(dotGlyph));
        }
    }

    private void renderRest(List<SVGGlyph> glyphs, SimpleRest symbol) throws IM3Exception {
        Glyph figureGlyph = glyphFinder.findFigure(symbol.getAtomFigure().getFigure(), "Rest");
        glyphs.add(new SVGGlyph(figureGlyph));

        for (int i=0; i<symbol.getAtomFigure().getDots(); i++) {
            Glyph dotGlyph = glyphFinder.findDot();
            glyphs.add(new SVGGlyph(dotGlyph));
        }
    }

    public String exportToSVG(int width, int height, Collection<Shape> shapes) throws IM3Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        XMLExporterHelper.start(sb, 0, "svg",
                "version", "1.1",
                "baseProfile", "full",
                "width", Integer.toString(width),
                "height", Integer.toString(height),
                "xmlns", "http://www.w3.org/2000/svg",
                "xmlns:xlink", "http://www.w3.org/1999/xlink",
                "xml:space", "preserve"
        );

        StringBuilder sbContent = new StringBuilder();
        XMLExporterHelper.start(sbContent, 1, "svg", "viewBox", "0 0 " + width + " " + height);

        XMLExporterHelper.start(sbContent, 2, "g", "class", "page", "transform", "translate(30, 30)"); //TODO Configurar márgen
        for (Shape shape: shapes) {
            shape.generateSVG(sbContent, 3, null);
        }

        XMLExporterHelper.end(sbContent, 2, "g");
        XMLExporterHelper.end(sbContent, 1, "svg");

        sb.append(sbContent);
        XMLExporterHelper.end(sb, 0, "svg");
        return sb.toString();
    }

}
