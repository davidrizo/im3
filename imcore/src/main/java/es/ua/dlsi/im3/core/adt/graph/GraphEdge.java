package es.ua.dlsi.im3.core.adt.graph;

import es.ua.dlsi.im3.core.IDGenerator;

public class GraphEdge<LabelEdgeType extends IEdgeLabel> {
	LabelEdgeType label;
	GraphNode<?, LabelEdgeType> targetNode;
	String ID;
	private GraphNode<?, LabelEdgeType> sourceNode;
	
	public GraphEdge(GraphNode<?, LabelEdgeType> sourceNode, GraphNode<?, LabelEdgeType> targetNode, LabelEdgeType label) {
		super();
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.label = label;
		ID = "GE" +IDGenerator.getID();
	}

	public GraphEdge(String ID, GraphNode<?, LabelEdgeType> sourceNode, GraphNode<?, LabelEdgeType> targetNode, LabelEdgeType label) {
		super();
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.label = label;
		this.ID = ID;
	}
	
	public String getID() {
		return ID;
	}

	public LabelEdgeType getLabel() {
		return label;
	}
	
	public GraphNode<?, LabelEdgeType> getSourceNode() {
		return sourceNode;
	}

	public GraphNode<?, LabelEdgeType> getTargetNode() {
		return targetNode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((sourceNode == null) ? 0 : sourceNode.hashCode());
		result = prime * result + ((targetNode == null) ? 0 : targetNode.hashCode());
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
		GraphEdge<?> other = (GraphEdge<?>) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (sourceNode == null) {
			if (other.sourceNode != null)
				return false;
		} else if (!sourceNode.equals(other.sourceNode))
			return false;
		if (targetNode == null) {
            return other.targetNode == null;
		} else return targetNode.equals(other.targetNode);
    }

	
	
}
