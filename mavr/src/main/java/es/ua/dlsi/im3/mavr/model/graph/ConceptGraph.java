package es.ua.dlsi.im3.mavr.model.graph;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.DirectedGraph;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Graph containing relations between concepts. Linked nodes are related.
 * @param <ConceptType> Must implement hashCode
 */
public class ConceptGraph<ConceptType> {
    DirectedGraph<ConceptLabel, ConceptRelationEdgeLabel> graph;
    HashMap<ConceptType, GraphNode<ConceptLabel, ConceptRelationEdgeLabel>> conceptNodes;

    public ConceptGraph() {
        graph = new DirectedGraph<>(false);
        conceptNodes = new HashMap<>();
    }

    public void addConcept(ConceptType conceptType) throws IM3Exception {
        GraphNode<ConceptLabel, ConceptRelationEdgeLabel> node = new GraphNode<ConceptLabel, ConceptRelationEdgeLabel>(graph, new ConceptLabel(conceptType));
        graph.addNode(node);
        conceptNodes.put(conceptType, node);
    }

    public GraphNode<ConceptLabel, ConceptRelationEdgeLabel> getNode(ConceptType conceptType) throws IM3Exception {
        GraphNode<ConceptLabel, ConceptRelationEdgeLabel> result = conceptNodes.get(conceptType);
        if (result == null) {
            throw new IM3Exception("Cannot find a node for concept " + conceptType);
        }
        return result;
    }

    public void relateConcepts(ConceptType from, ConceptType to, double percentage) throws IM3Exception {
        GraphNode<ConceptLabel, ConceptRelationEdgeLabel> fromNode = getNode(from);
        GraphNode<ConceptLabel, ConceptRelationEdgeLabel> toNode = getNode(to);
        fromNode.addEdge(new GraphEdge<>(fromNode, toNode, new ConceptRelationEdgeLabel(percentage)));
    }

    public DirectedGraph<ConceptLabel, ConceptRelationEdgeLabel> getGraph() {
        return graph;
    }

}
