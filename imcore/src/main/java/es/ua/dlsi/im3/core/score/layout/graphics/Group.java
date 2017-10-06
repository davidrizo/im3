package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
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

    public Group(String id) {
        super(id);
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

    public void remove(GraphicsElement element) {
        this.children.remove(element);
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
    public void generatePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        //TODO ¿cómo se hace un grupo?
        for (GraphicsElement child: children) {
            child.generatePDF(contents, exporter, page);
        }
    }

    @Override
    public Node getJavaFXRoot() throws GUIException {
        javafx.scene.Group group = new javafx.scene.Group();
        for (GraphicsElement child: children) {
            Node node = null;
            try {
                node = child.getJavaFXRoot();
            } catch (ExportException e) {
                throw new GUIException(e);
            }
            if (node != null) {
                group.getChildren().add(node);
            }
        }
        return group;
    }


    @Override
    public double getWidth() throws IM3Exception {
        // TODO: 19/9/17 Maybe we could save this value and update it for each add and width change of an element
        /*double fromX = Double.MAX_VALUE;
        double toX = Double.MIN_VALUE;

        for (GraphicsElement child: children) {
            double childX = child.getPosition().getAbsoluteX();
            fromX = Math.min(fromX, childX);
            toX = Math.max(toX, childX + child.getWidth());
        }

        return (toX - fromX);*/
        return computeBoundingBox().getWidth();
    }

    /**
     * The space between the x of the symbol and its left end
     * @return
     */
    public BoundingBox computeBoundingBox() throws IM3Exception {
        if (children.isEmpty()) {
            return new BoundingBox(0, 0);
        } else {
            // TODO: 19/9/17 Maybe we could save this value and update it for each add and width change of an element
            double maxLeftDisplacement = Double.MAX_VALUE;
            double maxRightDisplacement = Double.MIN_VALUE;

            for (GraphicsElement child : children) {
                BoundingBox childBB = child.computeBoundingBox();
                maxLeftDisplacement = Math.min(maxLeftDisplacement, childBB.getLeftEnd());
                maxRightDisplacement = Math.max(maxLeftDisplacement, childBB.getRightEnd());
            }

            return new BoundingBox(maxLeftDisplacement, maxRightDisplacement);
        }
    }

    @Override
    public Coordinate getPosition() throws IM3Exception {
        Coordinate leftTop = null;

        for (GraphicsElement child: children) {
            if (leftTop == null) {
                leftTop = child.getPosition();
            } else {
                leftTop = Coordinate.min(leftTop, child.getPosition());
            }
        }

        if (leftTop == null) {
            throw new IM3RuntimeException("Empty group, cannot compute position");
        }
        return leftTop;
    }

    /*@Override
    public double getX() {
        // TODO: 19/9/17 Maybe we could save this value and update it for each add and width change of an element
        // We could also store relative coordinates of the grouped elements
        double fromX = Double.MAX_VALUE;

        for (GraphicsElement child: children) {
            fromX = Math.min(fromX, child.getX());
        }

        return fromX;
    }*/
}
