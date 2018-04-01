package es.ua.dlsi.im3.analysis.hierarchical.motives;


import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;

public class MotivesEdgeLabel implements IEdgeLabel {
	String description;
	
	@Override
	public boolean equals(IEdgeLabel e) {
		return super.equals(e);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
