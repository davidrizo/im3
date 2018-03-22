package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.motives.MotivesEdgeLabel;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

public class MEIMotiveEdgeLabelExporter implements IMEIComponentExporter<MotivesEdgeLabel> {
	@Override
	public void process(MEIHierarchicalAnalysesModernExporter exporter, StringBuilder sb, int tabs, MotivesEdgeLabel label) throws ExportException {
		if (label.getDescription() != null && !label.getDescription().isEmpty()) {
			XMLExporterHelper.start(sb, tabs, "label");
			XMLExporterHelper.startEndTextContent(sb, tabs+1, "description", label.getDescription()); 
			XMLExporterHelper.end(sb, tabs, "label");
		} else {
			XMLExporterHelper.startEnd(sb, tabs, "label"); 
		}
		
	}


}
