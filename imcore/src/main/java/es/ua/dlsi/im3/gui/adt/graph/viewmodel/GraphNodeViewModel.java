package es.ua.dlsi.im3.gui.adt.graph.viewmodel;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.core.score.layout.graphics.Rectangle;

import java.util.ArrayList;
import java.util.Collection;

public class GraphNodeViewModel<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	GraphNode<LabelNodeType, LabelEdgeType> graphNode;
	ArrayList<GraphEdgeViewModel<LabelNodeType, LabelEdgeType>> outEdges;
	LabelViewModel labelViewModel;
	Rectangle rectangle;
	boolean visible = true;
	private DirectedGraphViewModel<LabelNodeType, LabelEdgeType> graphViewModel;
	
	public GraphNodeViewModel(DirectedGraphViewModel<LabelNodeType, LabelEdgeType> graphViewModel, GraphNode<LabelNodeType, LabelEdgeType> graphNode) throws IM3Exception {
		super();
		this.graphViewModel = graphViewModel;
		this.graphNode = graphNode;
		this.rectangle = new Rectangle(null, null, null, null); // TODO: 1/5/18
		if (graphNode.getLabel() != null) {
			try {
				labelViewModel = (LabelViewModel) GraphViewModelFactory.getInstance().createViewModelFor(graphNode.getLabel());
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new IM3Exception(e);
			}
		}
	}

	public double getX() {
        return rectangle.getFrom().getAbsoluteX();
    }

	public void setX(double x) {
		throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}

	public double getY() throws IM3Exception {
		return rectangle.getFrom().getAbsoluteY();
	}

	public void setY(double y) {
        throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}

	public double getWidth() {
		return rectangle.getWidth();
	}

	public void setWidth(double width) {
        throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}

	public double getHeight() throws IM3Exception {
		return rectangle.getHeight();
	}

	public void setHeight(double height) {
        throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}


	
	public void addOutgoingEdge(GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edge) {
		if (this.outEdges == null) {
			this.outEdges = new ArrayList<>();
		}
		this.outEdges.add(edge);
	}

	public Collection<GraphEdgeViewModel<LabelNodeType, LabelEdgeType>> getOutEdges() {
		return outEdges;
	}
	
	public LabelViewModel getLabel() {
		return labelViewModel;
	}

	public GraphNode<LabelNodeType, LabelEdgeType> getGraphNode() {
		return graphNode;
	}

	public void hide() {
		visible = false;
	}

	public boolean isVisible() {
		return visible;
	}

	public GraphEdgeViewModel<LabelNodeType, LabelEdgeType> connectTo(GraphNodeViewModel<LabelNodeType, LabelEdgeType> toNodeViewModel, LabelEdgeType label) throws IM3Exception {
		GraphEdge<LabelEdgeType> edge = this.graphNode.connect(toNodeViewModel.getGraphNode(), label);
		return graphViewModel.onEdgeAdded(edge);
	}
	
}
