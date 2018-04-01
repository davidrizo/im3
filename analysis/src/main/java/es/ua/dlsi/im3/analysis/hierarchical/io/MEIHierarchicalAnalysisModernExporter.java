package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.HierarchicalAnalysis;

public abstract class MEIHierarchicalAnalysisModernExporter<AnalysisType extends HierarchicalAnalysis<?>>
	implements IMEIComponentExporter<AnalysisType> {
	private String meiType;

	public MEIHierarchicalAnalysisModernExporter(String meiType) {
		this.meiType = meiType;
	}

	public String getMeiType() {
		return meiType;
	}
	
	
}
