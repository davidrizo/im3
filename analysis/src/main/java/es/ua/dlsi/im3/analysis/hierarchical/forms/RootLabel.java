package es.ua.dlsi.im3.analysis.hierarchical.forms;

import es.ua.dlsi.im3.core.score.Time;

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

    @Override
    public Time getTime() {
        return Time.TIME_ZERO;
    }

    @Override
    public Double getPredefinedHorizontalPosition() {
        return null;
    }

}
