package es.ua.dlsi.im3.analysis.hierarchical.io;


import es.ua.dlsi.im3.core.io.ExportException;

public interface IMEIComponentExporter<ObjectToExport> {
	void process(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, ObjectToExport objectToExport) throws ExportException;
}
