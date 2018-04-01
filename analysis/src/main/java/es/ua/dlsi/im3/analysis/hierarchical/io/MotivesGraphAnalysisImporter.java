package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotiveNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MotivesEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.io.ImportException;

import java.util.ArrayList;



public class MotivesGraphAnalysisImporter implements IMEIComponentImporter<Tree<AttributesLabel>, MelodicMotivesAnalysis> {
	@Override
	public MelodicMotivesAnalysis importFrom(Tree<AttributesLabel> source, MEIHierarchicalAnalysesModernImporter importer) throws ImportException {
		try {
			// the name will be given later by the general importer
			MelodicMotivesAnalysis result = new MelodicMotivesAnalysis(importer.getScoreSong());
			
			// import nodes
			if (!"graph".equals(source.getLabel().getTag())) {
				throw new ImportException("Expected tag graph for root of subXML and found '" + source.getLabel().getTag() + "'");
			}
			String type = source.getLabel().getAttribute("type");
			if (!"motives".equals(type)) {
				throw new ImportException("Expected type 'motives' for root of subXML and found '" + type + "'");
			}

			// separate first nodes from edges
			ArrayList<Tree<AttributesLabel>> nodes = new ArrayList<>();
			ArrayList<Tree<AttributesLabel>> edges = new ArrayList<>();
			for (Tree<AttributesLabel> child: source.getChildren()) {
				if ("node".equals(child.getLabel().getTag())) {
					nodes.add(child);
				} else if ("arc".equals(child.getLabel().getTag())) {
					edges.add(child);
				} else {
					throw new ImportException("Expected tag 'node' or 'arc' and found '" + child.getLabel().getTag());
				}
			}
			
			String newStartNodeID = result.getGraph().getStartNode().getID(); // the one just created
			String importedStartNodeID = null;
			// first import nodes as motives, then edges
			for (Tree<AttributesLabel> nodeTree : nodes) {	
				if (nodeTree.getNumChildren() == 1) {
					Tree<AttributesLabel> firstChild = nodeTree.getChild(0);
					type = firstChild.getLabel().getAttribute("type");
					if (type == null) {
						throw new ImportException("Expecting a type attribute for node tree of tag " 
								+ firstChild.getLabel().getTag() + " but none found");
						
					} else if ("start".equals(type)) {
						// this is the start node, the MotivesAnalysis has already created it
						importedStartNodeID = nodeTree.getLabel().getAttribute("xml:id");
					} else {
						//TODO ¿y si es otro tipo de motivo?
						MelodicMotive motive = (MelodicMotive) IOFactory.getInstance().getImporter(type).importFrom(firstChild, importer);
						result.createMotiveNode(nodeTree.getLabel().getAttribute("xml:id"), motive);
					}
				}
			}
			
			for (Tree<AttributesLabel> nodeTree : edges) {				
				String fromID = nodeTree.getLabel().getAttribute("from");
				String toID = nodeTree.getLabel().getAttribute("to");
				if (fromID.equals(importedStartNodeID)) {
					fromID = newStartNodeID;
				}
				GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> fromNode = result.getGraph().getNode(fromID);
				GraphNode<MelodicMotiveNodeLabel, MotivesEdgeLabel> toNode = result.getGraph().getNode(toID);
				GraphEdge<MotivesEdgeLabel> edge = new GraphEdge<>(fromNode, toNode, null); //TODO poder añadir una etiqueta
				//System.out.println("INSERTING " + edge.getSourceNode() + " to " + edge.getTargetNode() + ", " + edge.hashCode());
				fromNode.addEdge(edge);
				//System.out.println("INSERTED"); 
			}
			
			return result;
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}


	/*private void importSection(FormAnalysis result, Tree<AttributesLabel> child,
			MEIHierarchicalAnalysesModernImporter importer, AbstractModelSong2ModernSong abstractModel2ModernSong) throws ImportException, NotationException, IM2Exception, TreeException {
		
		if (child.getNumChildren() != 1) {
			throw new ImportException("Expected just a child named 'label' of child " + child);
		}
		
		Tree<AttributesLabel> labelChild = child.getChild(0);
		if (!"label".equals(labelChild.getLabel().getTag())) {
			throw new ImportException("Expected child named 'label' of child " + child);
		}
		
		AttributesLabel attrLabel = labelChild.getLabel();
		String name = attrLabel.getAttribute("name");
		String measureid = attrLabel.getAttribute("measureid");
		String tstamp = attrLabel.getAttribute("tstamp");
		AMTimedElement measure = importer.findXMLID(measureid);
		double quarters = importer.decodeTStamp((AMMeasure)measure, tstamp);
		String description = attrLabel.getTextContent();
		result.addSection(name, (long) (AbstractSong.DEFAULT_RESOLUTION * quarters), description);
		
		//ScoreAnalysisHook<FiguresModern> scoreAnalysisHookStart = importer.getScoreSong().getAnalysisStaff().findAnalysisHookWithOnset((long) (quarters * AbstractSong.DEFAULT_RESOLUTION));
		//SectionLabel sectionLabel = new SectionLabel(name, scoreAnalysisHookStart);
		//Tree<SectionLabel> sectionTree = new Tree<SectionLabel>(sectionLabel);
		//return sectionTree;
	}*/

}
