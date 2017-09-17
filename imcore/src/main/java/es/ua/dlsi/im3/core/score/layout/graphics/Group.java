package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;
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
        if (element == null) {
            throw new IM3RuntimeException("Cannot add a null element");
        }
        this.children.add(element);
    }
    public void add(int index, GraphicsElement element) {
        if (element == null) {
            throw new IM3RuntimeException("Cannot add a null element");
        }

        this.children.add(index, element);
    }

    public List<GraphicsElement> getChildren() {
        return children;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        XMLExporterHelper.start(sb, tabs, "g"); //TODO ID
        for (GraphicsElement child: children) {
            child.generateSVG(sb, tabs+1, usedGlyphs);
        }
        XMLExporterHelper.end(sb, tabs, "g"); //TODO ID

    }

    @Override
    public void generatePDF(PDPageContentStream contents, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {
        //TODO ¿cómo se hace un grupo?
        for (GraphicsElement child: children) {
            child.generatePDF(contents, musicFont, textFont, page);
        }
    }

    @Override
    public Node getJavaFXRoot() throws GUIException {
        javafx.scene.Group group = new javafx.scene.Group();
        for (GraphicsElement child: children) {
            Node node = child.getJavaFXRoot();
            if (node != null) {
                group.getChildren().add(node);
            }
        }
        return group;
    }
}
