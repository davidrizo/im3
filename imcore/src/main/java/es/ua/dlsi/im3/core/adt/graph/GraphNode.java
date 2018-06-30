package es.ua.dlsi.im3.core.adt.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import es.ua.dlsi.im3.core.IDGenerator;


public class GraphNode<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	LabelNodeType label;
	HashSet<GraphEdge<LabelEdgeType>> outedges;
	String ID;
	private DirectedGraph<LabelNodeType, LabelEdgeType> graph;
    private String hexaColor;

    public GraphNode(DirectedGraph<LabelNodeType, LabelEdgeType> graph, LabelNodeType label) {
		super();
		this.graph = graph;
		this.label = label;
		this.ID = "GN" + IDGenerator.getID();
	}

	public GraphNode(DirectedGraph<LabelNodeType, LabelEdgeType> graph, String ID, LabelNodeType label) {
		super();
		this.graph = graph;
		this.ID = ID;
		this.label = label;
	}
	
	public String getID() {
		return ID;
	}

	public Set<GraphEdge<LabelEdgeType>> getOutEdges() {
		return outedges;
	}

    public Collection<GraphEdge<LabelEdgeType>> getInEdges() {
        LinkedList<GraphEdge<LabelEdgeType>> result = new LinkedList<>();
        //TODO Optimizar esto?
        for (GraphEdge<LabelEdgeType> edge: graph.getEdges()) {
            if (edge.getTargetNode() == this) {
                result.add(edge);
            }
        }
        return result;
    }
	
	public void addEdge(GraphEdge<LabelEdgeType> edge) {
		if (outedges == null) {
			outedges = new HashSet<>();
		}
		outedges.add(edge);
		graph.onEdgeAdded(edge);
	}
	
	public void removeEdge(GraphEdge<LabelEdgeType> edge) {
		outedges.remove(edge);
		graph.onEdgeRemoved(edge);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode<?,?> other = (GraphNode<?,?>) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

	public LabelNodeType getLabel() {
		return label;
	}

	@SuppressWarnings("unchecked")
	public GraphEdge<LabelEdgeType> connect(GraphNode<?, ?> target, LabelEdgeType label) {
		GraphEdge<LabelEdgeType> edge = new GraphEdge<LabelEdgeType>(this, (GraphNode<?, LabelEdgeType>) target, label);
		addEdge(edge);	
		return edge;
	}

    public void setColor(String hexaColor) {
	    this.hexaColor = hexaColor;
    }

    public String getHexaColor() {
        return hexaColor;
    }
}
