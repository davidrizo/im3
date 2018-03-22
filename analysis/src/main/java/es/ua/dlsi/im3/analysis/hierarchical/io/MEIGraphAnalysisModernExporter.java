package es.ua.dlsi.im3.analysis.hierarchical.io;

import java.util.ArrayList;

import es.ua.dlsi.im3.analysis.hierarchical.GraphAnalysis;
import es.ua.dlsi.im3.core.adt.graph.*;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

public class MEIGraphAnalysisModernExporter extends MEIHierarchicalAnalysisModernExporter<GraphAnalysis<?, ?>> {
	public MEIGraphAnalysisModernExporter(String meiType) {
		super(meiType);
	}

	@Override
	public void process(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, GraphAnalysis<?, ?> analysis) throws ExportException {
		processGraph(meiExporter, sb, tabs, analysis.getGraph(), getMeiType());
	}

	private void processGraph(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, DirectedGraph<?, ?> graph, String type) throws ExportException {
		XMLExporterHelper.start(sb, tabs, "graph", "type", type);
		ArrayList<GraphEdge<?>> edges = new ArrayList<>();
		for (GraphNode<?, ?> node: graph.getNodes()) {
			XMLExporterHelper.start(sb, tabs+1, "node", "xml:id", node.getID());
			if (node.getLabel() != null) {
				processNodeLabel(meiExporter, sb, tabs+2, node.getLabel());
			}
			if (node.getOutEdges() != null) {
				edges.addAll(node.getOutEdges());
			}
			XMLExporterHelper.end(sb, tabs+1, "node"); 
		}
		
		for (GraphEdge<?> edge: edges) {
			String from = edge.getSourceNode().getID();
			String to = edge.getTargetNode().getID();
			if (edge.getLabel() == null) {
				XMLExporterHelper.startEnd(sb, tabs+1, "arc", "xml:id", edge.getID(), "from" , from, "to", to);
			} else {
				XMLExporterHelper.start(sb, tabs+1, "arc", "xml:id", edge.getID(), "from" , from, "to", to);
				processEdgeLabel(meiExporter, sb, tabs+2, edge.getLabel());
				XMLExporterHelper.end(sb, tabs+1, "arc");
			}
		}
		
		XMLExporterHelper.end(sb, tabs, "graph");
	}

	private void processNodeLabel(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, INodeLabel label) throws ExportException {
		IOFactory.getInstance().getExporter(label.getClass()).process(meiExporter, sb, tabs, label);
	}

	private void processEdgeLabel(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, IEdgeLabel label) throws ExportException {
		IOFactory.getInstance().getExporter(label.getClass()).process(meiExporter, sb, tabs, label);
	}

}
