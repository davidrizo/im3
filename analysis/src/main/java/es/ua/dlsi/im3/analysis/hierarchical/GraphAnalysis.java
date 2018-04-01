package es.ua.dlsi.im3.analysis.hierarchical;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.DirectedGraph;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.core.score.ScoreSong;

/**
 * Leaves must implement ITimedElement
 * @author drizo
 *
 * @param <LabelNodeType>
 */
public class GraphAnalysis<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> extends HierarchicalAnalysis<DirectedGraph<LabelNodeType, LabelEdgeType>>{
	public GraphAnalysis(ScoreSong song) throws IM3Exception {
		super(song);
		hierarchicalStructure = new DirectedGraph<>();
	}

	public DirectedGraph<LabelNodeType, LabelEdgeType> getGraph() {
		return hierarchicalStructure;
	}	
}
