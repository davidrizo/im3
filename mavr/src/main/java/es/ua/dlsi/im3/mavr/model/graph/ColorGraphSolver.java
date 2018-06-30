package es.ua.dlsi.im3.mavr.model.graph;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.mavr.model.harmony.IntegerHSB;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Try to find colors that match the percentages in the graph.
 * @autor drizo
 */
public class ColorGraphSolver {
    ConceptGraph<String> conceptGraph;
    private HashMap<String, IntegerHSB> conceptColors;

    public ColorGraphSolver(ConceptGraph<String> conceptGraph) {
        this.conceptGraph = conceptGraph;
    }

    public void findColors() throws IM3Exception {
        conceptColors = new HashMap<>();
        // first find nodes that have not an output edge
        Collection<GraphNode<ConceptLabel, ConceptRelationEdgeLabel>> mainNodes = conceptGraph.getGraph().getNodesWithoutOutEdge();

        // first fixed colors
        double angle = 360.0 / (double) mainNodes.size();
        double nextHue = 0;
        for (GraphNode<ConceptLabel, ConceptRelationEdgeLabel> graphNode: mainNodes) {
            ConceptLabel label = graphNode.getLabel();
            String concept = label.getConcept().toString();
            IntegerHSB color = computeColor(graphNode, nextHue, 100, 100);
            conceptColors.put(concept, color);
            graphNode.setColor(color.toLAB().hex());
            nextHue += angle;

        }
        // first primary colors, then related
        nextHue = 0;
        for (GraphNode<ConceptLabel, ConceptRelationEdgeLabel> graphNode: mainNodes) {
            computeNodeColor(graphNode, nextHue, 100, 100);
            nextHue += angle;
        }

    }

    private void computeNodeColor(GraphNode<ConceptLabel,ConceptRelationEdgeLabel> graphNode, double hue, int saturation, int bright) throws IM3Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Computing node for concept {0}", graphNode.getLabel().toString());

        if (!conceptColors.containsKey(graphNode.getLabel().getConcept().toString())) {
            IntegerHSB hsb = computeColor(graphNode, hue, saturation, bright);
            String hexaColor = hsb.toLAB().hex();
            conceptColors.put(graphNode.getLabel().getConcept().toString(), hsb);
            graphNode.setColor(hexaColor);
        }

        // Now find relationships - decrease saturation and bright as we move away the main node
        //TODO Esto aún es muy básico - no tiene en cuenta las distancias..

        Collection<GraphEdge<ConceptRelationEdgeLabel>> inEdges = graphNode.getInEdges();
        for (GraphEdge<ConceptRelationEdgeLabel> edge: inEdges) {
            GraphNode<ConceptLabel, ConceptRelationEdgeLabel> sourceNode = (GraphNode<ConceptLabel, ConceptRelationEdgeLabel>) edge.getSourceNode();
            computeNodeColor(sourceNode, hue, saturation-10, bright-10);
        }
    }

    private IntegerHSB getColorForConcept(String concept) throws IM3Exception {
        IntegerHSB result = conceptColors.get(concept);
        if (result == null) {
            throw new IM3Exception("Cannot find a color for concept " + concept);
        }
        return result;
    }
    private IntegerHSB computeColor(GraphNode<?,ConceptRelationEdgeLabel> sourceNode, double hue, int saturation, int bright) throws IM3Exception {
        if (sourceNode.getOutEdges() == null || sourceNode.getOutEdges().size() < 2) {
            return new IntegerHSB((int)hue, saturation, bright);
        } else if (sourceNode.getOutEdges().size() == 2) {
            Iterator<GraphEdge<ConceptRelationEdgeLabel>> iterator = sourceNode.getOutEdges().iterator();
            GraphEdge<ConceptRelationEdgeLabel> edge1 = iterator.next();
            GraphEdge<ConceptRelationEdgeLabel> edge2 = iterator.next();

            // get the middle point between connecting nodes
            IntegerHSB color1 = getColorForConcept(edge1.getTargetNode().getLabel().toString());
            IntegerHSB color2 = getColorForConcept(edge2.getTargetNode().getLabel().toString());

            hue = (color1.getHue() + color2.getHue()) / 2;
            System.out.println(color1.getHue() + " " + color2.getHue() + "->" + hue);
            return new IntegerHSB((int)hue, saturation, bright);
        } else {
            throw new UnsupportedOperationException("TO-DO, more than 2 edges");
        }
    }
}
