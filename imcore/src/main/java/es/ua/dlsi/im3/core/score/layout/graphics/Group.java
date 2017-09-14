package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.util.ArrayList;
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

    public List<GraphicsElement> getChildren() {
        return children;
    }

    @Override
    public void generateSVG(StringBuilder sb, int tabs) {
        XMLExporterHelper.start(sb, tabs, "g"); //TODO ID
        for (GraphicsElement child: children) {
            child.generateSVG(sb, tabs+1);
        }
        XMLExporterHelper.end(sb, tabs, "g"); //TODO ID

    }
}
