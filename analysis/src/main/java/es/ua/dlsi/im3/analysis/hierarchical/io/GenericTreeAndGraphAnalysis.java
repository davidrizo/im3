package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.core.adt.IADT;

public class GenericTreeAndGraphAnalysis {
	IADT dataStructure;
	String type;
	
	public GenericTreeAndGraphAnalysis(String type, IADT dataStructure) {
		super();
		this.type = type;
		this.dataStructure = dataStructure;
	}
	public IADT getDataStructure() {
		return dataStructure;
	}
	public void setDataStructure(IADT dataStructure) {
		this.dataStructure = dataStructure;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
