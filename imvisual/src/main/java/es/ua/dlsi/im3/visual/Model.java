package es.ua.dlsi.im3.visual;

import java.util.LinkedList;
import java.util.List;

public class Model {
    List<Node> nodes;

    public Model() {
        this.nodes = new LinkedList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
