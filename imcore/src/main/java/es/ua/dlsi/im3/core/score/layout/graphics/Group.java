package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author drizo
 */
public class Group extends GraphicsElement {
    List<GraphicsElement> children;

    public Group() {
        this.children = new ArrayList<>();
    }

    public void add(GraphicsElement element) {
        this.children.add(element);
    }
    public void add(int index, GraphicsElement element) {
        this.children.add(index, element);
    }

    public List<GraphicsElement> getChildren() {
        return children;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException {
        XMLExporterHelper.start(sb, tabs, "g"); //TODO ID
        for (GraphicsElement child: children) {
            child.generateSVG(sb, tabs+1, layoutFont, usedGlyphs);
        }
        XMLExporterHelper.end(sb, tabs, "g"); //TODO ID

    }

    @Override
    public void generatePDF(PDPageContentStream contents, LayoutFont layoutFont, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        //TODO ¿cómo se hace un grupo?
        for (GraphicsElement child: children) {
            child.generatePDF(contents, layoutFont, musicFont, textFont, page);
        }
    }
}
