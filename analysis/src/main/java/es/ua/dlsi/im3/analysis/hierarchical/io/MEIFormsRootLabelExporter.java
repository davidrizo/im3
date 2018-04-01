package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.forms.RootLabel;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

public class MEIFormsRootLabelExporter implements IMEIComponentExporter<RootLabel> {

	@Override
	public void process(MEIHierarchicalAnalysesModernExporter exporter, StringBuilder sb, int tabs, RootLabel label) throws ExportException {
		XMLExporterHelper.startEnd(sb, tabs, "label", "subtype", "root");

	}

}
