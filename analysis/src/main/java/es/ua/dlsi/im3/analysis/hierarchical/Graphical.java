package es.ua.dlsi.im3.analysis.hierarchical;

import es.ua.dlsi.im3.core.adt.tree.ILabelColorMapping;

import java.util.HashMap;

/**
 * Graphical information related to the analysis
 * @autor drizo
 */
public class Graphical implements ILabelColorMapping {
    /**
     * Mapping from color to node names
     */
    HashMap<String, String> colorMappings;

    public Graphical() {
        colorMappings = new HashMap<>();
    }

    public void addColorMapping(String nodeName, String color) {
        colorMappings.put(nodeName, color);
    }

    @Override
    public String getColorMapping(String nodeName) {
        return colorMappings.get(nodeName);
    }
}
