package es.ua.dlsi.im3.analysis.hierarchical.forms;

public class RootLabel extends FormAnalysisTreeNodeLabel {

	public RootLabel() {
		super();
	}

	@Override
	public String getStringLabel() {
		return "Root";
	}

	@Override
	public String getColor() throws Exception {
		return null;
	}

	@Override
	public FormAnalysisTreeNodeLabel clone() {
		return new RootLabel();
	}

}
