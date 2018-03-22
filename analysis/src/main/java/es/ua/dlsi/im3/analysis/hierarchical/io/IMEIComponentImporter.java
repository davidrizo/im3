package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.core.io.ImportException;

public interface IMEIComponentImporter<SourceObjectType, TargetObjectType> {
	public TargetObjectType importFrom(SourceObjectType source, MEIHierarchicalAnalysesModernImporter importer) throws ImportException;
}
