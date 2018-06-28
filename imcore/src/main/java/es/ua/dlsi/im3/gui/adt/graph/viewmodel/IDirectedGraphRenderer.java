package es.ua.dlsi.im3.gui.adt.graph.viewmodel;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.DirectedGraph;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;

/**
 * It organizes the layout of nodes and edges and stores the
 * coordinate values in the view model
 * @author drizo
 */
public interface IDirectedGraphRenderer<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	DirectedGraphViewModel<LabelNodeType, LabelEdgeType> render(DirectedGraph<LabelNodeType, LabelEdgeType> graph, boolean drawEdges) throws IM3Exception;
	void render(GraphNodeViewModel<LabelNodeType, LabelEdgeType> nodeVM);
	void render(GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edgeVM);
}
