package es.ua.dlsi.im3.analysis.hierarchical.io;


import es.ua.dlsi.im3.analysis.hierarchical.motives.Motive;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MotiveNodeLabel;
import es.ua.dlsi.im3.core.io.ExportException;

public class MEIMotiveNodeLabelExporter implements IMEIComponentExporter<MotiveNodeLabel> {
	@Override
	public void process(MEIHierarchicalAnalysesModernExporter exporter, StringBuilder sb, int tabs, MotiveNodeLabel label) throws ExportException {
		//TODO Hacerlo sin instanceof - usando también el IOFactory		
		Motive motive = label.getMotive();
		IOFactory.getInstance().getExporter(motive.getClass()).
			process(exporter, sb, tabs, motive);
	}
}
