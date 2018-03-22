package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.analysis.hierarchical.GraphAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.core.score.ScoreSong;

/**
 * Actually, this graph is a set of unconnected graphs connected by a common startNode
 * @author drizo
 */
public class MelodicMotivesAnalysis extends GraphAnalysis<MelodicMotiveNodeLabel, MotivesEdgeLabel> {
	
	public MelodicMotivesAnalysis(ScoreSong song) throws IM3Exception {
		super(song);
	}

	public void addMotive(MelodicMotive motive) throws IM3Exception {
		GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> node = createMotiveNode(null, motive);
		
		GraphEdge<MotivesEdgeLabel> edge =
				new GraphEdge<>(hierarchicalStructure.getStartNode(), node, null);
		hierarchicalStructure.getStartNode().addEdge(edge);
		
	}
	
	/**
	 * Used by the XML importer. 
	 * @param nodeID
	 * @param motive
	 * @return 
	 * @throws IM3Exception 
	 */
	public GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> createMotiveNode(String nodeID, MelodicMotive motive) throws IM3Exception {
		MelodicMotiveNodeLabel nodeLabel = new MelodicMotiveNodeLabel(motive);  
		GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> node;
		
		if (nodeID == null) {
			node = new GraphNode<>(hierarchicalStructure, nodeLabel);
		} else {
			node = new GraphNode<>(hierarchicalStructure, nodeID, nodeLabel);
		}
		motive.setContainedInNode(node);
		hierarchicalStructure.addNode(node);		
		return node;
	}
	

	public void connect(MelodicMotive from, MelodicMotive to, String edgeLabel) {
		MotivesEdgeLabel label = new MotivesEdgeLabel(); //TODO
		label.setDescription(edgeLabel);
		GraphEdge<MotivesEdgeLabel> edge = new GraphEdge<>(from.getContainedInNode(), to.getContainedInNode(), label);
		from.getContainedInNode().addEdge(edge);
	}

	/*public ArrayList<Motive> getMotives() {
		ArrayList<Motive> result = new ArrayList<>();
		for (GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> node: this.hierarchicalStructure.getNodes()) {
			if (node.getLabel() instanceof MotiveNodeLabel) {
				MelodicMotiveNodeLabel mnl = node.getLabel();
				result.add(mnl.getMotive());
			}
		}
		return result;
	}*/

}
