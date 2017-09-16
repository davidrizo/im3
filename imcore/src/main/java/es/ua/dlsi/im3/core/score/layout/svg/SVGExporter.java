package es.ua.dlsi.im3.core.score.layout.svg;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.graphics.*;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

//TODO Constantes
public class SVGExporter implements IGraphicsExporter {
    //TODO ver cómo cargar una fuente u otra - ver PDFExporter
    /**
     * Do not load until required
     */
    private static LayoutFont bravura = null;

    //TODO Cargar fuente .... sólo de lo que necesitamos
    private void fillDefinitions(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) {
        String viewbox = "0 0 " + bravura.getSVGFont().getUnitsPerEM() + " " + bravura.getSVGFont().getUnitsPerEM();

        XMLExporterHelper.start(sb, tabs, "defs");
        for (Glyph glyph: usedGlyphs) {
            XMLExporterHelper.start(sb, tabs+1, "symbol", "id", glyph.getEscapedUnicode(),
                    "viewBox", viewbox,
                    "overflow", "visible");
            XMLExporterHelper.startEnd(sb, tabs+2, "path", "d", glyph.getPath(),
                    "transform", "scale(1,-1)");
            XMLExporterHelper.end(sb, tabs+1, "symbol");
        }

        XMLExporterHelper.end(sb, tabs, "defs");
    }

    public String exportLayout(Canvas canvas) throws IM3Exception {
        try {
            initFont();
        } catch (ImportException e) {
            throw new ExportException(e);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        XMLExporterHelper.start(sb, 0, "svg",
                "version", "1.1",
                "baseProfile", "full",
                "width", Integer.toString(canvas.getWidth()),
                "height", Integer.toString(canvas.getHeight()),
                "xmlns", "http://www.w3.org/2000/svg",
                "xmlns:xlink", "http://www.w3.org/1999/xlink",
                "xml:space", "preserve"
                );

        StringBuilder sbContent = new StringBuilder();
        HashSet<Glyph> usedGlyphs = new HashSet<>();

        XMLExporterHelper.start(sbContent, 1, "svg", "viewBox", "0 0 " + canvas.getWidth() + " " + canvas.getHeight()); //TODO ¿Qué valor poner - antes tenía *EM

        XMLExporterHelper.start(sbContent, 2, "g", "class", "page", "transform", "translate(30, 30)"); //TODO Configurar márgen
        for (GraphicsElement element: canvas.getElements()) {
            element.generateSVG(sbContent, 3, bravura, usedGlyphs);
        }

        XMLExporterHelper.end(sbContent, 2, "g");
        XMLExporterHelper.end(sbContent, 1, "svg");

        fillDefinitions(sb, 1, usedGlyphs);

        sb.append(sbContent);
        XMLExporterHelper.end(sb, 0, "svg");
        return sb.toString();
    }

    /**
     * Package and return used for tests
     * @throws ImportException
     */
    synchronized LayoutFont initFont() throws ImportException, IM3Exception {
        if (bravura == null) {
            bravura = new BravuraFont();
        }
        return bravura;
    }

    @Override
    public void exportLayout(OutputStream os, ScoreLayout layout) throws ExportException {
        if (layout.getCanvases().length != 1) {
            throw new ExportException("Cannot export " + layout.getCanvases().length + " canvases to SVG");
        }
        try (Writer w = new OutputStreamWriter(os, "UTF-8")) {
            w.write(exportLayout(layout.getCanvases()[0]));
        } // or w.close(); //close will auto-flush    }
        catch (Exception e) {
            throw new ExportException(e);
        }
    }

        @Override
    public void exportLayout(File file, ScoreLayout layout)  throws ExportException {
            if (layout.getCanvases().length != 1) {
                throw new ExportException("Cannot export " + layout.getCanvases().length + " canvases to SVG");
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
                out.write(exportLayout(layout.getCanvases()[0]));
                out.close();
            } catch (Exception e) {
                throw new ExportException(e);
            }
    }
}
